package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.17
 */
public class ActionEventSync implements Serializable {
  private static final long serialVersionUID = 5489579284212134704L;
  private long id;
  private int mismatchedPrice;
  private int mismatchedSeat;
  @NotNull
  private String actionName = "";
  private long organizerId;
  @NotNull
  private String organizerName = "";
  @NotNull
  private String showTime = "";//в формате дд.мм.гггг чч:мм
  private boolean sellEnable;//todo rename sellEnabled
  @NotNull
  private GatewayEventObj gatewayEvent = GatewayEventObj.getDefault();

  public ActionEventSync(long id, int mismatchedPrice, int mismatchedSeat) {
    this.id = id;
    this.mismatchedPrice = mismatchedPrice;
    this.mismatchedSeat = mismatchedSeat;
  }

  public long getId() {
    return id;
  }

  public int getMismatchedPrice() {
    return mismatchedPrice;
  }

  public int getMismatchedSeat() {
    return mismatchedSeat;
  }

  @NotNull
  public String getActionName() {
    return actionName;
  }

  public void setActionName(@NotNull String actionName) {
    this.actionName = actionName;
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
  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(@NotNull String showTime) {
    this.showTime = showTime;
  }

  public boolean isSellEnabled() {
    return sellEnable;
  }

  public void setSellEnabled(boolean sellEnabled) {
    this.sellEnable = sellEnabled;
  }

  @NotNull
  public GatewayEventObj getGatewayEvent() {
    return gatewayEvent;
  }

  public void setGatewayEvent(@NotNull GatewayEventObj gatewayEvent) {
    this.gatewayEvent = gatewayEvent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActionEventSync)) return false;
    ActionEventSync that = (ActionEventSync) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
