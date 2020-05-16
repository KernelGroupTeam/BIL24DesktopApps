package server.protocol2.editor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.07.15
 */
public class ActionEventObj implements Filterable, Serializable {
  private static final long serialVersionUID = 6983925316868656785L;
  private static final DateFormat formatDDMMYYYYHHmm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  private long id;
  private long planId;
  @NotNull
  private String planName = "";
  private long actionId;
  @NotNull
  private String actionName = "";
  @NotNull
  private List<CategoryPriceObj> priceList = Collections.emptyList();
  @NotNull
  private String showTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private String sellStartTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private String sellEndTime = "";//в формате дд.мм.гггг чч:мм
  private boolean eTickets;
  private boolean fullNameRequired;
  private boolean phoneRequired;
  private boolean ticketRefundAllowed;
  private boolean ticketReissueAllowed;
  @Nullable
  private Integer maxReserveTime;
  @NotNull
  private BigDecimal vat = BigDecimal.ZERO;
  private boolean sellEnable;//todo rename sellEnabled
  private boolean quota;
  @NoLogging
  private boolean placementPlan;//только для информации
  @NotNull
  private GatewayEventObj gatewayEvent = GatewayEventObj.getDefault();
  private boolean archival;

  public ActionEventObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public long getPlanId() {
    return planId;
  }

  public void setPlanId(long planId) {
    this.planId = planId;
  }

  @NotNull
  public String getPlanName() {
    return planName;
  }

  public void setPlanName(@NotNull String planName) {
    this.planName = planName;
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
  public List<CategoryPriceObj> getPriceList() {
    return priceList;
  }

  public void setPriceList(@NotNull List<CategoryPriceObj> priceList) {
    this.priceList = priceList;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(@NotNull String showTime) {
    this.showTime = showTime;
  }

  @NotNull
  public String getSellStartTime() {
    return sellStartTime;
  }

  public void setSellStartTime(@NotNull String sellStartTime) {
    this.sellStartTime = sellStartTime;
  }

  @NotNull
  public String getSellEndTime() {
    return sellEndTime;
  }

  public void setSellEndTime(@NotNull String sellEndTime) {
    this.sellEndTime = sellEndTime;
  }

  public boolean isETickets() {
    return eTickets;
  }

  public void setETickets(boolean eTickets) {
    this.eTickets = eTickets;
  }

  public boolean isFullNameRequired() {
    return fullNameRequired;
  }

  public void setFullNameRequired(boolean fullNameRequired) {
    this.fullNameRequired = fullNameRequired;
  }

  public boolean isPhoneRequired() {
    return phoneRequired;
  }

  public void setPhoneRequired(boolean phoneRequired) {
    this.phoneRequired = phoneRequired;
  }

  public boolean isTicketRefundAllowed() {
    return ticketRefundAllowed;
  }

  public void setTicketRefundAllowed(boolean ticketRefundAllowed) {
    this.ticketRefundAllowed = ticketRefundAllowed;
  }

  public boolean isTicketReissueAllowed() {
    return ticketReissueAllowed;
  }

  public void setTicketReissueAllowed(boolean ticketReissueAllowed) {
    this.ticketReissueAllowed = ticketReissueAllowed;
  }

  @Nullable
  public Integer getMaxReserveTime() {
    return maxReserveTime;
  }

  public void setMaxReserveTime(@Nullable Integer maxReserveTime) {
    this.maxReserveTime = maxReserveTime;
  }

  @NotNull
  public BigDecimal getVat() {
    return vat;
  }

  public void setVat(@NotNull BigDecimal vat) {
    this.vat = vat;
  }

  public boolean isSellEnabled() {
    return sellEnable;
  }

  public void setSellEnabled(boolean sellEnabled) {
    this.sellEnable = sellEnabled;
  }

  public boolean isQuota() {
    return quota;
  }

  public void setQuota(boolean quota) {
    this.quota = quota;
  }

  public boolean isPlacementPlan() {
    return placementPlan;
  }

  public void setPlacementPlan(boolean placementPlan) {
    this.placementPlan = placementPlan;
  }

  public boolean isCombinedPlan() {
    if (!placementPlan) return false;
    for (int i = priceList.size() - 1; i >= 0; i--) {
      CategoryPriceObj price = priceList.get(i);
      if (!price.isPlacement()) return true;
    }
    return false;
  }

  @NotNull
  public GatewayEventObj getGatewayEvent() {
    return gatewayEvent;
  }

  public void setGatewayEvent(@NotNull GatewayEventObj gatewayEvent) {
    this.gatewayEvent = gatewayEvent;
  }

  public boolean isArchival() {
    return archival;
  }

  public void setArchival(boolean archival) {
    this.archival = archival;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (filter instanceof ActionObj) {
      ActionObj action = (ActionObj) filter;
      return action.getId() == getActionId();
    } else if (filter instanceof SeatingPlanObj) {
      SeatingPlanObj plan = (SeatingPlanObj) filter;
      return plan.getId() == getPlanId();
    } else return false;
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

  @Override
  public String toString() {
    return "[" + planName + "] " + showTime + (sellEnable ? "" : " Отключен");
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
