package common.svg;

import java.awt.*;
import java.math.BigDecimal;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.08.15
 */
class SVGCategoryVenue {
  private static final String idAttr = "sbt:id";
  @NotNull
  private final Element metaElement;
  @NotNull
  private final Element priceElement;
  @NotNull
  private final Color color;
  private final int index;
  private final long categoryId;
  @Nullable
  private Long categoryPriceId;

  public SVGCategoryVenue(@NotNull Element metaElement, @NotNull Element priceElement) throws SVGPlanException {
    this.metaElement = metaElement;
    this.priceElement = priceElement;
    color = CSS3ColorParser.parse(metaElement.getAttribute("sbt:color"));
    try {
      index = Integer.parseInt(metaElement.getAttribute("sbt:index"));
    } catch (NumberFormatException e) {
      throw new SVGPlanException("sbt:index format", e);
    }
    String metaId = metaElement.getAttribute(idAttr);
    String priceId = priceElement.getAttribute(idAttr);
    if (!metaId.equals(priceId)) throw new SVGPlanException(idAttr);
    try {
      categoryId = Long.parseLong(metaId);
    } catch (NumberFormatException e) {
      throw new SVGPlanException(idAttr + " format", e);
    }
  }

  @NotNull
  public Color getColor() {
    return color;
  }

  public int getIndex() {
    return index;
  }

  public long getCategoryId() {
    return categoryId;
  }

  public void replaceCategoryId(long categoryId, long categoryPriceId) throws SVGPlanException {
    if (this.categoryId != categoryId) throw new SVGPlanException(idAttr);
    metaElement.setAttribute(idAttr, String.valueOf(categoryPriceId));
    priceElement.setAttribute(idAttr, String.valueOf(categoryPriceId));
    this.categoryPriceId = categoryPriceId;
  }

  public boolean isIdReplaced() {
    return categoryPriceId != null;
  }

  public void setEditorPrice(long categoryId, @NotNull BigDecimal price) throws SVGPlanException {
    if (this.categoryId != categoryId) throw new SVGPlanException(idAttr);
    Text priceText = SVGParser.getUniqueChildText(priceElement);
    if (priceText == null) throw new SVGPlanException("price place is not found");
    String priceTextStr = priceText.getNodeValue();
    priceTextStr = priceTextStr.replace(SVGPlan.VAR_PRICE, SVGPlan.priceFormat(price));
    priceText.setNodeValue(priceTextStr);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVGCategoryVenue)) return false;
    SVGCategoryVenue that = (SVGCategoryVenue) o;
    return categoryId == that.categoryId;
  }

  @Override
  public int hashCode() {
    return (int) (categoryId ^ (categoryId >>> 32));
  }
}
