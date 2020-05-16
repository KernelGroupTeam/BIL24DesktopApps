package common.svg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.08.15
 */
public class SVGPlanEventTuning extends SVGPlan {
  @NotNull
  private final Document document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final Map<Long, SVGCategoryEvent> categoryMap = new HashMap<>();
  @NotNull
  private final Map<Long, SVGSeatEvent> seatMap = new HashMap<>();

  public SVGPlanEventTuning(@NotNull byte[] svgData) throws IOException, SVGPlanException {
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
    }

    List<Element> seatList = SVGParser.getElementListByAttrPresent(document, "sbt:seat");
    for (Element seatElement : seatList) {
      SVGSeatEvent svgSeat = new SVGSeatEvent(seatElement);
      seatMap.put(svgSeat.getSeatId(), svgSeat);
    }
  }

  @NotNull
  @Override
  public BigDecimal getVersion() {
    return version;
  }

  @Override
  public boolean isProcessed() {
    return true;
  }

  @NotNull
  @Override
  public byte[] getProcessedSvgData() throws SVGPlanException {
    if (!isProcessed()) throw new SVGPlanException("document is not processed");
    return toByteArray(document);
  }

  public void changeSeatCategory(long seatId, long oldCategoryPriceId, long newCategoryPriceId) throws SVGPlanException {
    if (oldCategoryPriceId == newCategoryPriceId) return;
    SVGSeatEvent svgSeat = seatMap.get(seatId);
    if (svgSeat == null) throw new SVGPlanException("seatId=" + seatId + " is not found");
    SVGCategoryEvent oldSvgCategory = categoryMap.get(oldCategoryPriceId);
    if (oldSvgCategory == null)
      throw new SVGPlanException("old categoryPriceId=" + oldCategoryPriceId + " is not found");
    SVGCategoryEvent newSvgCategory = categoryMap.get(newCategoryPriceId);
    if (newSvgCategory == null)
      throw new SVGPlanException("new categoryPriceId=" + newCategoryPriceId + " is not found");
    svgSeat.changeCategory(seatId, oldSvgCategory.getIndex(), newSvgCategory.getIndex());
  }

  public void orderCategories(List<Long> categoryPriceIdList) throws SVGPlanException {
    if (version.compareTo(VER_11) < 0) throw new SVGPlanVerException(version.toString());
    if (categoryPriceIdList.size() != categoryMap.size())
      throw new SVGPlanException("categoryPriceIdList size doesn't equal categoryMap size");
    List<SVGCategoryEvent11> categoryList = new ArrayList<>(categoryMap.size());
    for (Long categoryPriceId : categoryPriceIdList) {
      SVGCategoryEvent11 svgCategory = (SVGCategoryEvent11) categoryMap.get(categoryPriceId);
      if (svgCategory == null) throw new SVGPlanException("categoryPriceId=" + categoryPriceId + " is not found");
      categoryList.add(svgCategory);
    }

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
      SVGCategoryEvent svgCategory = ordinal.updateFrom(sorted);
      categoryMap.put(svgCategory.getCategoryPriceId(), svgCategory);
    }
  }
}
