package common.svg;

import java.io.*;
import java.math.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.08.15
 */
public class SVGPlanEditor extends SVGPlan {
  private static final int SIZE_LIMIT = 11 * 1024 * 1024;
  private static final int SIZE_LIMIT_FROM_SPL = 15 * 1024 * 1024;
  private static final int MAX_RATIO = 400;//байт на место
  private static final int WARN_RATIO = 250;//байт на место
  private static final String limitError = "Размер схемы после оптимизации превышает максимальный 8 МБ и составляет %.1f МБ";
  private static final String limitErrorSpl = "Размер схемы после оптимизации превышает максимальный 15 МБ и составляет %.1f МБ";
  private static final String ratioError = "Размер схемы превышает 400 байт/место и составляет %d байт/место";
  private static final String ratioWarn = "Размер схемы превышает норму 250 байт/место и составляет %d байт/место";
  private static final BigDecimal mb = new BigDecimal(1024 * 1024);
  @NotNull
  private final Document document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final SVGCategories categories;
  @NotNull
  private final Set<SVGSeat> seatSet = new HashSet<>();
  @NotNull
  private final StringBuilder report = new StringBuilder();
  private final boolean fromSpl;

  public SVGPlanEditor(File svgFile) throws IOException, SVGPlanException {
    this(Files.readAllBytes(svgFile.toPath()));
  }

  public SVGPlanEditor(@NotNull byte[] svgData) throws IOException, SVGPlanException {
    super(svgData);
    document = createDocument();
    version = VER_11;
    Element svgElement = SVGParser.getUniqueChildElement(document, "svg");
    if (svgElement == null) throw new SVGPlanException("<svg> is not found");
    String svgId = svgElement.getAttribute("id");
    fromSpl = svgId.equalsIgnoreCase("eventim_spl");
    String svgWidth = svgElement.getAttribute("width").replace("px", "");
    String svgHeight = svgElement.getAttribute("height").replace("px", "");
    if (svgWidth.isEmpty()) throw new SVGPlanException("<svg width> is not found");
    if (svgHeight.isEmpty()) throw new SVGPlanException("<svg height> is not found");
    try {
      BigDecimal max = new BigDecimal(3000);
      BigDecimal width = new BigDecimal(svgWidth);
      BigDecimal height = new BigDecimal(svgHeight);
      String err1 = "";
      String err2 = "";
      if (width.compareTo(max) > 0) err1 = "\nШирина страницы: " + width.toString() + " (больше 3000)";
      if (height.compareTo(max) > 0) err2 = "\nВысота страницы: " + height.toString() + " (больше 3000)";
      if (!fromSpl && (!err1.isEmpty() || !err2.isEmpty())) throw new SVGPlanException(err1 + err2);
    } catch (NumberFormatException e) {
      throw new SVGPlanException("Ширина или высота страницы задана неверно");
    }

    SVGLegend legend = new SVGLegend(document);
    categories = new SVGCategories(document, legend.getNoneColor(), true);
    addHeader(document, legend, categories);

    //ищем все места и распределяем по категориям
    NonDuplicateLine err = new NonDuplicateLine();
    Pattern pattern = Pattern.compile(" +");
    NodeList gList = document.getElementsByTagName("g");
    int gLen = gList.getLength();
    for (int i = 0; i < gLen; i++) {
      Element gElement = (Element) gList.item(i);
      String id = gElement.getAttribute("id");
      if (id.equals(ID_CATEGORY) || id.equals(ID_LEGEND)) continue;

      List<Element> circleList = SVGParser.getChildElementList(gElement, "circle");
      if (circleList.isEmpty()) continue;

      String sectorName = gElement.getAttribute("inkscape:label");
      if (sectorName.length() < 2) {
        err.appendLine("\nНазвание сектора не задано, id=" + id);
        continue;
      }
      sectorName = pattern.matcher(sectorName.substring(1).trim()).replaceAll(" ");
      if (sectorName.isEmpty()) {
        err.appendLine("\nНазвание сектора не задано, id=" + id);
        continue;
      }
      if (sectorName.toLowerCase().startsWith("сектор")) {
        err.appendLine("\nНазвание сектора задано неверно, id=" + id);
        continue;
      }

      String shortSectorName = null;
      Element descElement = SVGParser.getUniqueChildElement(gElement, "desc");
      if (descElement != null) {
        shortSectorName = pattern.matcher(descElement.getTextContent().trim()).replaceAll(" ");
        if (shortSectorName.isEmpty()) shortSectorName = null;
        gElement.removeChild(descElement);
      }

      Element rowTitle = SVGParser.getUniqueChildElement(gElement, "title");
      if (rowTitle == null) {
        err.appendLine("\nНазвание ряда не найдено, id=" + id);
        continue;
      }
      String rowName = pattern.matcher(rowTitle.getTextContent().trim()).replaceAll(" ");
      if (rowName.isEmpty()) {
        err.appendLine("\nНазвание ряда не задано, id=" + rowTitle.getAttribute("id"));
        continue;
      }
      if (!rowName.startsWith("Ряд ")) {
        err.appendLine("\nНазвание ряда задано неверно, id=" + rowTitle.getAttribute("id"));
        continue;
      }

      ArrayList<SVGSeat> gSeatList = new ArrayList<>();
      Set<String> gStrokeWidthSet = new HashSet<>();
      for (Element circleElement : circleList) {
        String strokeWidth = SVGParser.getStyleByName(circleElement, "stroke-width");
        if (strokeWidth == null) strokeWidth = "";
        gStrokeWidthSet.add(strokeWidth);
        Element seatTitle = SVGParser.getUniqueChildElement(circleElement, "title");
        if (seatTitle == null) {
          err.appendLine("\nНомер места не найден, id=" + circleElement.getAttribute("id"));
          continue;
        }
        String seatName = pattern.matcher(seatTitle.getTextContent().trim()).replaceAll(" ");
        if (seatName.isEmpty()) {
          err.appendLine("\nНомер места не задан, id=" + seatTitle.getAttribute("id"));
          continue;
        }
        String fillColor = SVGParser.getStyleByName(circleElement, "fill");
        if (fillColor == null || fillColor.equals("none")) {
          err.appendLine("\nЦвет места не найден, id=" + circleElement.getAttribute("id"));
          continue;
        }
        SVGCategory svgCategory;
        SVGColor color = new SVGColor(fillColor);
        if (color.equals(legend.getNoneColor())) svgCategory = categories.getNoneCategory();
        else svgCategory = categories.get(color);
        if (svgCategory == null) {
          err.appendLine("\nЦвет места не совпадает с категориями: " + color);
          continue;
        }
        SVGSeat svgSeat = new SVGSeat(circleElement, shortSectorName, sectorName, rowName.substring(4), seatName);
        if (!seatSet.add(svgSeat)) {
          err.appendLine("\nДублирующие координаты места: " + svgSeat);
          continue;
        }
        gSeatList.add(svgSeat);

        //место прошло все проверки
        svgCategory.incSeatsNumber();
        SVGParser.applyTransformCircle(circleElement);
        SVGParser.removeStyleByName(circleElement, "fill");
        SVGParser.removeStyleByName(circleElement, "fill-rule");
        SVGParser.removeStyleByName(circleElement, "fill-opacity");
        SVGParser.removeStyleByName(circleElement, "stroke");
        SVGParser.removeClassCat(circleElement);
        SVGParser.addClassName(circleElement, svgCategory.getClassName());
        if (!svgCategory.isNoneCat()) circleElement.setAttribute("sbt:cat", String.valueOf(svgCategory.getIndex()));
        circleElement.setAttribute("sbt:seat", seatName);
      }
      if (gStrokeWidthSet.size() == 1 && !gStrokeWidthSet.iterator().next().isEmpty()) {//все значения одинаковые и присутствуют
        String strokeWidth = gStrokeWidthSet.iterator().next();
        for (Element circleElement : circleList) {
          SVGParser.removeStyleByName(circleElement, "stroke-width");
        }
        SVGParser.setStyleByName(gElement, "stroke-width", strokeWidth);
      }
      if (!gSeatList.isEmpty()) {
        SVGParser.removeStyleByName(gElement, "fill");
        if (shortSectorName != null) gElement.setAttribute("sbt:short", shortSectorName);
        gElement.setAttribute("sbt:sect", sectorName);
        gElement.setAttribute("sbt:row", rowName);//sbt:row для совместимости хранит "Ряд 8"
      }
      if (gSeatList.size() > 1) {
        Collections.sort(gSeatList);
        SVGSeat prev = gSeatList.get(0);
        for (int j = 1; j < gSeatList.size(); j++) {
          SVGSeat svgSeat = gSeatList.get(j);
          if (svgSeat.getNumInt() == null || prev.getNumInt() == null) continue;
          if (svgSeat.getNumInt() - prev.getNumInt() != 1) {
            err.appendLine("\nВозможен пропуск места:\n" + prev + "\n" + svgSeat);
          }
          prev = svgSeat;
        }
      }
    }
    removeWhitespaces(document);
    removeDefaultStyles(document);
    removeIds(document);
    setIds(document);
    clean(document);

    //отчет
    int totalSeats = 0;
    for (SVGCategory svgCategory : categories.getCategoryList()) {
      report.append("Категория: ").append(svgCategory.getName()).append(", мест: ").append(svgCategory.getSeatsNumber());
      report.append(", цена: ");
      BigDecimal initPrice = svgCategory.getInitPrice();
      if (initPrice == null) report.append("<не задана>");
      else report.append(priceFormat(initPrice));
      report.append("\n");
      totalSeats += svgCategory.getSeatsNumber();
    }
    report.append("Всего доступных мест: ").append(totalSeats).append("\n");
    int noneNumber = categories.getNoneCategory().getSeatsNumber();
    if (noneNumber > 0) report.append("Всего мест в зале: ").append(totalSeats + noneNumber).append("\n");
    report.append("\nПотенциальные ошибки:");
    if (err.length() == 0) report.append("\n<не обнаружены>");
    else report.append(err);
  }

  @NotNull
  @Override
  public BigDecimal getVersion() {
    return version;
  }

  @Override
  public boolean isProcessed() {
    for (SVGCategory svgCategory : getCategoryList()) {
      if (!svgCategory.isObjectIdSet()) return false;
    }
    return true;
  }

  @NotNull
  @Override
  public byte[] getProcessedSvgData() throws SVGPlanException {
    if (!isProcessed()) throw new SVGPlanException("document is not processed");
    byte[] result = toByteArray(document);
    if (isSizeError(result.length, fromSpl)) throw new SVGPlanException(getSizeError(result.length, fromSpl));
    Integer ratio = getRatioError(result.length, seatSet.size());
    if (ratio != null) throw new SVGPlanException(getRatioError(ratio));
    return result;
  }

  private void addHeader(Document doc, SVGLegend legend, SVGCategories categories) throws SVGPlanException {
    Element svgElement = SVGParser.getUniqueChildElement(doc, "svg");
    if (svgElement == null) throw new SVGPlanException("<svg> is not found");
    svgElement.setAttribute("xmlns:sbt", "http://www.w3.org/2015/sbt/1.0");

    {//добавляем метаданные
      Element metaElement = SVGParser.getUniqueChildElement(svgElement, "metadata");
      if (metaElement == null) {
        metaElement = doc.createElement("metadata");
        svgElement.insertBefore(metaElement, svgElement.getFirstChild());
      }
      Element formatElement = doc.createElement("sbt:document");
      formatElement.setAttribute("sbt:version", "1.1");
      if (fromSpl) formatElement.setAttribute("sbt:source", "spl");
      metaElement.appendChild(formatElement);
      Element categoriesElement = doc.createElement("sbt:categories");
      for (SVGCategory svgCategory : categories.getCategoryList()) {
        Element catElement = doc.createElement("sbt:category");
        catElement.setAttribute("sbt:class", svgCategory.getClassName());
        catElement.setAttribute("sbt:color", CSS3ColorParser.toHash6Format(svgCategory.getColor().getColor()));
        catElement.setAttribute("sbt:index", String.valueOf(svgCategory.getIndex()));
        categoriesElement.appendChild(catElement);
        svgCategory.setMetaElement(catElement);
      }
      metaElement.appendChild(categoriesElement);
    }

    {//добавляем стили
      Element defsElement = SVGParser.getUniqueChildElement(svgElement, "defs");
      if (defsElement == null) {
        defsElement = doc.createElement("defs");
        svgElement.insertBefore(defsElement, svgElement.getFirstChild());
      }
      CDATASection styleSection;
      List<Element> styleList = SVGParser.getChildElementList(defsElement, "style");
      if (styleList.isEmpty()) {
        Element styleElement = doc.createElement("style");
        styleElement.setAttribute("type", "text/css");
        defsElement.appendChild(styleElement);
        styleSection = doc.createCDATASection("");
        styleElement.appendChild(styleSection);
      } else {
        Element styleElement = styleList.get(0);
        List<CDATASection> styleSectionList = SVGParser.getChildCDATAList(styleElement);
        if (styleSectionList.isEmpty()) {
          styleSection = doc.createCDATASection("");
          styleElement.appendChild(styleSection);
        } else styleSection = styleSectionList.get(0);
      }
      styleSection.setData(styleSection.getData() + "\n" + categories.getStyle() + legend.getStyle());
    }
  }

  private Document createEditorDocument() throws SVGPlanException {
    Document editorDoc = cloneDocument(document);
    Element textElement1 = SVGParser.getElementById(document, "text", ID_CATEGORY_TEXT);
    Element textElement2 = SVGParser.getElementById(editorDoc, "text", ID_CATEGORY_TEXT);
    if (textElement1 == null || textElement2 == null) throw new SVGPlanException("Список цен не найден");

    List<Element> priceList1 = SVGParser.getChildElementList(textElement1, "tspan");
    List<Element> priceList2 = SVGParser.getChildElementList(textElement2, "tspan");
    if (priceList1.size() != priceList2.size()) throw new IllegalStateException("unexpected error");

    List<SVGCategory> categoryList = categories.getCategoryList();
    for (int i = 0; i < priceList1.size(); i++) {
      Element priceElement1 = priceList1.get(i);
      Element priceElement2 = priceList2.get(i);
      for (SVGCategory svgCategory : categoryList) {
        Element priceElement = svgCategory.getPriceElement();
        if (priceElement == null) throw new IllegalStateException("price element is null");
        if (priceElement.isEqualNode(priceElement1) && svgCategory.getInitPrice() != null) {
          Text priceText = SVGParser.getUniqueChildText(priceElement2);
          if (priceText == null) throw new SVGPlanException("price place is not found");
          String priceTextStr = priceText.getNodeValue();
          priceTextStr = priceTextStr.replace(VAR_PRICE, priceFormat(svgCategory.getInitPrice()));
          priceText.setNodeValue(priceTextStr);
        }
      }
    }
    return editorDoc;
  }

  private Document createSelDocument() throws SVGPlanException {
    Document selDocument = cloneDocument(document);
    List<Element> seatList = SVGParser.getElementListByAttrPresent(selDocument, "sbt:seat");
    for (Element seatElement : seatList) {
      SVGParser.addClassName(seatElement, CLASS_STATE_SOLD);
    }
    Element textElement = SVGParser.getElementById(selDocument, "text", ID_CATEGORY_TEXT);
    if (textElement == null) throw new SVGPlanException("Список цен не найден");
    List<Element> priceList = SVGParser.getElementListByAttrPresent(textElement, "sbt:ordinal");
    for (Element priceElement : priceList) {
      int ordinal = Integer.parseInt(priceElement.getAttribute("sbt:ordinal"));
      Text priceText = SVGParser.getUniqueChildText(priceElement);
      if (priceText == null) throw new SVGPlanException("price is place not found");
      String priceTextStr = priceText.getNodeValue();
      priceTextStr = priceTextStr.replace(VAR_PRICE, "цена" + ordinal);
      priceText.setNodeValue(priceTextStr);
    }
    return selDocument;
  }

  public boolean isFromSpl() {
    return fromSpl;
  }

  @NotNull
  public byte[] getEditor1SvgData() throws SVGPlanException {
    return toByteArray(createEditorDocument());
  }

  @NotNull
  public byte[] getEditor2SvgData() throws SVGPlanException {
    return toByteArray(createSelDocument());
  }

  @NotNull
  public String getReport() {
    return report.toString();
  }

  @NotNull
  public List<SVGCategory> getCategoryList() {
    return categories.getCategoryList();
  }

  @NotNull
  public List<SVGSeat> getNotFoundSeatList(@NotNull List<String> eventimSeatStringList) {
    List<SVGSeat> notFoundSeatList = new ArrayList<>(seatSet);
    for (String eventimSeatString : eventimSeatStringList) {
      String eventimCompareString = eventimSeatString.replace(" ", "").toLowerCase();
      for (Iterator<SVGSeat> iterator = notFoundSeatList.iterator(); iterator.hasNext(); ) {
        SVGSeat svgSeat = iterator.next();
        String svgCompareString = svgSeat.toEventimString().replace(" ", "").toLowerCase();
        if (eventimCompareString.equals(svgCompareString)) {
          iterator.remove();//удаляем найденное место
          break;
        }
      }
    }
    return notFoundSeatList;
  }

  public int getSeatSize() {
    return seatSet.size();
  }

  @SuppressWarnings("RedundantIfStatement")
  public static boolean isSizeError(int bytes, boolean fromSpl) {
    if (fromSpl) {
      if (bytes > SIZE_LIMIT_FROM_SPL) return true;
    } else {
      if (bytes > SIZE_LIMIT) return true;
    }
    return false;
  }

  @NotNull
  public static String getSizeError(int bytes, boolean fromSpl) {
    BigDecimal size = new BigDecimal(bytes).divide(mb, 1, RoundingMode.HALF_UP);
    if (fromSpl) return String.format(limitErrorSpl, size);
    else return String.format(limitError, size);
  }

  @Nullable
  public static Integer getRatioError(int bytes, int seats) {
    int ratio = bytes / Math.max(seats, 1);
    if (ratio > MAX_RATIO && bytes > 100 * 1024) return ratio;
    return null;
  }

  @NotNull
  public static String getRatioError(int ratio) {
    return String.format(ratioError, ratio);
  }

  @Nullable
  public static Integer getRatioWarn(int bytes, int seats) {
    int ratio = bytes / Math.max(seats, 1);
    if (ratio > WARN_RATIO && bytes > 75 * 1024) return ratio;
    return null;
  }

  @NotNull
  public static String getRatioWarn(int ratio) {
    return String.format(ratioWarn, ratio);
  }

  private static class NonDuplicateLine {
    private final StringBuilder stringBuilder = new StringBuilder();
    private String lastStr = "";

    public boolean appendLine(String str) {
      if (str == null) return false;
      if (lastStr.equals(str)) return false;
      stringBuilder.append(str);
      lastStr = str;
      return true;
    }

    public int length() {
      return stringBuilder.length();
    }

    @Override
    public String toString() {
      return stringBuilder.toString();
    }
  }
}
