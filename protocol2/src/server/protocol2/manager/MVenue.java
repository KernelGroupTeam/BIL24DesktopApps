package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;
import server.protocol2.common.CityObj;

/**
 * Created by Inventor on 24.11.2016.
 */
public class MVenue implements Filterable, Serializable {
  private static final long serialVersionUID = 9096449213455405458L;
  private long id;
  private long cityId;
  @NotNull
  private String name;

  public MVenue(long id, long cityId, @NotNull String name) {
    this.id = id;
    this.cityId = cityId;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public long getCityId() {
    return cityId;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof CityObj)) return false;
    CityObj city = (CityObj) filter;
    return getCityId() == 0 || city.getId() == getCityId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MVenue)) return false;
    MVenue venue = (MVenue) o;
    return id == venue.id;
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
