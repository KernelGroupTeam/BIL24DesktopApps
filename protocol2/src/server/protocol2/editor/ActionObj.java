package server.protocol2.editor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.KindObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.06.15
 */
public class ActionObj implements Filterable, Serializable {
  private static final long serialVersionUID = -5054017220645820236L;
  private long id;
  private long organizerId;
  @NotNull
  private String organizerName = "";
  @NotNull
  private KindObj kind;
  @NotNull
  private String name = "";
  @NotNull
  private String fullName = "";
  private int duration;
  @NotNull
  private String posterName = "";
  @NotNull
  private String posterDesc = "";
  @Nullable
  private ImageObj smallImage;//320х335
  @Nullable
  private ImageObj bigImage;//640х670
  @Nullable
  private BookletType bookletType;
  private int rating;
  @NotNull
  private Age age = Age.UNKNOWN;
  @NotNull
  private Set<GenreObj> genreSet = Collections.emptySet();
  @NotNull
  private String legalOwner = "";
  @NotNull
  private String legalOwnerInn = "";
  @NotNull
  private String legalOwnerPhone = "";
  @NotNull
  private BigDecimal minChargePercent = BigDecimal.ZERO;
  @Nullable
  private Integer kdp;//КДП - код доступа к представлению
  @NoLogging
  @NotNull
  private Set<Long> cityIdSet = new HashSet<>();//список городов, использующих это представление
  @NoLogging
  @NotNull
  private Set<Long> venueIdSet = new HashSet<>();//список мест проведения, использующих это представление
  @NoLogging
  private boolean actual;
  private transient boolean childless;

  public ActionObj(long id, @NotNull KindObj kind) {
    this.id = id;
    this.kind = kind;
  }

  public long getId() {
    return id;
  }

  public long getOrganizerId() {
    return organizerId;
  }

  public void setOrganizerId(long organizerId) {
    this.organizerId = organizerId;
  }

  @NotNull
  public String getOrganizerName() {
    return organizerName;
  }

  public void setOrganizerName(@NotNull String organizerName) {
    this.organizerName = organizerName;
  }

  @NotNull
  public KindObj getKind() {
    return kind;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getFullName() {
    return fullName;
  }

  public void setFullName(@NotNull String fullName) {
    this.fullName = fullName;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    if (!isValidDuration(duration)) throw new IllegalArgumentException("duration");
    this.duration = duration;
  }

  public static boolean isValidDuration(int duration) {
    return duration >= 0;
  }

  @NotNull
  public String getPosterName() {
    return posterName;
  }

  public void setPosterName(@NotNull String posterName) {
    this.posterName = posterName;
  }

  @NotNull
  public String getPosterDesc() {
    return posterDesc;
  }

  public void setPosterDesc(@NotNull String posterDesc) {
    this.posterDesc = posterDesc;
  }

  @Nullable
  public ImageObj getSmallImage() {
    return smallImage;
  }

  public void setSmallImage(@Nullable ImageObj smallImage) {
    this.smallImage = smallImage;
  }

  @Nullable
  public ImageObj getBigImage() {
    return bigImage;
  }

  public void setBigImage(@Nullable ImageObj bigImage) {
    this.bigImage = bigImage;
  }

  @Nullable
  public BookletType getBookletType() {
    return bookletType;
  }

  public void setBookletType(@Nullable BookletType bookletType) {
    this.bookletType = bookletType;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    if (rating < 0 || rating > 10) throw new IllegalArgumentException("rating");
    this.rating = rating;
  }

  @NotNull
  public Age getAge() {
    return age;
  }

  public void setAge(@NotNull Age age) {
    this.age = age;
  }

  @NotNull
  public Set<GenreObj> getGenreSet() {
    return genreSet;
  }

  public void setGenreSet(@NotNull Set<GenreObj> genreSet) {
    this.genreSet = genreSet;
  }

  @NotNull
  public String getLegalOwner() {
    return legalOwner;
  }

  public void setLegalOwner(@NotNull String legalOwner) {
    this.legalOwner = legalOwner;
  }

  @NotNull
  public String getLegalOwnerInn() {
    return legalOwnerInn;
  }

  public void setLegalOwnerInn(@NotNull String legalOwnerInn) {
    this.legalOwnerInn = legalOwnerInn;
  }

  @NotNull
  public String getLegalOwnerPhone() {
    return legalOwnerPhone;
  }

  public void setLegalOwnerPhone(@NotNull String legalOwnerPhone) {
    this.legalOwnerPhone = legalOwnerPhone;
  }

  @NotNull
  public BigDecimal getMinChargePercent() {
    return minChargePercent;
  }

  public void setMinChargePercent(@NotNull BigDecimal minChargePercent) {
    this.minChargePercent = minChargePercent;
  }

  @Nullable
  public Integer getKdp() {
    return kdp;
  }

  public void setKdp(@Nullable Integer kdp) {
    if (kdp != null && kdp < 0) throw new IllegalArgumentException("negative kdp");
    this.kdp = kdp;
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

  public void setActual(boolean actual) {
    this.actual = actual;
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
    if (filter instanceof KindObj) {
      KindObj kind = (KindObj) filter;
      return kind.equals(this.kind);
    } else if (filter instanceof VenueObj) {
      if (venueIdSet.isEmpty()) return true;
      VenueObj venue = (VenueObj) filter;
      return venueIdSet.contains(venue.getId());
    } else return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActionObj)) return false;
    ActionObj actionObj = (ActionObj) o;
    return id == actionObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name;
  }

  public enum Age {
    UNKNOWN(0, "Не задано"), C_0(1, "0+"), C_6(2, "6+"), C_12(3, "12+"), C_16(4, "16+"), C_18(5, "18+");
    private static final Age[] en;

    static {
      en = new Age[]{UNKNOWN, C_0, C_6, C_12, C_16, C_18};
      for (int i = 0; i < en.length; i++) {
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<Age> enList = Arrays.asList(en);
      for (Age value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    Age(int id, @NotNull String desc) {
      this.id = id;
      this.desc = desc;
    }

    public int getId() {
      return id;
    }

    @NotNull
    public String getDesc() {
      return desc;
    }

    @Override
    public String toString() {
      return desc;
    }

    @NotNull
    public static Age getAge(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
