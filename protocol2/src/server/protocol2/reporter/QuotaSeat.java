package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.03.17
 */
public class QuotaSeat implements Serializable {
  private static final long serialVersionUID = -2444097008700600636L;
  private long id;
  @Nullable
  private SeatLocationObj seatLocation;
  @NotNull
  private String category;
  @NotNull
  private BigDecimal price;

  public QuotaSeat(long id, @Nullable SeatLocationObj seatLocation, @NotNull String category, @NotNull BigDecimal price) {
    this.id = id;
    this.seatLocation = seatLocation;
    this.category = category;
    this.price = price;
  }

  public long getId() {
    return id;
  }

  @Nullable
  public SeatLocationObj getSeatLocation() {
    return seatLocation;
  }

  @NotNull
  public String getCategory() {
    return category;
  }

  @NotNull
  public BigDecimal getPrice() {
    return price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof QuotaSeat)) return false;
    QuotaSeat quotaSeat = (QuotaSeat) o;
    return id == quotaSeat.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
