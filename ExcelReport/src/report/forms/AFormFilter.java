package report.forms;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.*;
import server.protocol2.reporter.TicketObj;

/**
 * Created by Inventor on 13.11.2017
 * Класс для отчетов формирующихся по фильтру
 */
public abstract class AFormFilter<Data> extends AForm<Data> {
  @NotNull
  private final Filter filter;//Слепок настроек фильтра
  private final boolean charge;//С учетом сервисного сбора
  private final boolean discount;//С учетом скидки

  protected AFormFilter(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(form, sheetName, sign);
    if (filter == null) throw ValidationException.absent("Фильтр");
    if (charge == null) throw ValidationException.absent("С учетом сервисного сбора");
    if (discount == null) throw ValidationException.absent("С учетом скидки");
    this.filter = filter;
    this.charge = charge;
    this.discount = discount;
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    Period period = filter.getPeriod();
    if (period == null && !filter.isFullReport()) throw new NullPointerException("filter period is null");
    getHeader()
        .add(EHeader.PERIOD, filter.isFullReport() ? "Полный" : "С " + period.getFromFormat() + " по " + period.getToFormat())
        .add(EHeader.ACQUIRING, filter.getAcquiring().getName())
        .add(EHeader.ORGANIZER, filter.getOrganizer().getName())
        .add(EHeader.CITY, filter.getCity().getName())
        .add(EHeader.VENUE, filter.getVenue().getName())
        .add(EHeader.ACTION, filter.getAction().getName())
        .add(EHeader.ACTION_EVENT, filter.getActionEvent().getShowTime())
        .add(EHeader.AGENT, filter.getAgent().getName())
        .add(EHeader.FRONTEND, filter.getFrontend().getName())
        .add(EHeader.SYSTEM, filter.getSystem().getName())
        .add(EHeader.GATEWAY, filter.getGateway().getName())
        .add(EHeader.FULL_REPORT, filter.isFullReport())
        .add(EHeader.ALL_STATUSES, filter.isAllStatuses());
    if (filter.getPeriodType() != null) getHeader().add(EHeader.PERIOD_TYPE, filter.getPeriodType().getName());
  }

  @NotNull
  public final Filter getFilter() {
    return filter;
  }

  public final boolean isCharge() {
    return charge;
  }

  public final boolean isDiscount() {
    return discount;
  }

  @NotNull
  protected final BigDecimal getPrice(@NotNull TicketObj ticket) {
    return getPrice(ticket, charge, discount);
  }

  @NotNull
  protected static BigDecimal getPrice(@NotNull TicketObj ticket, boolean charge, boolean discount) {
    BigDecimal result = ticket.getPrice();
    if (charge) result = result.add(ticket.getCharge());
    if (discount) result = result.subtract(ticket.getDiscount());
    return result;
  }
}
