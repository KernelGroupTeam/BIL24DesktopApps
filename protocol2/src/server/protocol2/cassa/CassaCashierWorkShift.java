package server.protocol2.cassa;

import java.io.Serializable;
import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 29.08.2018
 */
public class CassaCashierWorkShift implements Serializable {
  private static final long serialVersionUID = -923005200126262139L;
  @NotNull
  private static final DateFormat formatDDMMYYYYHHMMSS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");//Формат дат кассовой смены
  private long id;//Идентификатор кассовой смены
  @Nullable
  private String kktSerialNumber;//Серийный номер ККТ
  private long cassaSessionId;//Идентификатор сессии входа в кассу(идентификатор сессии кассы)
  @NotNull
  private String startDate;//Дата старта кассовой смены
  @Nullable
  private String endDate;//Дата окончания кассовой смены

  public CassaCashierWorkShift(long id, @Nullable String kktSerialNumber, long cassaSessionId, long startDate, @Nullable Long endDate) {
    this.id = id;
    this.kktSerialNumber = kktSerialNumber;
    this.cassaSessionId = cassaSessionId;
    this.startDate = formatDate(startDate);
    this.endDate = endDate == null ? null : formatDate(endDate);
  }

  public long getId() {
    return id;
  }

  @Nullable
  public String getKktSerialNumber() {
    return kktSerialNumber;
  }

  public long getCassaSessionId() {
    return cassaSessionId;
  }

  @NotNull
  public String getStartDate() {
    return startDate;
  }

  @Nullable
  public String getEndDate() {
    return endDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CassaCashierWorkShift that = (CassaCashierWorkShift) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaCashierWorkShift{id=" + id + ", kktSerialNumber=" + kktSerialNumber + ", cassaSessionId=" + cassaSessionId + ", startDate=" + startDate + ", endDate=" + endDate + '}';
  }

  @NotNull
  private static synchronized String formatDate(long date) {
    return formatDDMMYYYYHHMMSS.format(new Date(date));
  }

  @NotNull
  public static synchronized Date parseDate(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHMMSS.parse(date);
  }
}
