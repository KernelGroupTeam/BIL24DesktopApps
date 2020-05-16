package common.svg;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.09.15
 */
public class SVGSeat implements Comparable<SVGSeat> {
  @NotNull
  private final Element seatElement;
  @Nullable
  private final Long categoryId;//null - место без категории
  @Nullable
  private final String shortSector;//краткий вариант сектора, непустое значение, используется только для эвентима
  @NotNull
  private final String sector;//непустое значение, "A", "Партер"
  @NotNull
  private final String row;//непустое значение, "8", "нет"
  @Nullable
  private final Integer rowInt;//8, null
  @NotNull
  private final String num;//непустое значение, "3", "D"
  @Nullable
  private final Integer numInt;//3, null
  private boolean objectIdSet;

  SVGSeat(@NotNull Element seatElement, @Nullable Long categoryId) throws SVGPlanException {//SVGPlanVenue
    this.seatElement = seatElement;
    this.categoryId = categoryId;
    Node parentNode = seatElement.getParentNode();
    if (parentNode.getNodeType() != Node.ELEMENT_NODE) throw new SVGPlanException("seat parent is not found");
    Element parentElement = (Element) parentNode;
    num = seatElement.getAttribute("sbt:seat");
    row = parentElement.getAttribute("sbt:row").substring(4);//sbt:row для совместимости хранит "Ряд 8"
    sector = parentElement.getAttribute("sbt:sect");
    String sSector = parentElement.getAttribute("sbt:short");
    shortSector = (sSector.isEmpty() ? null : sSector);
    if (num.isEmpty()) throw new SVGPlanException("seat num is empty");
    if (row.isEmpty()) throw new SVGPlanException("seat row is empty");
    if (sector.isEmpty()) throw new SVGPlanException("seat sector is empty");
    if (shortSector != null && shortSector.isEmpty()) throw new IllegalArgumentException("seat short sector is empty");
    rowInt = getIntValue(row);
    numInt = getIntValue(num);
  }

  SVGSeat(@NotNull Element seatElement, @Nullable String shortSector, @NotNull String sector, @NotNull String row, @NotNull String num) {//SVGPlanEditor
    if (shortSector != null && shortSector.isEmpty()) throw new IllegalArgumentException("seat short sector is empty");
    if (sector.isEmpty()) throw new IllegalArgumentException("seat sector is empty");
    if (row.isEmpty()) throw new IllegalArgumentException("seat row is empty");
    if (num.isEmpty()) throw new IllegalArgumentException("seat num is empty");
    this.seatElement = seatElement;
    this.shortSector = shortSector;
    this.sector = sector;
    this.row = row;
    this.num = num;
    rowInt = getIntValue(row);
    numInt = getIntValue(num);
    categoryId = null;
  }

  @Nullable
  private static Integer getIntValue(@NotNull String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @NotNull
  Element getSeatElement() {
    return seatElement;
  }

  @Nullable
  Long getCategoryId() {
    return categoryId;
  }

  @NotNull
  public String getEventimSector() {
    return (shortSector == null ? sector : shortSector);
  }

  @NotNull
  public String getSector() {
    return sector;
  }

  @NotNull
  public String getRow() {
    return row;
  }

  @NotNull
  public String getNum() {
    return num;
  }

  @Nullable
  Integer getNumInt() {
    return numInt;
  }

  @NotNull
  public BigDecimal[] getAbsCoordinates() {
    return SVGParser.getAbsCoordinatesCircle(seatElement);
  }

  public void setObjectID(long id) {
    seatElement.setAttribute("sbt:id", String.valueOf(id));
    objectIdSet = true;
  }

  public boolean isObjectIdSet() {
    return objectIdSet;
  }

  public String toEventimString() {
    if (shortSector == null) return sector + "|" + row + "|" + num;
    else return shortSector + "|" + row + "|" + num;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVGSeat)) return false;
    SVGSeat svgSeat = (SVGSeat) o;
    if (!num.equals(svgSeat.num)) return false;
    if (!sector.equals(svgSeat.sector)) return false;
    return row.equals(svgSeat.row);
  }

  @Override
  public int hashCode() {
    int result = sector.hashCode();
    result = 31 * result + row.hashCode();
    result = 31 * result + num.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return sector + "|" + row + "|" + num;
  }

  @Override
  public int compareTo(@NotNull SVGSeat o) {
    if (!sector.equals(o.sector)) return sector.compareTo(o.sector);
    if (!row.equals(o.row)) {
      if (rowInt != null && o.rowInt != null) return rowInt.compareTo(o.rowInt);
      if (rowInt == null && o.rowInt == null) return row.compareTo(o.row);
      if (rowInt == null) return 1;//нечисловые значения больше (идут после числовых)
      else return -1;
    }
    if (!num.equals(o.num)) {
      if (numInt != null && o.numInt != null) return numInt.compareTo(o.numInt);
      if (numInt == null && o.numInt == null) return num.compareTo(o.num);
      if (numInt == null) return 1;//нечисловые значения больше (идут после числовых)
      else return -1;
    }
    return 0;
  }
}
