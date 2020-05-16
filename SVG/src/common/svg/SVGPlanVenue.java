package common.svg;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.08.15
 */
public class SVGPlanVenue extends SVGPlan {
  @NotNull
  private final Document document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final Map<Long, SVGCategoryVenue> categoryMap = new HashMap<>();
  @NotNull
  private final Set<SVGSeat> seatSet = new HashSet<>();//все места, включая недоступные с categoryId==null
  private boolean inaccessibleCategory = false;
  private boolean editorSetPrice = false;

  public SVGPlanVenue(@NotNull byte[] svgData) throws IOException, SVGPlanException {
    this(svgData, false, null);
  }

  public SVGPlanVenue(@NotNull byte[] svgData, boolean exam, AtomicReference<byte[]> result) throws IOException, SVGPlanException {
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
    Element priceListElement = SVGParser.getElementById(document, "text", ID_CATEGORY_TEXT);
    if (priceListElement == null) throw new SVGPlanException("Список цен не найден");
    List<Element> catList = SVGParser.getChildElementList(categoriesElement, "sbt:category");
    if (catList.isEmpty()) throw new SVGPlanException("Не найдено ни одной категории");
    Map<Integer, SVGCategoryVenue> categoryIndexMap = new HashMap<>();
    for (Element catElement : catList) {
      String id = catElement.getAttribute("sbt:id");
      Element priceElement = SVGParser.getUniqueElementByAttr(priceListElement, "sbt:id", id);
      if (priceElement == null) throw new SVGPlanException("price element id=" + id + " is not found");
      SVGCategoryVenue svgCategory = new SVGCategoryVenue(catElement, priceElement);
      categoryMap.put(svgCategory.getCategoryId(), svgCategory);
      categoryIndexMap.put(svgCategory.getIndex(), svgCategory);
    }

    List<Element> seatList = SVGParser.getElementListByAttrPresent(document, "sbt:seat");
    for (Element seatElement : seatList) {
      Long categoryId = null;
      if (seatElement.hasAttribute("sbt:cat")) {
        try {
          int index = Integer.parseInt(seatElement.getAttribute("sbt:cat"));
          SVGCategoryVenue svgCategory = categoryIndexMap.get(index);
          if (svgCategory == null) throw new SVGPlanException("sbt:cat index is not found");
          categoryId = svgCategory.getCategoryId();
        } catch (NumberFormatException e) {
          throw new SVGPlanException("sbt:cat format", e);
        }
      }
      if (!seatSet.add(new SVGSeat(seatElement, categoryId))) throw new SVGPlanException("duplicated seat");
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
        Element labelListElement = SVGParser.getElementById(document, "g", ID_CATEGORY);
        if (labelListElement == null) throw new SVGPlanException("Список цен не найден");
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
    if (!inaccessibleCategory) return false;
    if (editorSetPrice) return false;
    for (SVGCategoryVenue svgCategory : categoryMap.values()) {
      if (!svgCategory.isIdReplaced()) return false;
    }
    for (SVGSeat svgSeat : seatSet) {
      if (!svgSeat.isObjectIdSet()) return false;
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

  @NotNull
  public Color getCategoryColor(long categoryId) throws SVGPlanException {
    SVGCategoryVenue svgCategory = categoryMap.get(categoryId);
    if (svgCategory == null) throw new SVGPlanException("categoryId=" + categoryId + " is not found");
    return svgCategory.getColor();
  }

  public void replaceCategoryId(long categoryId, long categoryPriceId) throws SVGPlanException {
    SVGCategoryVenue svgCategory = categoryMap.get(categoryId);
    if (svgCategory == null) throw new SVGPlanException("categoryId=" + categoryId + " is not found");
    svgCategory.replaceCategoryId(categoryId, categoryPriceId);
  }

  @NotNull
  public TreeSet<SVGSeat> getSeatSet(long categoryId) {
    TreeSet<SVGSeat> result = new TreeSet<>();
    for (SVGSeat svgSeat : seatSet) {
      if (Objects.equals(svgSeat.getCategoryId(), categoryId)) result.add(svgSeat);
    }
    return result;
  }

  @NotNull
  public TreeSet<SVGSeat> getInaccessibleSeatSet() {
    TreeSet<SVGSeat> result = new TreeSet<>();
    for (SVGSeat svgSeat : seatSet) {
      if (svgSeat.getCategoryId() == null) result.add(svgSeat);
    }
    return result;
  }

  public void setInaccessibleCategory(long categoryId) throws SVGPlanException {
    SVGCategoryVenue svgCategory = categoryMap.get(categoryId);
    if (svgCategory == null) throw new SVGPlanException("categoryId=" + categoryId + " is not found");
    for (SVGSeat svgSeat : seatSet) {
      if (svgSeat.getCategoryId() == null) {
        Element seatElement = svgSeat.getSeatElement();
        seatElement.setAttribute("sbt:cat", String.valueOf(svgCategory.getIndex()));
        SVGParser.replaceClassName(seatElement, CLASS_CAT + "0", CLASS_CAT + svgCategory.getIndex());
      }
    }
    inaccessibleCategory = true;
  }

  public void setEditorPrice(long categoryId, @Nullable BigDecimal price) throws SVGPlanException {
    if (price == null) return;
    SVGCategoryVenue svgCategory = categoryMap.get(categoryId);
    if (svgCategory == null) throw new SVGPlanException("categoryId=" + categoryId + " is not found");
    editorSetPrice = true;
    svgCategory.setEditorPrice(categoryId, price);
  }
}
