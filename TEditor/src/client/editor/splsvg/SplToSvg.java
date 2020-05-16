package client.editor.splsvg;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import eventim.spl.managers.EventimManager;
import eventim.spl.models.EventimSeat;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

/**
 * Generates svg based on spl
 */
public class SplToSvg {
  private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  private static final Comparator<String> comparator1 = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
    }
  };
  private static final Comparator<Named.Row> comparator2 = new Comparator<Named.Row>() {
    @Override
    public int compare(Named.Row o1, Named.Row o2) {
      return Integer.compare(Integer.parseInt(o1.getName()), Integer.parseInt(o2.getName()));
    }
  };
  private static final String g = "g";
  private static final String circle = "circle";
  private static final String text = "text";
  private static final String title = "title";
  private static final String tspan = "tspan";
  private static final String transform = "transform";

  private SplToSvg() {
  }

  public static byte[] convert(@NotNull File spl) throws IOException, TransformerException, ParserConfigurationException {
    return convert(Files.readAllBytes(spl.toPath()));
  }

  public static byte[] convert(@NotNull byte[] splData) throws TransformerException, ParserConfigurationException {
    EventimManager eventimManager = EventimManager.read(splData);
    return convert(eventimManager.getSeatList());
  }

  public static byte[] convert(@NotNull List<EventimSeat> seatList) throws ParserConfigurationException, TransformerException {
    if (seatList.isEmpty()) return new byte[0];//todo не понятно что делать со схемами без мест с размещением
    // fill sector map
    Map<String, Named.Sector> sectorMap = new HashMap<>();

    for (EventimSeat seat : seatList) {
      String sector = seat.getSector();
      String row = seat.getRow();
      String seatName = seat.getSeat();

      if (!sectorMap.containsKey(sector)) {
        sectorMap.put(sector, new Named.Sector(sector, new HashMap<String, Named.Row>()));
      }

      Map<String, Named.Row> rowsMap = sectorMap.get(sector).getRows();
      if (!rowsMap.containsKey(row)) {
        rowsMap.put(row, new Named.Row(row, new HashSet<String>()));
      }

      Set<String> seatsSet = rowsMap.get(row).getSeats();
      seatsSet.add(seatName);
    }

    // take document and transform to byte array
    Document document = convert(sectorMap, seatList);
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(4));
    DOMSource domSource = new DOMSource(document);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    StreamResult result = new StreamResult(byteArrayOutputStream);
    transformer.transform(domSource, result);
    return byteArrayOutputStream.toByteArray();
  }

  private static Document convert(Map<String, Named.Sector> sectors, List<EventimSeat> seatList) throws ParserConfigurationException {
    // 0. determine sizes and x,y translates between sectors
    SvgModel model = new SvgModel(sectors);

    // 1. init document and root svg
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element rootElement = layoutG(document, model);

    // 2. set legend nodes
    if (sectors.size() <= 8 && seatList.size() <= 1000) {
      setLegend(document, rootElement, true);
      setPrices(document, rootElement, true);
    } else {
      setLegend(document, rootElement, false);
      setPrices(document, rootElement, false);
    }

//    setLegend(document, rootElement, false);
//    setPrices(document, rootElement, false);


    // 3. set sectors nodes
    setSeats(document, rootElement, model);

    return document;
  }

  private static void setSeats(Document document, Element rootSvg, SvgModel model) {
    String radius = String.valueOf(SvgSector.seatRadius);
    for (int i = 0; i < model.rows(); i++) {
      List<SvgSector> sectorsRow = model.row(i);

      Colors[] colors = Colors.values();
      int totalSeatRows = model.totalSeatRows();

      int step = totalSeatRows / colors.length;
      if (totalSeatRows % colors.length != 0) step += 1;
      Map<Integer, Colors> rowColors = new HashMap<>();
      for (int j = 0; j < totalSeatRows; j++) {
        rowColors.put(j, colors[j / step]);
      }

      for (SvgSector svgSector : sectorsRow) {
        Named.Sector sector = svgSector.getSector();
        Map<String, Named.Row> rowsMap = sector.getRows();
        List<Named.Row> rows = tryToOrderRows(rowsMap);
        // todo determine in which svg group set sector title
        // todo determine in which svg group set rows names

        Element rowNumbersG = document.createElement(g);

        // append sector name title
        Element sectorNameText = document.createElement(text);
        if (sector.getName().toLowerCase().startsWith("сектор")) {
          String[] str = sector.getName().split(" ");
          String nameSector = str[1];
          sectorNameText.setTextContent(nameSector);
          sectorNameText.setAttribute("x", String.valueOf(svgSector.getxOffset() + SvgModel.legendXOffset + svgSector.getWidth() / 3));
          sectorNameText.setAttribute("y", String.valueOf(svgSector.getyOffset() + SvgModel.topOffset));
          sectorNameText.setAttribute("style", "font-size:14px;line-height:1.0;line-height:125%;font-family:sans-serif;text-align:start;fill:#000000;stroke:none;stroke-width:0.12120575px");
          rootSvg.appendChild(sectorNameText);

          // append sector seats
          double currentY = SvgSector.titleHeight + SvgSector.seatDiameter;
          for (int j = 0; j < rows.size(); j++) {
            Element rowG = document.createElement(g);
            rowG.setAttribute("inkscape:label", "#" + nameSector.trim());
            rowG.setAttribute("transform", "translate(" + (svgSector.getxOffset() + SvgModel.legendXOffset) + "," + (svgSector.getyOffset() + SvgModel.topOffset) + ")");
            Element rowTitle = document.createElement(title);
            rowTitle.setTextContent("Ряд " + rows.get(j).getName());
            rowG.appendChild(rowTitle);

            int absoluteRow = model.absoluteSeatRow(i, j);

            // initialize seats
            Named.Row currentRow = rows.get(j);
            List<String> seats = tryToOrderSeats(currentRow);
            double currentX = SvgSector.spaceAfterRowNumbers;
            for (String seat : seats) {
              Element seatCircle = document.createElement(circle);
              seatCircle.setAttribute("r", radius);
              seatCircle.setAttribute("cx", String.valueOf(currentX));
              seatCircle.setAttribute("cy", String.valueOf(currentY));
              seatCircle.setAttribute("inkscape:label", "#");
              seatCircle.setAttribute("style", "fill:#" + rowColors.get(absoluteRow).hex + ";stroke:#000000;stroke-width:0.14883965");

              Element seatTitle = document.createElement(title);
              seatTitle.setTextContent(seat);
              seatCircle.appendChild(seatTitle);

              currentX += SvgSector.seatDiameter + SvgSector.spaceBetweenSeats;
              rowG.appendChild(seatCircle);
            }

            // set row number
            Element rowNumberText = document.createElement("text");
            rowNumberText.setTextContent(currentRow.getName());
            rowNumberText.setAttribute("x", String.valueOf(svgSector.getxOffset() + SvgModel.legendXOffset));
            rowNumberText.setAttribute("y", String.valueOf(svgSector.getyOffset() + SvgModel.topOffset + currentY + SvgSector.rowNumberYOffset));
            rowNumberText.setAttribute("style", "font-size:10px;line-height:125%;font-family:sans-serif;text-align:start;fill:#000000;stroke:none;stroke-width:0.26458332px");
            rowNumbersG.appendChild(rowNumberText);

            currentY += SvgSector.seatDiameter + SvgSector.spaceBetweenRows;
            rootSvg.appendChild(rowG);
          }

          rootSvg.appendChild(rowNumbersG);
        } else {
          sectorNameText.setTextContent(sector.getName());
          sectorNameText.setAttribute("x", String.valueOf(svgSector.getxOffset() + SvgModel.legendXOffset + svgSector.getWidth() / 3));
          sectorNameText.setAttribute("y", String.valueOf(svgSector.getyOffset() + SvgModel.topOffset));
          sectorNameText.setAttribute("style", "font-size:14px;line-height:1.0;line-height:125%;font-family:sans-serif;text-align:start;fill:#000000;stroke:none;stroke-width:0.12120575px");
          rootSvg.appendChild(sectorNameText);

          // append sector seats
          double currentY = SvgSector.titleHeight + SvgSector.seatDiameter;
          for (int j = 0; j < rows.size(); j++) {
            Element rowG = document.createElement(g);
            rowG.setAttribute("inkscape:label", "#" + sector.getName().trim());
            rowG.setAttribute("transform", "translate(" + (svgSector.getxOffset() + SvgModel.legendXOffset) + "," + (svgSector.getyOffset() + SvgModel.topOffset) + ")");
            Element rowTitle = document.createElement(title);
            rowTitle.setTextContent("Ряд " + rows.get(j).getName());
            rowG.appendChild(rowTitle);

            int absoluteRow = model.absoluteSeatRow(i, j);

            // initialize seats
            Named.Row currentRow = rows.get(j);
            List<String> seats = tryToOrderSeats(currentRow);
            double currentX = SvgSector.spaceAfterRowNumbers;
            for (String seat : seats) {
              Element seatCircle = document.createElement(circle);
              seatCircle.setAttribute("r", radius);
              seatCircle.setAttribute("cx", String.valueOf(currentX));
              seatCircle.setAttribute("cy", String.valueOf(currentY));
              seatCircle.setAttribute("inkscape:label", "#");
              seatCircle.setAttribute("style", "fill:#" + rowColors.get(absoluteRow).hex + ";stroke:#000000;stroke-width:0.14883965");

              Element seatTitle = document.createElement(title);
              seatTitle.setTextContent(seat);
              seatCircle.appendChild(seatTitle);

              currentX += SvgSector.seatDiameter + SvgSector.spaceBetweenSeats;
              rowG.appendChild(seatCircle);
            }

            // set row number
            Element rowNumberText = document.createElement("text");
            rowNumberText.setTextContent(currentRow.getName());
            rowNumberText.setAttribute("x", String.valueOf(svgSector.getxOffset() + SvgModel.legendXOffset));
            rowNumberText.setAttribute("y", String.valueOf(svgSector.getyOffset() + SvgModel.topOffset + currentY + SvgSector.rowNumberYOffset));
            rowNumberText.setAttribute("style", "font-size:10px;line-height:125%;font-family:sans-serif;text-align:start;fill:#000000;stroke:none;stroke-width:0.26458332px");
            rowNumbersG.appendChild(rowNumberText);

            currentY += SvgSector.seatDiameter + SvgSector.spaceBetweenRows;
            rootSvg.appendChild(rowG);
          }

          rootSvg.appendChild(rowNumbersG);
        }


      }
    }
  }

  private static void setLegend(Document document, Element rootSvg, Boolean check) {

    String textStyle = "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px;font-size:15px;line-height:1.25";
    double r = 13.5;
    double d = r * 2;
    double space = 5;
    double textOffset = 5;

    if (check.equals(true)) {
      r = 5;
      d = r * 2;
      space = 5;
      textOffset = 5;
    }

    String radius = String.valueOf(r);

    Element legendG = document.createElement(g);
    legendG.setAttribute("id", "Legend");
    legendG.setAttribute(transform, "translate(" + SvgModel.leftOffset + "," + SvgModel.topOffset + ")");

    Element soldCircle = document.createElement(circle);
    soldCircle.setAttribute("id", "Sold");
    soldCircle.setAttribute("r", radius);
    soldCircle.setAttribute("cy", String.valueOf(0));
    soldCircle.setAttribute("inkscape:label", "#Sold");
    soldCircle.setAttribute("style", "fill:#999999;stroke:#000000;stroke-width:1.4");
    Element soldText = document.createElement(text);
    soldText.setTextContent("- продано");
    soldText.setAttribute("x", String.valueOf(r + space));
    soldText.setAttribute("y", String.valueOf(textOffset));
    soldText.setAttribute("inkscape:label", "#Sold");
    soldText.setAttribute("style", "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px;font-size:23.84563828px;line-height:1.25");
    legendG.appendChild(soldCircle);
    legendG.appendChild(soldText);

    Element notInSaleCircle = document.createElement(circle);
    notInSaleCircle.setAttribute("id", "None");
    notInSaleCircle.setAttribute("r", radius);
    notInSaleCircle.setAttribute("cy", String.valueOf(d + space));
    notInSaleCircle.setAttribute("inkscape:label", "#None");
    notInSaleCircle.setAttribute("style", "fill:#ffffff;stroke:#000000;stroke-width:1.4");
    Element notInSaleText = document.createElement(text);
    notInSaleText.setTextContent("- нет в продаже");
    notInSaleText.setAttribute("x", String.valueOf(r + space));
    notInSaleText.setAttribute("y", String.valueOf(d + space + textOffset));
    notInSaleText.setAttribute("inkscape:label", "#None");
    notInSaleText.setAttribute("style", "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px;font-size:23.84563828px;line-height:1.25");
    legendG.appendChild(notInSaleCircle);
    legendG.appendChild(notInSaleText);

    Element bookedCircle = document.createElement(circle);
    bookedCircle.setAttribute("id", "Reserved");
    bookedCircle.setAttribute("r", radius);
    bookedCircle.setAttribute("cy", String.valueOf(2 * (d + space)));
    bookedCircle.setAttribute("inkscape:label", "#Reserved");
    bookedCircle.setAttribute("style", "fill:#4d4d4d;stroke:#000000;stroke-width:1.4");
    Element bookedText = document.createElement(text);
    bookedText.setTextContent("- забронировано");
    bookedText.setAttribute("x", String.valueOf(r + space));
    bookedText.setAttribute("y", String.valueOf(2 * (d + space) + textOffset));
    bookedText.setAttribute("inkscape:label", "#Reserved");
    bookedText.setAttribute("style", "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px;font-size:23.84563828px;line-height:1.25");
    legendG.appendChild(bookedCircle);
    legendG.appendChild(bookedText);

    Element myPlacesCircle = document.createElement(circle);
    myPlacesCircle.setAttribute("id", "MyTickets");
    myPlacesCircle.setAttribute("r", radius);
    myPlacesCircle.setAttribute("cy", String.valueOf(3 * (d + space)));
    myPlacesCircle.setAttribute("inkscape:label", "#MyTickets");
    myPlacesCircle.setAttribute("style", "fill:#ff00cc;stroke:#000000;stroke-width:1.4");
    Element myPlacesText = document.createElement(text);
    myPlacesText.setTextContent("- мои места");
    myPlacesText.setAttribute("x", String.valueOf(r + space));
    myPlacesText.setAttribute("y", String.valueOf(3 * (d + space) + textOffset));
    myPlacesText.setAttribute("inkscape:label", "#MyTickets");
    myPlacesText.setAttribute("style", "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px;font-size:23.84563828px;line-height:1.25");
    legendG.appendChild(myPlacesCircle);
    legendG.appendChild(myPlacesText);

    if (check.equals(true)) {
      soldText.setAttribute("style", textStyle);
      notInSaleText.setAttribute("style", textStyle);
      bookedText.setAttribute("style", textStyle);
      myPlacesText.setAttribute("style", textStyle);
    }

    rootSvg.appendChild(legendG);

  }

  private static void setPrices(Document document, Element rootSvg, Boolean check) {

    double r = 13.5;
    double d = r * 2;
    double space = 4.5;
    double textOffset = 5;

    if (check.equals(true)) {
      r = 5;
      d = r * 2;
      space = 5;
      textOffset = 5;
    }

    Colors[] values = Colors.values();
    Element pricesCategoryG = document.createElement(g);
    pricesCategoryG.setAttribute("id", "PriceCategory");
    pricesCategoryG.setAttribute("inkscape:label", "#PriceCategory");
    pricesCategoryG.setAttribute(transform, "translate(" + SvgModel.leftOffset + "," + (SvgModel.topOffset + 5 * (d + space)) + ")");

    Element pricesTextsG = document.createElement(text);
    pricesTextsG.setAttribute("id", "PriceCategoryText");
    pricesTextsG.setAttribute("style", "line-height:0%;font-family:sans-serif;fill:#000000;stroke:none;stroke-width:1px");
    pricesCategoryG.appendChild(pricesTextsG);

    double currentY = 0;
    for (int i = 0; i < values.length; i++) {
      Element priceCircle = document.createElement(circle);
      priceCircle.setAttribute("r", String.valueOf(r));
      priceCircle.setAttribute("cy", String.valueOf(currentY));
      priceCircle.setAttribute("inkscape:label", "#" + values[i].order);
      priceCircle.setAttribute("style", "fill:#" + values[i].hex + ";stroke:#000000;stroke-width:1.16546202");

      Element priceText = document.createElement(tspan);
      priceText.setTextContent("- " + (i + 1) + " руб.");
      priceText.setAttribute("x", String.valueOf(r + space));
      priceText.setAttribute("y", String.valueOf(currentY + textOffset));
      priceText.setAttribute("inkscape:label", "#" + values[i].order);
      if (check.equals(true)) {
        priceText.setAttribute("style", "font-size:15px;line-height:1.25");
      } else {
        priceText.setAttribute("style", "font-size:23.84563828px;line-height:1.25");
      }
      pricesCategoryG.appendChild(priceCircle);
      pricesTextsG.appendChild(priceText);
      currentY += d + space;
    }

    rootSvg.appendChild(pricesCategoryG);
  }

  private static Element layoutG(Document document, SvgModel model) {
    Element rootSvg = document.createElement("svg");
    rootSvg.setAttribute("id", "eventim_spl");

    // set namespaces
    rootSvg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
    rootSvg.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
    rootSvg.setAttribute("xmlns:cc", "http://creativecommons.org/ns#");
    rootSvg.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    rootSvg.setAttribute("xmlns:svg", "http://www.w3.org/2000/svg");
    rootSvg.setAttribute("xmlns:sodipodi", "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd");
    rootSvg.setAttribute("xmlns:inkscape", "http://www.inkscape.org/namespaces/inkscape");

    // set view settings
    rootSvg.setAttribute("height", String.valueOf(model.getHeight() * model.getScale()));
    rootSvg.setAttribute("width", String.valueOf(model.getWidth() * model.getScale()));
    rootSvg.setAttribute("viewBox", "0 0 " + (model.getWidth() * model.getScale()) + " " + (model.getHeight() * model.getScale()));

    Element scaleLayerG = document.createElement(g);
    scaleLayerG.setAttribute(transform, "scale(" + model.getScale() + ")");

    document.appendChild(rootSvg);
    rootSvg.appendChild(scaleLayerG);

    return scaleLayerG;
  }

  private static List<String> tryToOrderSeats(Named.Row currentRow) {
    boolean allNumbers = true;
    for (String seat : currentRow.getSeats()) {
      try {
        //todo возможно нужно проверять методом isDigits(), поскольку +12 и -34 проходят проверку
        Integer.parseInt(seat);
      } catch (NumberFormatException e) {
        allNumbers = false;
        break;
      }
    }

    List<String> seats = new ArrayList<>(currentRow.getSeats());
    if (allNumbers) {
      Collections.sort(seats, comparator1);
    }

    return seats;
  }

  private static List<Named.Row> tryToOrderRows(Map<String, Named.Row> rowsMap) {
    boolean allNumbers = true;
    for (String name : rowsMap.keySet()) {
      try {
        //todo возможно нужно проверять методом isDigits(), поскольку +12 и -34 проходят проверку
        Integer.parseInt(name);
      } catch (NumberFormatException e) {
        allNumbers = false;
        break;
      }
    }

    List<Named.Row> rows = new ArrayList<>(rowsMap.values());
    if (allNumbers) {
      Collections.sort(rows, comparator2);
    }

    return rows;
  }

  private static boolean isDigits(String str) {
    if (str == null || str.isEmpty()) return false;
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch < '0' || ch > '9') return false;
    }
    return true;
  }
}
