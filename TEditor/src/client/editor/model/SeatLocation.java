package client.editor.model;

import org.jetbrains.annotations.*;
import server.protocol2.editor.SeatLocationObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 10.02.17
 */
public class SeatLocation implements Comparable<SeatLocation> {
  private static final String separator = "|";
  private final long seatId;//для безместовых
  private final boolean placement;
  @NotNull
  private final String sector;
  @NotNull
  private final String row;
  @Nullable
  private final Integer rowInt;
  @NotNull
  private final String num;
  @Nullable
  private final Integer numInt;

  public SeatLocation(long seatId) {
    this.seatId = seatId;
    this.placement = false;
    this.sector = "";
    this.row = "";
    this.num = "";
    rowInt = 0;
    numInt = 0;
  }

  public SeatLocation(@NotNull SeatLocationObj seatLocation) {
    this(seatLocation.getSector(), seatLocation.getRow(), seatLocation.getNumber());
  }

  public SeatLocation(@NotNull String sector, @NotNull String row, @NotNull String num) {
    this.seatId = 0L;
    this.placement = true;
    this.sector = sector;
    this.row = row;
    this.num = num;
    rowInt = getIntValue(row);
    numInt = getIntValue(num);
  }

  public boolean isPlacement() {
    return placement;
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

  @Override
  public int compareTo(@NotNull SeatLocation o) {
    if (seatId != o.seatId) return Long.compare(seatId, o.seatId);
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

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SeatLocation)) return false;
    SeatLocation that = (SeatLocation) o;
    if (seatId != that.seatId) return false;
    if (!num.equals(that.num)) return false;
    if (!row.equals(that.row)) return false;
    return sector.equals(that.sector);
  }

  @Override
  public int hashCode() {
    int result = (int) (seatId ^ (seatId >>> 32));
    result = 31 * result + sector.hashCode();
    result = 31 * result + row.hashCode();
    result = 31 * result + num.hashCode();
    return result;
  }

  @Override
  public String toString() {
    if (seatId == 0) return sector + separator + row + separator + num;
    else return "Без размещения id=" + seatId;
  }
}
