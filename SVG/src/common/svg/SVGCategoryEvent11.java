package common.svg;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

import static common.svg.SVGPlan.CLASS_CAT;
import static common.svg.SVGPlan.CLASS_UNUSED;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.02.16
 */
class SVGCategoryEvent11 extends SVGCategoryEvent {
  private static final String ordAttr = "sbt:ordinal";
  @NotNull
  private final Element metaElement;
  @NotNull
  private final Element priceElement;
  @NotNull
  private final Element labelElement;
  private final int ordinal;
  @NotNull
  private final Text priceText;

  public SVGCategoryEvent11(@NotNull Element metaElement, @NotNull Element priceElement, @NotNull Element labelElement) throws SVGPlanException {
    super(metaElement, priceElement);
    this.metaElement = metaElement;
    this.priceElement = priceElement;
    this.labelElement = labelElement;
    String metaIndex = metaElement.getAttribute("sbt:index");
    String labelIndex = labelElement.getAttribute("sbt:cat");
    if (!metaIndex.equals(labelIndex)) throw new SVGPlanException("sbt:index");
    String priceOrdinal = priceElement.getAttribute(ordAttr);
    String labelOrdinal = labelElement.getAttribute(ordAttr);
    if (!priceOrdinal.equals(labelOrdinal)) throw new SVGPlanException(ordAttr);
    try {
      ordinal = Integer.parseInt(priceOrdinal);
    } catch (NumberFormatException e) {
      throw new SVGPlanException(ordAttr + " format", e);
    }
    if (ordinal <= 0) throw new SVGPlanException(ordAttr + " is negative or zero");
    Text text = SVGParser.getUniqueChildText(priceElement);
    if (text == null) throw new SVGPlanException("price place is not found");
    priceText = text;
  }

  public int getOrdinal() {
    return ordinal;
  }

  //устанавливает в элементы текущей категории значения из переданной и создает новый экземпляр из них
  public SVGCategoryEvent11 updateFrom(SVGCategoryEvent11 svgCategory) throws SVGPlanException {
    priceElement.setAttribute("sbt:id", String.valueOf(svgCategory.getCategoryPriceId()));
    labelElement.setAttribute("sbt:cat", String.valueOf(svgCategory.getIndex()));
    priceText.setNodeValue(svgCategory.getPriceTextStr());
    SVGParser.replaceClassName(labelElement, CLASS_CAT + getIndex(), CLASS_CAT + svgCategory.getIndex());
    SVGCategoryEvent11 result = new SVGCategoryEvent11(svgCategory.metaElement, priceElement, labelElement);
    result.setNamePriceUsed(svgCategory);
    return result;
  }

  public void hide() {
    SVGParser.addClassName(priceElement, CLASS_UNUSED);
    SVGParser.addClassName(labelElement, CLASS_UNUSED);
  }
}
