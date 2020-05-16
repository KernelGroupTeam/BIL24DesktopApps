package report.models;

import java.util.Date;

import org.jetbrains.annotations.*;
import report.exceptions.ValidationException;
import report.reporter.enums.EPeriodType;
import server.protocol2.common.*;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 19.11.2017
 */
public final class Filter {
  @NotNull
  public static final AcquiringObj DEF_ACQUIRING = AcquiringObj.getInstance(-11, "любое значение");//Объект значения по умолчанию
  @NotNull
  public static final AcquiringObj ANY_ACQUIRING = AcquiringObj.getInstance(-1, "любой эквайринг");//Объект значения по умолчанию
  @NotNull
  public static final ROrganizer DEF_ORGANIZER = new ROrganizer(0, "любой");//Объект значения по умолчанию
  @NotNull
  public static final RCity DEF_CITY = new RCity(0, "любой");//Объект значения по умолчанию
  @NotNull
  public static final RVenue DEF_VENUE = new RVenue(0, 0, "любое");//Объект значения по умолчанию
  @NotNull
  public static final RAction DEF_ACTION = new RAction(0, 0, "", KindObj.getInstance(-1, ""), "любое");//Объект значения по умолчанию
  @NotNull
  public static final RActionEvent DEF_ACTION_EVENT = new RActionEvent(0, 0, "любой");//Объект значения по умолчанию
  @NotNull
  public static final RAgent DEF_AGENT = new RAgent(0, "любой");//Объект значения по умолчанию
  @NotNull
  public static final RFrontend DEF_FRONTEND = new RFrontend(0, 0, "любой", FrontendType.getInstance(0, ""));//Объект значения по умолчанию
  @NotNull
  public static final GSystemObj DEF_SYSTEM = GSystemObj.getInstance(-11, "любое значение");//Объект значения по умолчанию
  @NotNull
  public static final GSystemObj ANY_SYSTEM = GSystemObj.getInstance(-1, "любой шлюз");//Объект значения по умолчанию
  @NotNull
  public static final GatewayObj DEF_GATEWAY = GatewayObj.getInstance(-11, -11, "любое значение", null, null);//Объект значения по умолчанию
  @NotNull
  public static final GatewayObj ANY_GATEWAY = GatewayObj.getInstance(-1, -1, "любое подключение", null, null);//Объект значения по умолчанию

  @Nullable
  private final Period period;//Период дат "От" и "До". При fullReport == true имеет null значение
  @Nullable
  private final EPeriodType periodType;//Период. При fullReport == true имеет null значение
  @NotNull
  private final AcquiringObj acquiring;//Эквайринг
  @NotNull
  private final ROrganizer organizer;//Организатор
  @NotNull
  private final RCity city;//Город
  @NotNull
  private final RVenue venue;//Место
  @NotNull
  private final RAction action;//Представление
  @NotNull
  private final RActionEvent actionEvent;//Сеанс
  @NotNull
  private final RAgent agent;//Агент
  @NotNull
  private final RFrontend frontend;//Интерфейс
  @NotNull
  private final GSystemObj system;//ВБС
  @NotNull
  private final GatewayObj gateway;//Подключение ВБС
  private final boolean fullReport;//Все продажи
  private final boolean allStatuses;//Все статусы

  public Filter(@Nullable Long dateFrom, @Nullable Long dateTo, @Nullable Integer periodTypeId, @Nullable AcquiringObj acquiring,
                @Nullable ROrganizer organizer, @Nullable RCity city, @Nullable RVenue venue, @Nullable RAction action, @Nullable RActionEvent actionEvent,
                @Nullable RAgent agent, @Nullable RFrontend frontend, @Nullable GSystemObj system, @Nullable GatewayObj gateway,
                @Nullable Boolean fullReport, @Nullable Boolean allStatuses) throws ValidationException {
    this(dateFrom == null ? null : new Date(dateFrom),
        dateTo == null ? null : new Date(dateTo),
        periodTypeId == null ? null : EPeriodType.getPeriodTypeById(periodTypeId),
        acquiring, organizer, city, venue, action, actionEvent, agent, frontend, system, gateway, fullReport, allStatuses);
  }

  public Filter(@Nullable Date dateFrom, @Nullable Date dateTo, @Nullable EPeriodType periodType, @Nullable AcquiringObj acquiring,
                @Nullable ROrganizer organizer, @Nullable RCity city, @Nullable RVenue venue, @Nullable RAction action, @Nullable RActionEvent actionEvent,
                @Nullable RAgent agent, @Nullable RFrontend frontend, @Nullable GSystemObj system, @Nullable GatewayObj gateway,
                @Nullable Boolean fullReport, @Nullable Boolean allStatuses) throws ValidationException {
    if (fullReport == null) throw ValidationException.absent("Все продажи");
    if (fullReport) period = null;
    else {
      period = Period.create(dateFrom, dateTo);
      if (periodType == null) throw ValidationException.absent("Период");
    }
    if (acquiring == null) throw ValidationException.absent("Эквайринг");
    if (organizer == null) throw ValidationException.absent("Организатор");
    if (city == null) throw ValidationException.absent("Город");
    if (venue == null) throw ValidationException.absent("Место");
    if (action == null) throw ValidationException.absent("Представление");
    if (actionEvent == null) throw ValidationException.absent("Сеанс");
    if (agent == null) throw ValidationException.absent("Агент");
    if (frontend == null) throw ValidationException.absent("Интерфейс");
    if (system == null) throw ValidationException.absent("Шлюз ВБС");
    if (gateway == null) throw ValidationException.absent("Подключение ВБС");
    if (allStatuses == null) throw ValidationException.absent("Все статусы");
    this.periodType = periodType;
    this.acquiring = DEF_ACQUIRING.equals(acquiring) ? DEF_ACQUIRING : (ANY_ACQUIRING.equals(acquiring) ? ANY_ACQUIRING : acquiring);
    this.organizer = DEF_ORGANIZER.equals(organizer) ? DEF_ORGANIZER : organizer;
    this.city = DEF_CITY.equals(city) ? DEF_CITY : city;
    this.venue = DEF_VENUE.equals(venue) ? DEF_VENUE : venue;
    this.action = DEF_ACTION.equals(action) ? DEF_ACTION : action;
    this.actionEvent = DEF_ACTION_EVENT.equals(actionEvent) ? DEF_ACTION_EVENT : actionEvent;
    this.agent = DEF_AGENT.equals(agent) ? DEF_AGENT : agent;
    this.frontend = DEF_FRONTEND.equals(frontend) ? DEF_FRONTEND : frontend;
    this.system = DEF_SYSTEM.equals(system) ? DEF_SYSTEM : (ANY_SYSTEM.equals(system) ? ANY_SYSTEM : system);
    this.gateway = DEF_GATEWAY.equals(gateway) ? DEF_GATEWAY : (ANY_GATEWAY.equals(gateway) ? ANY_GATEWAY : gateway);
    this.fullReport = fullReport;
    this.allStatuses = allStatuses;
  }

  @Nullable
  public Period getPeriod() {
    return period;
  }

  @Nullable
  public EPeriodType getPeriodType() {
    return periodType;
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
}
