package server.protocol2.editor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.11.16
 */
public class QuotaOutObj implements Serializable {
  private static final long serialVersionUID = 8606178781398018588L;
  private long actionEventId;
  @NotNull
  private String number;
  @NotNull
  private String date;//в формате дд.мм.гггг
  @NotNull
  private long[] eventSeatIds;//по возрастанию id
  @NotNull
  private List<String> quotaInNumberList;
  private boolean allSeats;
  @NotNull
  private long[] confirmedSeatIds;//по возрастанию id
  private boolean confirmed = false;
  private transient boolean returnFile = false;

  public QuotaOutObj(long actionEventId, @NotNull String number, @NotNull Date date, @NotNull long[] eventSeatIds) {
    this(actionEventId, number, date, eventSeatIds, null, null);
  }

  public QuotaOutObj(long actionEventId, @NotNull String number, @NotNull Date date, @NotNull List<String> quotaInNumberList) {
    this(actionEventId, number, date, null, quotaInNumberList, null);
  }

  public QuotaOutObj(long actionEventId, @NotNull String number, @NotNull Date date) {
    this(actionEventId, number, date, null, null, true);
  }

  private QuotaOutObj(long actionEventId, @NotNull String number, @NotNull Date date, @Nullable long[] eventSeatIds,
                      @Nullable List<String> quotaInNumberList, @Nullable Boolean allSeats) {
    this.actionEventId = actionEventId;
    this.number = number;
    this.date = new SimpleDateFormat("dd.MM.yyyy").format(date);
    if (allSeats != null) {
      this.eventSeatIds = new long[0];
      this.quotaInNumberList = Collections.emptyList();
      this.allSeats = allSeats;
    } else if (quotaInNumberList != null) {
      this.eventSeatIds = new long[0];
      this.quotaInNumberList = quotaInNumberList;
      this.allSeats = false;
    } else if (eventSeatIds != null) {
      this.eventSeatIds = eventSeatIds;
      this.quotaInNumberList = Collections.emptyList();
      this.allSeats = false;
      Arrays.sort(this.eventSeatIds);
    } else throw new IllegalStateException();//не может быть
    this.confirmedSeatIds = new long[0];
  }

  public long getActionEventId() {
    return actionEventId;
  }

  @NotNull
  public String getNumber() {
    return number;
  }

  @NotNull
  public String getDate() {
    return date;
  }

  @NotNull
  public long[] getEventSeatIds() {
    return eventSeatIds;
  }

  @NotNull
  public List<String> getQuotaInNumberList() {
    return quotaInNumberList;
  }

  public boolean isAllSeats() {
    return allSeats;
  }

  @NotNull
  public long[] getConfirmedSeatIds() {
    return confirmedSeatIds;
  }

  public void setConfirmedSeatIds(@NotNull long[] confirmedSeatIds) {
    this.confirmedSeatIds = confirmedSeatIds;
    Arrays.sort(this.confirmedSeatIds);
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public boolean isReturnFile() {
    return returnFile;
  }

  public void setReturnFile(boolean returnFile) {
    this.returnFile = returnFile;
  }
}
