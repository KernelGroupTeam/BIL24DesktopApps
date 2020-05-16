package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;

/**
 * Created by Inventor on 24.11.2016.
 */
public class RVenue implements Filterable, Serializable {
  private static final long serialVersionUID = 9096449213455405458L;
  private long id;
  private long cityId;
  @NotNull
  private String name;

  public RVenue(long id, long cityId, @NotNull String name) {
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
    if (!(filter instanceof RCity)) return false;
    RCity city = (RCity) filter;
    return city.getId() == 0 || city.getId() == getCityId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RVenue)) return false;
    RVenue rVenue = (RVenue) o;
    return id == rVenue.id;
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
