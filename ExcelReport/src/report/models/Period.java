package report.models;

import java.text.ParseException;
import java.util.*;

import org.jetbrains.annotations.*;
import report.exceptions.ValidationException;
import report.utils.DateFormats;
import server.protocol2.reporter.OrderObj;

/**
 * Created by Inventor on 09.06.2019
 */
public final class Period {
  @NotNull
  private final Date from;
  @NotNull
  private final Date to;
  @Nullable
  private transient String fromFormat;
  @Nullable
  private transient String toFormat;

  @NotNull
  public static Period create(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");
    Date from = null;
    Date to = null;
    for (OrderObj order : orderList) {
      Date orderDate;
      try {
        orderDate = OrderObj.parseFormat(order.getDate());
      } catch (ParseException e) {
        throw ValidationException.incorrect(order.getDate(), "Дата заказа");
      }
      if (from == null) {
        from = orderDate;
        to = orderDate;
      } else {
        if (orderDate.before(from)) from = orderDate;
        else if (orderDate.after(to)) to = orderDate;
      }
    }
    return create(from, to);
  }

  @NotNull
  static Period create(@Nullable Date from, @Nullable Date to) throws ValidationException {
    if (from == null) throw ValidationException.absent("Период От");
    if (to == null) throw ValidationException.absent("Период До");
    if (from.after(to)) return new Period(to, from);
    return new Period(from, to);
  }

  private Period(@NotNull Date from, @NotNull Date to) {
    this.from = from;
    this.to = to;
  }

  @NotNull
  public Date getFrom() {
    return from;
  }

  @NotNull
  public Date getTo() {
    return to;
  }

  @NotNull
  public String getFromFormat() {
    if (fromFormat == null) fromFormat = format(from);
    return fromFormat;
  }

  @NotNull
  public String getToFormat() {
    if (toFormat == null) toFormat = format(to);
    return toFormat;
  }

  @NotNull
  private static String format(@NotNull Date date) {
    return DateFormats.format(date, DateFormats.ETemplate.ddMMyyyyHHmmss);
  }
}
