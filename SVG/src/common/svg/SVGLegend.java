package common.svg;

import java.util.List;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

import static common.svg.SVGParser.getStyleByName;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.08.15
 */
class SVGLegend {
  @NotNull
  private final SVGColor noneColor;
  @NotNull
  private final SVGColor reservedColor;
  @NotNull
  private final SVGColor soldColor;
  @NotNull
  private final SVGColor myColor;

  public SVGLegend(Document document) throws SVGPlanException {
    SVGColor noneColor = null;
    SVGColor reservedColor = null;
    SVGColor soldColor = null;
    SVGColor myColor = null;

    Element legendElement = SVGParser.getElementById(document, "g", SVGPlan.ID_LEGEND);
    if (legendElement == null) throw new SVGPlanException("Условные обозначения не найдены");

    List<Element> legendList = SVGParser.getChildElementList(legendElement, "circle");
    for (Element element : legendList) {
      String id = element.getAttribute("id");
      if (id.equals(SVGPlan.ID_STATE_NONE)) noneColor = checkOldValue(getStyleByName(element, "fill"), noneColor, id);
      if (id.equals(SVGPlan.ID_STATE_RESERVED)) reservedColor = checkOldValue(getStyleByName(element, "fill"), reservedColor, id);
      if (id.equals(SVGPlan.ID_STATE_SOLD)) soldColor = checkOldValue(getStyleByName(element, "fill"), soldColor, id);
      if (id.equals(SVGPlan.ID_STATE_MY)) myColor = checkOldValue(getStyleByName(element, "fill"), myColor, id);
      SVGParser.applyTransformCircle(element);
    }
    checkColor(noneColor, SVGPlan.ID_STATE_NONE);
    checkColor(reservedColor, SVGPlan.ID_STATE_RESERVED);
    checkColor(soldColor, SVGPlan.ID_STATE_SOLD);
    checkColor(myColor, SVGPlan.ID_STATE_MY);

    this.noneColor = noneColor;
    this.reservedColor = reservedColor;
    this.soldColor = soldColor;
    this.myColor = myColor;
  }

  @NotNull
  public SVGColor getNoneColor() {
    return noneColor;
  }

  @NotNull
  public SVGColor getReservedColor() {
    return reservedColor;
  }

  @NotNull
  public SVGColor getSoldColor() {
    return soldColor;
  }

  @NotNull
  public SVGColor getMyColor() {
    return myColor;
  }

  @NotNull
  public String getStyle() {
    return "." + SVGPlan.CLASS_STATE_NONE + " {fill:" + noneColor + " !important}\n" +
        "." + SVGPlan.CLASS_STATE_RESERVED + " {fill:" + reservedColor + " !important}\n" +
        "." + SVGPlan.CLASS_STATE_SOLD + " {fill:" + soldColor + " !important}\n" +
        "." + SVGPlan.CLASS_STATE_MY + " {fill:" + myColor + " !important}\n";
  }

  @Nullable
  private SVGColor checkOldValue(@Nullable String fillColor, @Nullable SVGColor oldValue, @NotNull String id) throws SVGPlanException {
    if (oldValue != null) throw new SVGPlanException("Условное обозначение '" + id + "' уже задано");
    if (fillColor == null) return null;
    return new SVGColor(fillColor);
  }

  private void checkColor(@Nullable SVGColor color, @NotNull String id) throws SVGPlanException {
    if (color == null) throw new SVGPlanException("Условное обозначение '" + id + "' не найдено");
  }
}
