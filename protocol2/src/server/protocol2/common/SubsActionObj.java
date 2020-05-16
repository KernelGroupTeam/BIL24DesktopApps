package server.protocol2.common;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.03.16
 */
public class SubsActionObj implements Filterable, Serializable {
  private static final long serialVersionUID = 687198204776767975L;
  private long id;
  @NotNull
  private String name;
  private long organizerId;
  @NotNull
  private String organizerName;
  @NoLogging
  @NotNull
  private Set<Long> cityIdSet;
  @NoLogging
  @NotNull
  private Set<Long> venueIdSet;
  private transient long sellEndTime;
  @NoLogging
  private boolean actual;

  public SubsActionObj(long id, @NotNull String name, long organizerId, @NotNull String organizerName, @NotNull Set<Long> cityIdSet, @NotNull Set<Long> venueIdSet, long sellEndTime) {
    this.id = id;
    this.name = name;
    this.organizerId = organizerId;
    this.organizerName = organizerName;
    this.cityIdSet = new HashSet<>(cityIdSet);
    this.venueIdSet = new HashSet<>(venueIdSet);
    this.sellEndTime = sellEndTime;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public long getOrganizerId() {
    return organizerId;
  }

  @NotNull
  public String getOrganizerName() {
    return organizerName;
  }

  @NotNull
  public Set<Long> getCityIdSet() {
    return cityIdSet;
  }

  @NotNull
  public Set<Long> getVenueIdSet() {
    return venueIdSet;
  }

  public boolean isActual() {
    return actual;
  }

  public void updateActual(long actualTime) {
    this.actual = (sellEndTime == 0L || actualTime < sellEndTime);
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (filter instanceof CityObj) {
      CityObj city = (CityObj) filter;
      return cityIdSet.contains(city.getId());
    } else return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SubsActionObj)) return false;
    SubsActionObj that = (SubsActionObj) o;
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
