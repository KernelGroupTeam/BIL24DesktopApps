package common.svg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.08.15
 */
public class SVGPlanEvent extends SVGPlan {
  @NotNull
  private final Document document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final Element categoriesElement;
  @NotNull
  private final Map<Long, SVGCategoryEvent> categoryMap = new HashMap<>();
  @NotNull
  private final Map<Integer, AtomicBoolean> categoryUsedMap = new HashMap<>();
  @NotNull
  private final Map<Long, SVGSeatEvent> seatMap = new HashMap<>();
  private boolean editorSetSelected = false;

  public SVGPlanEvent(@NotNull byte[] svgData) throws IOException, SVGPlanException {
    this(svgData, false, null);
  }

  public SVGPlanEvent(@NotNull byte[] svgData, boolean exam, AtomicReference<byte[]> result) throws IOException, SVGPlanException {
    super(svgData);
    document = createDocument();

    Element svgElement = SVGParser.getUniqueChildElement(document, "svg");
    if (svgElement == null) throw new SVGPlanException("<svg> is not found");

    Element metaElement = SVGParser.getUniqueChildElement(svgElement, "metadata");
    if (metaElement == null) throw new SVGPlanException("<metadata> is not found");
    Element docElement = SVGParser.getUniqueChildElement(metaElement, "sbt:document");
    if (docElement == null) throw new SVGPlanException("<sbt:document> is not found");
    version = new BigDecimal(docElement.getAttribute("sbt:version"));
    Element categoriesElement = SVGParser.getUniqueChildElement(metaElement, "sbt:categories");
    if (categoriesElement == null) throw new SVGPlanException("<sbt:categories> is not found");
    this.categoriesElement = categoriesElement;

    Element labelListElement = SVGParser.getElementById(document, "g", ID_CATEGORY);
    if (labelListElement == null) throw new SVGPlanException("Список цен не найден");
    Element priceListElement = SVGParser.getElementById(labelListElement, "text", ID_CATEGORY_TEXT);
    if (priceListElement == null) throw new SVGPlanException("Список цен не найден");

    List<Element> catList = SVGParser.getChildElementList(categoriesElement, "sbt:category");
    for (Element catElement : catList) {
      String id = catElement.getAttribute("sbt:id");
      String index = catElement.getAttribute("sbt:index");
      Element priceElement = SVGParser.getUniqueElementByAttr(priceListElement, "sbt:id", id);
      if (priceElement == null) throw new SVGPlanException("price element id=" + id + " is not found");
      SVGCategoryEvent svgCategory;
      if (version.compareTo(VER_11) >= 0) {
        Element labelElement = SVGParser.getUniqueElementByAttr(labelListElement, "sbt:cat", index);
        if (labelElement == null) throw new SVGPlanException("price label element index=" + index + " is not found");
        svgCategory = new SVGCategoryEvent11(catElement, priceElement, labelElement);
      } else {
        svgCategory = new SVGCategoryEvent(catElement, priceElement);
      }
      categoryMap.put(svgCategory.getCategoryPriceId(), svgCategory);
      categoryUsedMap.put(svgCategory.getIndex(), new AtomicBoolean(false));
    }

    List<Element> seatList = SVGParser.getElementListByAttrPresent(document, "sbt:seat");
    for (Element seatElement : seatList) {
      SVGSeatEvent svgSeat = new SVGSeatEvent(seatElement);
      seatMap.put(svgSeat.getSeatId(), svgSeat);
    }
    if (exam) {//todo
      Element defsElement = SVGParser.getUniqueChildElement(svgElement, "defs");
      if (defsElement == null) throw new SVGPlanException("<defs> not found");
      Element styleElement = SVGParser.getUniqueChildElement(defsElement, "style");
      if (styleElement == null) throw new SVGPlanException("<style> not found");
      NodeList nodeList = styleElement.getChildNodes();
      int len = nodeList.getLength();
      CDATASection styleSection = null;
      for (int i = 0; i < len; i++) {
        Node childNode = nodeList.item(i);
        if (childNode.getNodeType() != Node.CDATA_SECTION_NODE) continue;
        if (styleSection == null) styleSection = (CDATASection) childNode;
        else throw new SVGPlanException("<style> not unique");
      }
      if (styleSection == null) throw new SVGPlanException("<style> not found");
      StringBuilder styles = new StringBuilder(styleSection.getData());

      int index = 0;
      while(true) {
        index = styles.indexOf(".cat", index);
        if (index == -1) break;
        int end = styles.indexOf("}", index);
        if (end == -1) throw new SVGPlanException("error");
        if (styles.substring(index, end).endsWith("stroke:#000000")) {//уже оптимизирована
          result.set(null);
          return;
        }
        styles.insert(end, ";stroke:#000000");
        index = end;
      }
      styleSection.setData(styles.toString());

      for (Element seatElement : seatList) {
        if (!seatElement.getTagName().equals("circle")) throw new SVGPlanException("seat is not <circle>");
        SVGParser.applyTransformCircle(seatElement);
        SVGParser.removeStyleByName(seatElement, "fill-rule");
        SVGParser.removeStyleByName(seatElement, "stroke");
      }

      List<Element> rowList = SVGParser.getElementListByAttrPresent(document, "sbt:row");
      for (Element rowElement : rowList) {
        if (!rowElement.getTagName().equals("g")) throw new SVGPlanException("row is not <g>");
        List<Element> circleList = SVGParser.getChildElementList(rowElement, "circle");
        if (circleList.isEmpty()) throw new SVGPlanException("circleList is empty");
        Set<String> gStrokeWidthSet = new HashSet<>();
        for (Element circleElement : circleList) {
          String strokeWidth = SVGParser.getStyleByName(circleElement, "stroke-width");
          if (strokeWidth == null) strokeWidth = "";
          gStrokeWidthSet.add(strokeWidth);
        }
        if (gStrokeWidthSet.size() == 1 && !gStrokeWidthSet.iterator().next().isEmpty()) {//все значения одинаковые и присутствуют
          String strokeWidth = gStrokeWidthSet.iterator().next();
          for (Element circleElement : circleList) {
            SVGParser.removeStyleByName(circleElement, "stroke-width");
          }
          SVGParser.setStyleByName(rowElement, "stroke-width", strokeWidth);
        }
        SVGParser.removeStyleByName(rowElement, "fill");
      }

      if (version.compareTo(VER_11) >= 0) {
//        Element labelListElement = SVGParser.getElementById(document, "g", ID_CATEGORY);
//        if (labelListElement == null) throw new SVGPlanException("Список цен не найден");
        List<Element> labelList = SVGParser.getElementListByAttrPresent(labelListElement, "sbt:cat");
        if (labelList.isEmpty()) throw new SVGPlanException("labelList is empty");
        for (Element labelElement : labelList) {
          if (!labelElement.getTagName().equals("circle")) throw new SVGPlanException("label is not <circle>");
          SVGParser.applyTransformCircle(labelElement);
          SVGParser.removeStyleByName(labelElement, "fill-rule");
          SVGParser.removeStyleByName(labelElement, "stroke");
        }
      }

      Element legendListElement = SVGParser.getElementById(document, "g", SVGPlan.ID_LEGEND);
      if (legendListElement == null) throw new SVGPlanException("Условные обозначения не найдены");
      List<Element> legendList = SVGParser.getChildElementList(legendListElement, "circle");
      if (legendList.isEmpty()) throw new SVGPlanException("legendList is empty");
      for (Element legendElement : legendList) {
        SVGParser.applyTransformCircle(legendElement);
      }

      removeDefaultStyles(document);
      byte[] bytes = toByteArray(document);
      result.set(bytes);
    }
  }

  @NotNull
  @Override
  public BigDecimal getVersion() {
    return version;
  }

  @Override
  public boolean isProcessed() {
    if (editorSetSelected) return false;
    for (SVGCategoryEvent svgCategory : categoryMap.values()) {
      if (!svgCategory.isDataSet()) return false;
    }
    for (SVGSeatEvent svgSeat : seatMap.values()) {
      if (!svgSeat.isDataSet()) return false;
    }
    return true;
  }

  @NotNull
  @Override
  public byte[] getProcessedSvgData() throws SVGPlanException {
    if (!isProcessed()) throw new SVGPlanException("document is not processed");
    return toByteArray(document);
  }

  @NotNull
  public byte[] getEditorSvgData() throws SVGPlanException {
    return toByteArray(document);
  }

  public void setCategoryData(long categoryPriceId, @NotNull String name, @NotNull BigDecimal price) throws SVGPlanException {
    SVGCategoryEvent svgCategory = categoryMap.get(categoryPriceId);
    if (svgCategory == null) throw new SVGPlanException("categoryPriceId=" + categoryPriceId + " is not found");
    svgCategory.setCategoryName(categoryPriceId, name);
    svgCategory.setCategoryPrice(categoryPriceId, price);
  }

  public void setSeatState(long seatId, int state, boolean my) throws SVGPlanException {
    SVGSeatEvent svgSeat = seatMap.get(seatId);
    if (svgSeat == null) throw new SVGPlanException("seatId=" + seatId + " is not found");
    svgSeat.setSeatState(seatId, state, my);
    if (state >= 1 && state <= 3) categoryUsedMap.get(svgSeat.getCatIndex()).set(true);
  }

  public void metaUnusedCategories() {//вызывать после всех setSeatState
    boolean sold = true;
    for (Map.Entry<Long, SVGCategoryEvent> entry : categoryMap.entrySet()) {
      SVGCategoryEvent svgCategory = entry.getValue();
      boolean used = categoryUsedMap.get(svgCategory.getIndex()).get();
      svgCategory.setCategoryUsed(used);
      if (used) sold = false;
    }
    categoriesElement.setAttribute("sbt:sold", (sold ? "1" : "0"));
  }

  public void hideUnusedCategories() throws SVGPlanException {//вызывать после всех setSeatState
    if (version.compareTo(VER_11) < 0) return;
    List<SVGCategoryEvent11> categoryList = new ArrayList<>();
    List<SVGCategoryEvent11> unusedCategoryList = new ArrayList<>();
    for (Map.Entry<Long, SVGCategoryEvent> entry : categoryMap.entrySet()) {
      SVGCategoryEvent11 svgCategory = (SVGCategoryEvent11) entry.getValue();
      boolean used = categoryUsedMap.get(svgCategory.getIndex()).get();
      if (used) categoryList.add(svgCategory);
      else unusedCategoryList.add(svgCategory);
    }
    if (!unusedCategoryList.isEmpty()) {
      Collections.sort(categoryList, new Comparator<SVGCategoryEvent11>() {
        @Override
        public int compare(SVGCategoryEvent11 o1, SVGCategoryEvent11 o2) {
          return Integer.compare(o1.getOrdinal(), o2.getOrdinal());
        }
      });
      categoryList.addAll(unusedCategoryList);
      List<SVGCategoryEvent11> categoryListByOrdinal = new ArrayList<>(categoryList);
      Collections.sort(categoryListByOrdinal, new Comparator<SVGCategoryEvent11>() {
        @Override
        public int compare(SVGCategoryEvent11 o1, SVGCategoryEvent11 o2) {
          return Integer.compare(o1.getOrdinal(), o2.getOrdinal());
        }
      });
      categoryMap.clear();
      for (int i = 0; i < categoryListByOrdinal.size(); i++) {
        SVGCategoryEvent11 ordinal = categoryListByOrdinal.get(i);
        SVGCategoryEvent11 sorted = categoryList.get(i);
        SVGCategoryEvent11 svgCategory = ordinal.updateFrom(sorted);
        categoryMap.put(svgCategory.getCategoryPriceId(), svgCategory);
        if (!categoryUsedMap.get(svgCategory.getIndex()).get()) svgCategory.hide();
      }
    }
  }

  public void setEditorSelected(long seatId) throws SVGPlanException {
    SVGSeatEvent svgSeat = seatMap.get(seatId);
    if (svgSeat == null) throw new SVGPlanException("seatId=" + seatId + " is not found");
    editorSetSelected = true;
    svgSeat.setSeatMy(seatId);
  }

  @NotNull
  public Map<Long, String> getSvgStringMap() throws SVGPlanException {
    Map<Long, String> result = new HashMap<>(seatMap.size());
    for (Map.Entry<Long, SVGSeatEvent> entry : seatMap.entrySet()) {
      result.put(entry.getKey(), entry.getValue().toEventimString());
    }
    return result;
  }
}
