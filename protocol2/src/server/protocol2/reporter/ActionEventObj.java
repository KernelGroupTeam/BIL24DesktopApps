package server.protocol2.reporter;

import java.io.Serializable;
import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.07.15
 */
public class ActionEventObj implements Serializable {
  private static final long serialVersionUID = -351961881293607683L;
  private static final DateFormat formatDDMMYYYYHHmm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  private long id;
  private long cityId;
  @NotNull
  private String cityName = "";
  private long venueId;
  @NotNull
  private String venueName = "";
  private long actionId;
  @NotNull
  private String actionName = "";
  @NotNull
  private String showTime = "";//в формате дд.мм.гггг чч:мм
  @Nullable
  private transient Date showTimeDate;
  private boolean eTickets;

  public ActionEventObj(long id) {
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

  public long getActionId() {
    return actionId;
  }

  public void setActionId(long actionId) {
    this.actionId = actionId;
  }

  @NotNull
  public String getActionName() {
    return actionName;
  }

  public void setActionName(@NotNull String actionName) {
    this.actionName = actionName;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(@NotNull String showTime) {
    this.showTime = showTime;
  }

  @NotNull
  public Date getShowTimeDate() throws ParseException {
    if (showTimeDate != null) return showTimeDate;
    showTimeDate = parseFormat(showTime);
    return showTimeDate;
  }

  public boolean isETickets() {
    return eTickets;
  }

  public void setETickets(boolean eTickets) {
    this.eTickets = eTickets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActionEventObj)) return false;
    ActionEventObj that = (ActionEventObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmm.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmm.parse(date);
  }
}
