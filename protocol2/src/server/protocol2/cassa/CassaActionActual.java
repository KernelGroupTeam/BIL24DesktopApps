package server.protocol2.cassa;

import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.KindObj;

/**
 * Created by Inventor on 19.10.2018
 */
public class CassaActionActual extends CassaAction<CassaActionEventActual> {
  private static final long serialVersionUID = 7333129718323179316L;
  private static final DateFormat formatDDMMYYYY = new SimpleDateFormat("dd.MM.yyyy");//Формат дат первого и последнего сеанса
  @NotNull
  private BigDecimal minPrice;//Минимальная цена за одно место представления
  @NotNull
  private String firstActionEventDate;//Дата первого сеанса
  @NotNull
  private String lastActionEventDate;//Дата последнего сеанса
  private boolean kdp;//Для запроса схемы зала и при бронировании любых мест необходимо передавать КДП
  @Nullable
  private transient Map<Long, CassaVenue> venueMap;//Список площадок сеансов

  public CassaActionActual(long id, @NotNull String name, @NotNull KindObj kind, @NotNull String legalOwner, @NotNull String legalOwnerInn,
                           @NotNull String legalOwnerPhone, @NotNull String age, @NotNull String smallPosterUrl, @NotNull String organizerInn,
                           @NotNull List<CassaActionEventActual> actionEventList, @NotNull BigDecimal minPrice,
                           long firstActionEventDate, long lastActionEventDate, boolean kdp) {
    super(id, name, kind, legalOwner, legalOwnerInn, legalOwnerPhone, age, smallPosterUrl, organizerInn, actionEventList);
    this.minPrice = minPrice;
    this.firstActionEventDate = formatDate(firstActionEventDate);
    this.lastActionEventDate = formatDate(lastActionEventDate);
    this.kdp = kdp;
  }

  @NotNull
  public BigDecimal getMinPrice() {
    return minPrice;
  }

  @NotNull
  public String getFirstActionEventDate() {
    return firstActionEventDate;
  }

  @NotNull
  public String getLastActionEventDate() {
    return lastActionEventDate;
  }

  public boolean isKdp() {
    return kdp;
  }

  @NotNull
  public Map<Long, CassaVenue> getVenueMap() {
    if (venueMap == null) {
      Map<Long, CassaVenue> venueMap = new HashMap<>();
      for (CassaActionEventActual actionEvent : getActionEventList()) {
        CassaVenue venue = actionEvent.getVenue();
        venueMap.put(venue.getId(), venue);
      }
      this.venueMap = Collections.unmodifiableMap(venueMap);
    }
    return venueMap;
  }

  @Override
  public String toString() {
    return "CassaActionActual{id=" + getId() + ", name=" + getName() + ", kind=" + getKind() + ", legalOwner=" + getLegalOwner() +
        ", legalOwnerInn=" + getLegalOwnerInn() + ", legalOwnerPhone=" + getLegalOwnerPhone() + ", age=" + getAge() + ", smallPosterUrl=" + getSmallPosterUrl() +
        ", organizerInn=" + getOrganizerInn() + ", actionEventList=" + getActionEventList().size() + ", minPrice=" + minPrice +
        ", firstActionEventDate=" + firstActionEventDate + ", lastActionEventDate=" + lastActionEventDate + ", kdp=" + kdp + '}';
  }

  @NotNull
  private static synchronized String formatDate(long date) {
    return formatDDMMYYYY.format(new Date(date));
  }

  @NotNull
  public static synchronized Date parseDate(@NotNull String date) throws ParseException {
    return formatDDMMYYYY.parse(date);
  }
}
