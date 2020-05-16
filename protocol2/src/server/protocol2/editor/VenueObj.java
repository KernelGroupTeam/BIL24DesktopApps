package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;
import server.protocol2.common.CityObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.06.15
 */
public class VenueObj implements Filterable, Serializable {
  private static final long serialVersionUID = -6455004688338801118L;
  private long id;
  private long cityId;
  @NotNull
  private String cityName = "";
  @NotNull
  private String name = "";
  @NotNull
  private VenueType venueType = VenueType.getUnknown();
  @NotNull
  private String address = "";
  @NotNull
  private String geoLat = "";//широта "45.104667"
  @NotNull
  private String geoLon = "";//долгота "41.045800"
  @NotNull
  private String description = "";
  @Nullable
  private ImageObj bigImage;//640х670
  private transient boolean childless;

  public VenueObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public long getCityId() {
    return cityId;
  }

  public void setCityId(long cityId) {
    this.cityId = cityId;
  }

  @NotNull
  public String getCityName() {
    return cityName;
  }

  public void setCityName(@NotNull String cityName) {
    this.cityName = cityName;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public VenueType getType() {
    return venueType;
  }

  public void setType(@NotNull VenueType venueType) {
    this.venueType = venueType;
  }

  @NotNull
  public String getAddress() {
    return address;
  }

  public void setAddress(@NotNull String address) {
    this.address = address;
  }

  @NotNull
  public String getGeoLat() {
    return geoLat;
  }

  public void setGeoLat(@NotNull String geoLat) {
    this.geoLat = geoLat;
  }

  @NotNull
  public String getGeoLon() {
    return geoLon;
  }

  public void setGeoLon(@NotNull String geoLon) {
    this.geoLon = geoLon;
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(@NotNull String description) {
    this.description = description;
  }

  @Nullable
  public ImageObj getBigImage() {
    return bigImage;
  }

  public void setBigImage(@Nullable ImageObj bigImage) {
    this.bigImage = bigImage;
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
    if (!(filter instanceof CityObj)) return false;
    CityObj city = (CityObj) filter;
    return city.getId() == getCityId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof VenueObj)) return false;
    VenueObj venueObj = (VenueObj) o;
    return id == venueObj.id;
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
