package report.reporter.managers;

import java.util.Date;

import org.jetbrains.annotations.*;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.reporter.enums.EPeriodType;
import server.protocol2.common.*;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 13.11.2017
 */
public final class FilterManager {
  @Nullable
  private static Date dateFrom;//Дата От
  @Nullable
  private static Date dateTo;//Дата До
  @Nullable
  private static EPeriodType periodType;//Период
  @Nullable
  private static AcquiringObj acquiring;//Эквайринг
  @Nullable
  private static ROrganizer organizer;//Организатор
  @Nullable
  private static RCity city;//Город
  @Nullable
  private static RVenue venue;//Место
  @Nullable
  private static RAction action;//Представление
  @Nullable
  private static RActionEvent actionEvent;//Сеанс
  @Nullable
  private static RAgent agent;//Агент
  @Nullable
  private static RFrontend frontend;//Интерфейс
  @Nullable
  private static GSystemObj system;//ВБС
  @Nullable
  private static GatewayObj gateway;//Подключение ВБС
  @Nullable
  private static Boolean fullReport;//Все продажи
  @Nullable
  private static Boolean allStatuses;//Все статусы

  private FilterManager() {
  }

  public static void setDateFrom(@Nullable Date dateFrom) {
    FilterManager.dateFrom = dateFrom;
  }

  public static void setDateTo(@Nullable Date dateTo) {
    FilterManager.dateTo = dateTo;
  }

  public static void setPeriodType(@Nullable String periodTypeName) {
    if (periodTypeName == null) FilterManager.periodType = null;
    else FilterManager.periodType = EPeriodType.valueOf(periodTypeName);
  }

  public static void setAcquiring(@Nullable AcquiringObj acquiring) {
    FilterManager.acquiring = acquiring;
  }

  public static void setOrganizer(@Nullable ROrganizer organizer) {
    FilterManager.organizer = organizer;
  }

  public static void setCity(@Nullable RCity city) {
    FilterManager.city = city;
  }

  public static void setVenue(@Nullable RVenue venue) {
    FilterManager.venue = venue;
  }

  public static void setAction(@Nullable RAction action) {
    FilterManager.action = action;
  }

  public static void setActionEvent(@Nullable RActionEvent actionEvent) {
    FilterManager.actionEvent = actionEvent;
  }

  public static void setAgent(@Nullable RAgent agent) {
    FilterManager.agent = agent;
  }

  public static void setFrontend(@Nullable RFrontend frontend) {
    FilterManager.frontend = frontend;
  }

  public static void setSystem(@Nullable GSystemObj system) {
    FilterManager.system = system;
  }

  public static void setGateway(@Nullable GatewayObj gateway) {
    FilterManager.gateway = gateway;
  }

  public static void setFullReport(@Nullable Boolean fullReport) {
    FilterManager.fullReport = fullReport;
  }

  public static void setAllStatuses(@Nullable Boolean allStatuses) {
    FilterManager.allStatuses = allStatuses;
  }

  @NotNull
  public static Filter getFilter() throws ValidationException {
    return new Filter(dateFrom, dateTo, periodType, acquiring, organizer, city, venue, action, actionEvent, agent, frontend, system, gateway, fullReport, allStatuses);
  }
}
