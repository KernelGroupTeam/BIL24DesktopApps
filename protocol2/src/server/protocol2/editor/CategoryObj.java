package server.protocol2.editor;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.07.15
 */
public class CategoryObj implements Serializable {
  private static final long serialVersionUID = 4538189075650037750L;
  private long id;
  private boolean placement;
  @NotNull
  private String name = "";
  private int seatsNumber;
  @Nullable
  private BigDecimal initPrice = null;

  public CategoryObj(long id, boolean placement) {
    this.id = id;
    this.placement = placement;
  }

  public long getId() {
    return id;
  }

  public boolean isPlacement() {
    return placement;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  public int getSeatsNumber() {
    return seatsNumber;
  }

  public void setSeatsNumber(int seatsNumber) {
    this.seatsNumber = seatsNumber;
  }

  @Nullable
  public BigDecimal getInitPrice() {
    return initPrice;
  }

  public void setInitPrice(@Nullable BigDecimal initPrice) {
    if (!isValidInitPrice(initPrice)) throw new IllegalArgumentException("initPrice");
    this.initPrice = initPrice;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  public static boolean isValidInitPrice(BigDecimal initPrice) {
    if (initPrice == null) return true;
    if (initPrice.scale() > 2) return false;
    return initPrice.compareTo(BigDecimal.ZERO) >= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CategoryObj)) return false;
    CategoryObj that = (CategoryObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name;
  }
}
