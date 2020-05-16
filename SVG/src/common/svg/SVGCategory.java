package common.svg;

import java.math.BigDecimal;
import java.util.regex.*;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.08.15
 */
public class SVGCategory {
  private static final Pattern pricePattern = Pattern.compile("\\d+[.,]?\\d*");
  private static final String priceVar = "%price%";
  @Nullable
  private final Element labelElement;
  @NotNull
  private final String name;
  @NotNull
  private final SVGColor color;
  private final boolean noneCat;
  private int index = -1;
  private int seatsNumber;
  @Nullable
  private BigDecimal initPrice;
  @Nullable
  private Element metaElement;
  @Nullable
  private Element priceElement;
  private boolean objectIdSet;

  SVGCategory(@NotNull SVGColor color) {
    this(null, "", color, true);
  }

  SVGCategory(@NotNull Element labelElement, @NotNull String name, @NotNull SVGColor color) {
    this(labelElement, name, color, false);
  }

  private SVGCategory(@Nullable Element labelElement, @NotNull String name, @NotNull SVGColor color, boolean noneCat) {
    this.labelElement = labelElement;
    this.name = name;
    this.color = color;
    this.noneCat = noneCat;
  }

  @Nullable
  Element getLabelElement() {
    return labelElement;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  SVGColor getColor() {
    return color;
  }

  boolean isNoneCat() {
    return noneCat;
  }

  void setIndex(int index) {
    if (index < 0) throw new IllegalArgumentException("index is negative");
    this.index = index;
  }

  int getIndex() {
    return index;
  }

  void updateIndex() {
    if (labelElement == null) throw new IllegalStateException("label element is null");
    if (index < 0) throw new IllegalStateException("index is not set");
    labelElement.setAttribute("sbt:cat", String.valueOf(index));
  }

  void updateOrdinal() {
    if (labelElement == null) throw new IllegalStateException("label element is null");
    if (priceElement == null) throw new IllegalStateException("price element is null");
    if (index < 0) throw new IllegalStateException("index is not set");
    labelElement.setAttribute("sbt:ordinal", String.valueOf(index));
    priceElement.setAttribute("sbt:ordinal", String.valueOf(index));
  }

  void updateLabelClass() {
    if (labelElement == null) throw new IllegalStateException("label element is null");
    if (index < 0) throw new IllegalStateException("index is not set");
    SVGParser.applyTransformCircle(labelElement);
    SVGParser.removeStyleByName(labelElement, "fill");
    SVGParser.removeStyleByName(labelElement, "fill-rule");
    SVGParser.removeStyleByName(labelElement, "stroke");
    SVGParser.removeClassCat(labelElement);
    SVGParser.addClassName(labelElement, getClassName());
  }

  String getClassName() {
    return SVGPlan.CLASS_CAT + index;
  }

  public int getSeatsNumber() {
    return seatsNumber;
  }

  void incSeatsNumber() {
    seatsNumber++;
  }

  @Nullable
  public BigDecimal getInitPrice() {
    return initPrice;
  }

  @Nullable
  Element getMetaElement() {
    return metaElement;
  }

  void setMetaElement(@Nullable Element metaElement) {
    this.metaElement = metaElement;
  }

  @Nullable
  Element getPriceElement() {
    return priceElement;
  }

  void setPriceElement(@Nullable Element priceElement, boolean replaceVar) throws SVGPlanException {
    this.priceElement = priceElement;
    if (priceElement != null) {
      Text priceText = SVGParser.getUniqueChildText(priceElement);
      if (priceText == null) {
        priceText = priceElement.getOwnerDocument().createTextNode(priceVar);
        priceElement.appendChild(priceText);
      } else {
        String priceTextStr = priceText.getNodeValue();
        if (!priceTextStr.contains(priceVar)) {
          Matcher m = pricePattern.matcher(priceTextStr);
          if (!m.find()) throw new SVGPlanException("Цена категории задана неверно: " + priceTextStr);
          String priceStr = m.group();
          BigDecimal price = new BigDecimal(priceStr.replace(',', '.'));
          if (price.scale() > 2)
            throw new SVGPlanException("Цена категории содержит больше 2-х знаков после запятой: " + price.toString());
          initPrice = price;
          if (replaceVar) {
            priceTextStr = priceTextStr.replace(priceStr, priceVar);
            priceText.setNodeValue(priceTextStr);
          }
        }
      }
    }
  }

  public void setObjectID(long id) {
    if (metaElement == null) throw new IllegalStateException("meta element is null");
    if (priceElement == null) throw new IllegalStateException("price element is null");
    metaElement.setAttribute("sbt:id", String.valueOf(id));
    priceElement.setAttribute("sbt:id", String.valueOf(id));
    objectIdSet = true;
  }

  public boolean isObjectIdSet() {
    return objectIdSet;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVGCategory)) return false;
    SVGCategory that = (SVGCategory) o;
    return color.equals(that.color);
  }

  @Override
  public int hashCode() {
    return color.hashCode();
  }
}
