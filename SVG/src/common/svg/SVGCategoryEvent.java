package common.svg;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

import static common.svg.SVGPlan.VAR_PRICE;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.09.15
 */
class SVGCategoryEvent {
  private static final String idAttr = "sbt:id";
  @NotNull
  private final Element metaElement;
  @NotNull
  private final Element priceElement;
  @NotNull
  private final Text priceText;
  private final int index;
  private final long categoryPriceId;
  @NotNull
  private String priceTextStr;
  @Nullable
  private String name = null;
  @Nullable
  private BigDecimal price = null;
  @Nullable
  private Boolean used = null;

  public SVGCategoryEvent(@NotNull Element metaElement, @NotNull Element priceElement) throws SVGPlanException {
    this.metaElement = metaElement;
    this.priceElement = priceElement;
    Text text = SVGParser.getUniqueChildText(priceElement);
    if (text == null) throw new SVGPlanException("price place is not found");
    priceText = text;
    priceTextStr = priceText.getNodeValue();
    try {
      index = Integer.parseInt(metaElement.getAttribute("sbt:index"));
    } catch (NumberFormatException e) {
      throw new SVGPlanException("sbt:index format", e);
    }
    String metaId = metaElement.getAttribute(idAttr);
    String priceId = priceElement.getAttribute(idAttr);
    if (!metaId.equals(priceId)) throw new SVGPlanException(idAttr);
    try {
      categoryPriceId = Long.parseLong(metaId);
    } catch (NumberFormatException e) {
      throw new SVGPlanException(idAttr + " format", e);
    }
    priceTextStr = priceText.getNodeValue();
  }

  public int getIndex() {
    return index;
  }

  public long getCategoryPriceId() {
    return categoryPriceId;
  }

  @NotNull
  public String getPriceTextStr() {
    return priceTextStr;
  }

  public void setCategoryName(long categoryPriceId, @NotNull String name) throws SVGPlanException {
    if (this.categoryPriceId != categoryPriceId) throw new SVGPlanException(idAttr);
    metaElement.setAttribute("sbt:name", name);
    this.name = name;
  }

  public void setCategoryPrice(long categoryPriceId, @NotNull BigDecimal price) throws SVGPlanException {
    if (this.categoryPriceId != categoryPriceId) throw new SVGPlanException(idAttr);
    metaElement.setAttribute("sbt:price", price.toString());
    if (!priceTextStr.contains(VAR_PRICE)) throw new SVGPlanException(VAR_PRICE + " not found");
    priceTextStr = priceTextStr.replace(VAR_PRICE, SVGPlan.priceFormat(price));
    priceText.setNodeValue(priceTextStr);
    this.price = price;
  }

  public void setCategoryUsed(boolean used) {
    metaElement.setAttribute("sbt:used", (used ? "1" : "0"));
    this.used = used;
  }

  public boolean isDataSet() {
    return (name != null && price != null && used != null);
  }

  void setNamePriceUsed(SVGCategoryEvent svgCategory) throws SVGPlanException {
    if (categoryPriceId != svgCategory.categoryPriceId) throw new SVGPlanException(idAttr);
    name = svgCategory.name;
    price = svgCategory.price;
    used = svgCategory.used;
  }
}
