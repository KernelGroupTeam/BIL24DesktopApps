package server.protocol2.editor;

import java.io.Serializable;
import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.*;
import server.protocol2.common.GatewayObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.15
 */
public class GatewayEventObj implements Serializable {
  private static final long serialVersionUID = -51779645310512389L;
  private static final DateFormat formatDDMMYYYYHHmm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  private static final GatewayEventObj NONE = new GatewayEventObj(GatewayObj.getNone(), (byte) 0, "");
  @NotNull
  private GatewayObj gateway;
  @NotNull
  private Object eventId;
  @NotNull
  private Object eventUid;
  @NotNull
  private String stringViews;
  @Nullable
  private String date;//в формате дд.мм.гггг чч:мм
  @Nullable
  private Object actionId;
  @Nullable
  private String actionName;
  @Nullable
  private Object venueId;
  @Nullable
  private String venueName;
  @Nullable
  private Object planId;
  @Nullable
  private String planName;
  @Nullable
  private Object[] data;

  public GatewayEventObj(@NotNull GatewayObj gateway, @NotNull Object eventId, @NotNull String stringViews) {
    this(gateway, eventId, eventId, stringViews);
  }

  public GatewayEventObj(@NotNull GatewayObj gateway, @NotNull Object eventId, @NotNull Object eventUid, @NotNull String stringViews) {
    this.gateway = gateway;
    this.eventId = eventId;
    this.eventUid = eventUid;
    this.stringViews = stringViews;
  }

  @NotNull
  public GatewayObj getGateway() {
    return gateway;
  }

  @NotNull
  public Object getEventId() {
    return eventId;
  }

  @NotNull
  public Object getEventUid() {
    return eventUid;
  }

  @NotNull
  public String getStringViews() {
    return stringViews;
  }

  @Nullable
  public String getDate() {
    return date;
  }

  public void setDate(@Nullable String date) {
    this.date = date;
  }

  @Nullable
  public Object getActionId() {
    return actionId;
  }

  public void setActionId(@Nullable Object actionId) {
    this.actionId = actionId;
  }

  @Nullable
  public String getActionName() {
    return actionName;
  }

  public void setActionName(@Nullable String actionName) {
    this.actionName = actionName;
  }

  @Nullable
  public Object getVenueId() {
    return venueId;
  }

  public void setVenueId(@Nullable Object venueId) {
    this.venueId = venueId;
  }

  @Nullable
  public String getVenueName() {
    return venueName;
  }

  public void setVenueName(@Nullable String venueName) {
    this.venueName = venueName;
  }

  @Nullable
  public Object getPlanId() {
    return planId;
  }

  public void setPlanId(@Nullable Object planId) {
    this.planId = planId;
  }

  @Nullable
  public String getPlanName() {
    return planName;
  }

  public void setPlanName(@Nullable String planName) {
    this.planName = planName;
  }

  @Nullable
  public Object[] getData() {
    return data;
  }

  public void setData(@Nullable Object[] data) {
    this.data = data;
  }

  @NotNull
  static GatewayEventObj getDefault() {
    return NONE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GatewayEventObj)) return false;
    GatewayEventObj that = (GatewayEventObj) o;
    return gateway.equals(that.gateway) && eventId.equals(that.eventId);
  }

  @Override
  public int hashCode() {
    int result = gateway.hashCode();
    result = 31 * result + eventId.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return stringViews;
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmm.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @Nullable
  public static synchronized Date parseFormat(@Nullable String date) {
    if (date == null) return null;
    try {
      return formatDDMMYYYYHHmm.parse(date);
    } catch (ParseException e) {
      return null;
    }
  }
}
