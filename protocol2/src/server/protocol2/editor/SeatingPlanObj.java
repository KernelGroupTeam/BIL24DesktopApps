package server.protocol2.editor;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 25.08.15
 */
public class SeatingPlanObj implements Filterable, Serializable {
  private static final long serialVersionUID = -487807480847276099L;
  private long id;
  private long venueId;
  @NotNull
  private String venueName = "";
  private boolean placement;
  @NotNull
  private String name = "";
  @NotNull
  private List<CategoryObj> categoryList = Collections.emptyList();
  @NotNull
  private List<CategoryLimitObj> categoryLimitList = Collections.emptyList();
  @Nullable
  @NoLogging
  private byte[] svgZip;
  private boolean splExists;
  @NoLogging
  private boolean owner;
  private transient boolean childless;

  public SeatingPlanObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public long getVenueId() {
    return venueId;
  }

  public void setVenueId(long venueId) {
    this.venueId = venueId;
  }

  @NotNull
  public String getVenueName() {
    return venueName;
  }

  public void setVenueName(@NotNull String venueName) {
    this.venueName = venueName;
  }

  public boolean isPlacement() {
    return placement;
  }

  public void setPlacement(boolean placement) {
    this.placement = placement;
  }

  //true только для комбинированной схемы
  public boolean isCombined() {
    if (!placement) return false;
    for (int i = categoryList.size() - 1; i >= 0; i--) {
      CategoryObj category = categoryList.get(i);
      if (!category.isPlacement()) return true;
    }
    return false;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public List<CategoryObj> getCategoryList() {
    return categoryList;
  }

  public void setCategoryList(@NotNull List<CategoryObj> categoryList) {
    this.categoryList = categoryList;
  }

  @NotNull
  public List<CategoryLimitObj> getCategoryLimitList() {
    return categoryLimitList;
  }

  public void setCategoryLimitList(@NotNull List<CategoryLimitObj> categoryLimitList) {
    this.categoryLimitList = categoryLimitList;
  }

  @Nullable
  public byte[] getSvgZip() {
    return svgZip;
  }

  public void setSvgZip(@Nullable byte[] svgZip) {
    this.svgZip = svgZip;
  }

  public boolean isSplExists() {
    return splExists;
  }

  public void setSplExists(boolean splExists) {
    this.splExists = splExists;
  }

  public boolean isOwner() {
    return owner;
  }

  public void setOwner(boolean owner) {
    this.owner = owner;
  }

  public boolean isChildless() {
    return childless;
  }

  public void setChildless(boolean childless) {
    this.childless = childless;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof VenueObj)) return false;
    VenueObj venue = (VenueObj) filter;
    return venue.getId() == getVenueId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SeatingPlanObj)) return false;
    SeatingPlanObj obj = (SeatingPlanObj) o;
    return id == obj.id;
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
