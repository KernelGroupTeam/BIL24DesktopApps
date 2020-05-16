package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 18.10.2018
 */
public class CassaSeat implements Serializable {
  private static final long serialVersionUID = 3579509380248450900L;
  private long id;//Идентификатор места
  @NotNull
  private CassaCategoryPrice categoryPrice;//Ценовая категория места
  @Nullable
  private CassaSeatLocation location;//Координаты места

  public CassaSeat(long id, @NotNull CassaCategoryPrice categoryPrice, @Nullable CassaSeatLocation location) {
    this.id = id;
    this.categoryPrice = categoryPrice;
    this.location = location;
  }

  public final long getId() {
    return id;
  }

  @NotNull
  public final CassaCategoryPrice getCategoryPrice() {
    return categoryPrice;
  }

  @Nullable
  public final CassaSeatLocation getLocation() {
    return location;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CassaSeat)) return false;
    CassaSeat that = (CassaSeat) o;
    return id == that.id;
  }

  @Override
  public final int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaSeat{id=" + id + ", categoryPrice=" + categoryPrice + ", location=" + location + '}';
  }
}
