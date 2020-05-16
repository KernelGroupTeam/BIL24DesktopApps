package common.svg;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

import static common.svg.SVGPlan.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.09.15
 */
class SVGSeatEvent {
  private static final String idAttr = "sbt:id";
  @NotNull
  private final Element seatElement;
  private final long seatId;
  private final int catIndex;
  @Nullable
  private Integer state = null;

  public SVGSeatEvent(@NotNull Element seatElement) throws SVGPlanException {
    this.seatElement = seatElement;
    try {
      seatId = Long.parseLong(seatElement.getAttribute(idAttr));
    } catch (NumberFormatException e) {
      throw new SVGPlanException(idAttr + " format", e);
    }
    try {
      catIndex = Integer.parseInt(seatElement.getAttribute("sbt:cat"));
    } catch (NumberFormatException e) {
      throw new SVGPlanException("sbt:cat format", e);
    }
  }

  public long getSeatId() {
    return seatId;
  }

  public int getCatIndex() {
    return catIndex;
  }

  public void setSeatState(long seatId, int state, boolean my) throws SVGPlanException {
    if (this.seatId != seatId) throw new SVGPlanException(idAttr);
    seatElement.setAttribute("sbt:state", String.valueOf(state));
    if (my) seatElement.setAttribute("sbt:owner", "yes");
    String className = getClassName(state, my);
    if (className != null) SVGParser.addClassName(seatElement, className);
    this.state = state;
  }

  public boolean isDataSet() {
    return state != null;
  }

  public void setSeatMy(long seatId) throws SVGPlanException {
    if (this.seatId != seatId) throw new SVGPlanException(idAttr);
    SVGParser.addClassName(seatElement, CLASS_STATE_MY);
  }

  public void changeCategory(long seatId, int oldIndex, int newIndex) throws SVGPlanException {
    if (this.seatId != seatId) throw new SVGPlanException(idAttr);
    if (!seatElement.getAttribute("sbt:cat").equals(String.valueOf(oldIndex))) throw new SVGPlanException("sbt:cat");
    seatElement.setAttribute("sbt:cat", String.valueOf(newIndex));
    SVGParser.replaceClassName(seatElement, CLASS_CAT + oldIndex, CLASS_CAT + newIndex);
  }

  @SuppressWarnings("DuplicateBranchesInSwitch")
  @Nullable
  private String getClassName(int state, boolean my) {
    switch (state) {
      case 0:
      case 5:
        return CLASS_STATE_NONE;
      case 1:
        return null;
      case 2:
      case 3:
        if (my) return CLASS_STATE_MY;
        else return CLASS_STATE_RESERVED;
      case 4:
        if (my) return CLASS_STATE_MY;
//        else return CLASS_STATE_SOLD;//отключен вывод проданных мест
        else return CLASS_STATE_NONE;
      default:
        return CLASS_STATE_NONE;
    }
  }

  public String toEventimString() throws SVGPlanException {
    Node parentNode = seatElement.getParentNode();
    if (parentNode.getNodeType() != Node.ELEMENT_NODE) throw new SVGPlanException("seat parent is not found");
    Element parentElement = (Element) parentNode;
    String num = seatElement.getAttribute("sbt:seat");
    String row = parentElement.getAttribute("sbt:row").substring(4);//sbt:row для совместимости хранит "Ряд 8"
    String sector = parentElement.getAttribute("sbt:sect");
    String sSector = parentElement.getAttribute("sbt:short");
    String shortSector = (sSector.isEmpty() ? null : sSector);
    if (num.isEmpty()) throw new SVGPlanException("seat num is empty");
    if (row.isEmpty()) throw new SVGPlanException("seat row is empty");
    if (sector.isEmpty()) throw new SVGPlanException("seat sector is empty");
    if (shortSector == null) return sector + "|" + row + "|" + num;
    else return shortSector + "|" + row + "|" + num;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVGSeatEvent)) return false;
    SVGSeatEvent that = (SVGSeatEvent) o;
    return seatId == that.seatId;
  }

  @Override
  public int hashCode() {
    return (int) (seatId ^ (seatId >>> 32));
  }
}
