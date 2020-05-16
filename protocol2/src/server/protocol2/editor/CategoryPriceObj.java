package server.protocol2.editor;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class CategoryPriceObj implements Serializable {
  private static final long serialVersionUID = 3470473719734750713L;
  private long id;
  private boolean placement;
  @NotNull
  private String name = "";
  @NotNull
  private BigDecimal price = BigDecimal.ZERO;
  private int availability;

  public CategoryPriceObj(long id) {
    this.id = id;
  }

  public CategoryPriceObj(long id, boolean placement) {
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

  @NotNull
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@NotNull BigDecimal price) {
    if (!isValidPrice(price)) throw new IllegalArgumentException("price");
    this.price = price;
  }

  public int getAvailability() {
    return availability;
  }

  public void setAvailability(int availability) {
    this.availability = availability;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  public static boolean isValidPrice(BigDecimal price) {
    if (price == null) return false;
    if (price.scale() > 2) return false;
    return price.compareTo(BigDecimal.ZERO) >= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CategoryPriceObj)) return false;
    CategoryPriceObj that = (CategoryPriceObj) o;
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
