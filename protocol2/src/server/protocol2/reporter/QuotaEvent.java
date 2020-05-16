package server.protocol2.reporter;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.03.17
 */
public class QuotaEvent implements Serializable {
  private static final long serialVersionUID = 8580050115732505684L;
  private long id;
  private long venueId;
  @NotNull
  private String venueName = "";
  @NotNull
  private String venueAddress = "";
  private long actionId;
  @NotNull
  private String actionName = "";
  @NotNull
  private String actionLegalOwner = "";//устроитель(передал)
  @NotNull
  private String organizerName = "";//организатор(получил)
  @NotNull
  private String showTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private List<QuotaDataObj> quotaInDataList = Collections.emptyList();
  @NotNull
  private List<QuotaDataObj> quotaOutDataList = Collections.emptyList();

  public QuotaEvent(long id) {
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

  @NotNull
  public String getVenueAddress() {
    return venueAddress;
  }

  public void setVenueAddress(@NotNull String venueAddress) {
    this.venueAddress = venueAddress;
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
  public String getActionLegalOwner() {
    return actionLegalOwner;
  }

  public void setActionLegalOwner(@NotNull String actionLegalOwner) {
    this.actionLegalOwner = actionLegalOwner;
  }

  @NotNull
  public String getOrganizerName() {
    return organizerName;
  }

  public void setOrganizerName(@NotNull String organizerName) {
    this.organizerName = organizerName;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(@NotNull String showTime) {
    this.showTime = showTime;
  }

  @NotNull
  public List<QuotaDataObj> getQuotaInDataList() {
    return quotaInDataList;
  }

  public void setQuotaInDataList(@NotNull List<QuotaDataObj> quotaInDataList) {
    for (QuotaDataObj quotaData : quotaInDataList) {
      if (this != quotaData.getQuotaEvent()) throw new IllegalArgumentException();
    }
    this.quotaInDataList = quotaInDataList;
  }

  @NotNull
  public List<QuotaDataObj> getQuotaOutDataList() {
    return quotaOutDataList;
  }

  public void setQuotaOutDataList(@NotNull List<QuotaDataObj> quotaOutDataList) {
    for (QuotaDataObj quotaData : quotaOutDataList) {
      if (this != quotaData.getQuotaEvent()) throw new IllegalArgumentException();
    }
    this.quotaOutDataList = quotaOutDataList;
  }

  @NotNull
  public List<QuotaDataObj> getQuotaDataList(@NotNull QuotaDataObj.Type type) {
    switch (type) {
      case IN:
        return quotaInDataList;
      case OUT:
        return quotaOutDataList;
      default:
        throw new IllegalArgumentException("type");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof QuotaEvent)) return false;
    QuotaEvent that = (QuotaEvent) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
