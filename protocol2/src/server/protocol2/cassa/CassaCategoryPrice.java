package server.protocol2.cassa;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 13.10.2018
 */
public class CassaCategoryPrice implements Serializable {
  private static final long serialVersionUID = 9170033395745319418L;
  private long id;//Идентификатор ценовой категории
  @NotNull
  private String name;//Название ценовой категории
  @NotNull
  private BigDecimal price;//Цена
  private boolean placement;//Местовая ли цена

  public CassaCategoryPrice(long id, @NotNull String name, @NotNull BigDecimal price, boolean placement) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.placement = placement;
  }

  public final long getId() {
    return id;
  }

  @NotNull
  public final String getName() {
    return name;
  }

  @NotNull
  public final BigDecimal getPrice() {
    return price;
  }

  public final boolean isPlacement() {
    return placement;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CassaCategoryPrice)) return false;
    CassaCategoryPrice that = (CassaCategoryPrice) o;
    return id == that.id;
  }

  @Override
  public final int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaCategoryPrice{id=" + id + ", name=" + name + ", price=" + price + ", placement=" + placement + '}';
  }
}
