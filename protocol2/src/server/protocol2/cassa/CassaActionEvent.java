package server.protocol2.cassa;

import java.io.Serializable;
import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 13.10.2018
 */
public class CassaActionEvent implements Serializable {
  private static final long serialVersionUID = -6447252532528831615L;
  @NotNull
  private static final DateFormat formatDDMMYYYYHHMM = new SimpleDateFormat("dd.MM.yyyyHH:mm");//Формат полной даты сеанса
  @NotNull
  private static final DateFormat formatDDMMYYYY = new SimpleDateFormat("dd.MM.yyyy");//Формат даты сеанса
  @NotNull
  private static final DateFormat formatHHMM = new SimpleDateFormat("HH:mm");//Формат времени сеанса
  private long id;//Идентификатор сеанса
  @NotNull
  private String day;//Дата сеанса
  @NotNull
  private String time;//Время сеанса
  @NotNull
  private CassaVenue venue;//Площадка(место проведения) сеанса
  private boolean fullNameRequired;//Обязательность указания имени покупателя на билете
  private boolean phoneRequired;//Обязательность указания телефона покупателя на билете

  public CassaActionEvent(long id, long showTime, @NotNull CassaVenue venue, boolean fullNameRequired, boolean phoneRequired) {
    this.id = id;
    this.day = formatDate(showTime, formatDDMMYYYY);
    this.time = formatDate(showTime, formatHHMM);
    this.venue = venue;
    this.fullNameRequired = fullNameRequired;
    this.phoneRequired = phoneRequired;
  }

  public final long getId() {
    return id;
  }

  @NotNull
  public final synchronized Date getShowTime() {
    try {
      return formatDDMMYYYYHHMM.parse(day + time);
    } catch (ParseException e) {
      throw new RuntimeException(e);//Не должно возникать
    }
  }

  @NotNull
  public final String getDay() {
    return day;
  }

  @NotNull
  public final String getTime() {
    return time;
  }

  @NotNull
  public final CassaVenue getVenue() {
    return venue;
  }

  public final boolean isFullNameRequired() {
    return fullNameRequired;
  }

  public boolean isPhoneRequired() {
    return phoneRequired;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CassaActionEvent)) return false;
    CassaActionEvent that = (CassaActionEvent) o;
    return id == that.id;
  }

  @Override
  public final int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaActionEvent{id=" + id + ", day=" + day + ", time=" + time + ", venue=" + venue + ", fullNameRequired=" + fullNameRequired + ", phoneRequired=" + phoneRequired + '}';
  }

  @NotNull
  private static synchronized String formatDate(long date, @NotNull DateFormat format) {
    return format.format(new Date(date));
  }
}
