package server.protocol2.reporter;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;
import server.protocol2.common.KindObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.10.15
 */
public class RAction implements Filterable, Serializable {
  private static final long serialVersionUID = 7935654325510438102L;
  private long id;
  private long organizerId;
  @NotNull
  private String organizerName;
  @NotNull
  private KindObj kind;
  @NotNull
  private Set<Long> cityIdSet;
  @NotNull
  private Set<Long> venueIdSet;
  @NotNull
  private String name;
  private boolean sellEnd;//продажи закончились
  private boolean actual;//продажи закончились менее суток назад или еще не закончились

  public RAction(long id, long organizerId, @NotNull String organizerName, @NotNull KindObj kind, @NotNull String name) {
    this(id, organizerId, organizerName, kind, Collections.<Long>emptySet(), Collections.<Long>emptySet(), name);
  }

  public RAction(long id, long organizerId, @NotNull String organizerName, @NotNull KindObj kind, @NotNull Set<Long> cityIdSet,
                 @NotNull Set<Long> venueIdSet, @NotNull String name) {
    this.id = id;
    this.organizerId = organizerId;
    this.organizerName = organizerName;
    this.kind = kind;
    this.cityIdSet = new HashSet<>(cityIdSet);
    this.venueIdSet = new HashSet<>(venueIdSet);
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public long getOrganizerId() {
    return organizerId;
  }

  @NotNull
  public String getOrganizerName() {
    return organizerName;
  }

  @NotNull
  public KindObj getKind() {
    return kind;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public boolean isSellEnd() {
    return sellEnd;
  }

  public void setSellEnd(boolean sellEnd) {
    this.sellEnd = sellEnd;
  }

  public boolean isActual() {
    return actual;
  }

  public void setActual(boolean actual) {
    this.actual = actual;
  }

  public boolean pass(@Nullable ROrganizer organizer) {
    return organizer != null && (organizer.getId() == 0 || organizerId == organizer.getId());
  }

  public boolean pass(@Nullable RCity city) {
    return city != null && (city.getId() == 0 || cityIdSet.contains(city.getId()));
  }

  public boolean pass(@Nullable RVenue venue) {
    return venue != null && (venue.getId() == 0 || venueIdSet.contains(venue.getId()));
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (filter instanceof ROrganizer) return pass((ROrganizer) filter);
    else if (filter instanceof RCity) return pass((RCity) filter);
    else if (filter instanceof RVenue) return pass((RVenue) filter);
    else return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RAction)) return false;
    RAction rAction = (RAction) o;
    return id == rAction.id;
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
