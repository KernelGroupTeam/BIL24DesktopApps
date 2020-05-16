package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.common.*;

/**
 * Created by Inventor on 24.01.2018.
 */
public class FilterObj implements Serializable {
  private static final long serialVersionUID = 46747845053434039L;
  @Nullable
  private Long dateFrom;
  @Nullable
  private Long dateTo;
  @Nullable
  private Integer periodTypeId;
  @NotNull
  private AcquiringObj acquiring;
  @NotNull
  private ROrganizer organizer;
  @NotNull
  private RCity city;
  @NotNull
  private RVenue venue;
  @NotNull
  private RAction action;
  @NotNull
  private RActionEvent actionEvent;
  @NotNull
  private RAgent agent;
  @NotNull
  private RFrontend frontend;
  @NotNull
  private GSystemObj system;
  @NotNull
  private GatewayObj gateway;
  private boolean fullReport;
  private boolean allStatuses;

  public FilterObj(@Nullable Long dateFrom, @Nullable Long dateTo, @Nullable Integer periodTypeId,
                   @NotNull AcquiringObj acquiring, @NotNull ROrganizer organizer, @NotNull RCity city, @NotNull RVenue venue,
                   @NotNull RAction action, @NotNull RActionEvent actionEvent, @NotNull RAgent agent, @NotNull RFrontend frontend,
                   @NotNull GSystemObj system, @NotNull GatewayObj gateway, boolean fullReport, boolean allStatuses) {
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.periodTypeId = periodTypeId;
    this.acquiring = acquiring;
    this.organizer = organizer;
    this.city = city;
    this.venue = venue;
    this.action = action;
    this.actionEvent = actionEvent;
    this.agent = agent;
    this.frontend = frontend;
    this.system = system;
    this.gateway = gateway;
    this.fullReport = fullReport;
    this.allStatuses = allStatuses;
  }

  @Nullable
  public Long getDateFrom() {
    return dateFrom;
  }

  @Nullable
  public Long getDateTo() {
    return dateTo;
  }

  @Nullable
  public Integer getPeriodTypeId() {
    return periodTypeId;
  }

  @NotNull
  public AcquiringObj getAcquiring() {
    return acquiring;
  }

  @NotNull
  public ROrganizer getOrganizer() {
    return organizer;
  }

  @NotNull
  public RCity getCity() {
    return city;
  }

  @NotNull
  public RVenue getVenue() {
    return venue;
  }

  @NotNull
  public RAction getAction() {
    return action;
  }

  @NotNull
  public RActionEvent getActionEvent() {
    return actionEvent;
  }

  @NotNull
  public RAgent getAgent() {
    return agent;
  }

  @NotNull
  public RFrontend getFrontend() {
    return frontend;
  }

  @NotNull
  public GSystemObj getSystem() {
    if (system == null) return GSystemObj.getInstance(-11, "любое значение");//todo temp
    return system;
  }

  @NotNull
  public GatewayObj getGateway() {
    return gateway;
  }

  public boolean isFullReport() {
    return fullReport;
  }

  public boolean isAllStatuses() {
    return allStatuses;
  }

  @Override
  public String toString() {
    return "FilterObj{" +
        "dateFrom=" + dateFrom +
        ", dateTo=" + dateTo +
        ", periodTypeId=" + periodTypeId +
        ", acquiringId=" + acquiring.getId() +
        ", organizerId=" + organizer.getId() +
        ", cityId=" + city.getId() +
        ", venueId=" + venue.getId() +
        ", actionId=" + action.getId() +
        ", actionEventId=" + actionEvent.getId() +
        ", agentId=" + agent.getId() +
        ", frontendId=" + frontend.getId() +
        ", systemId=" + system.getId() +
        ", gatewayId=" + gateway.getId() +
        ", fullReport=" + fullReport +
        ", allStatuses=" + allStatuses +
        '}';
  }
}
