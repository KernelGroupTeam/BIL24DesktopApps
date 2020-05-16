package server.protocol2.editor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.11.16
 */
public abstract class QuotaInObj implements Serializable {
  private static final long serialVersionUID = -8855676412550527618L;
  private long actionEventId;
  @NotNull
  private String number;
  @NotNull
  private String date;//в формате дд.мм.гггг
  private boolean confirmed = false;

  public QuotaInObj(long actionEventId, @NotNull String number, @NotNull Date date) {
    this.actionEventId = actionEventId;
    this.number = number;
    this.date = new SimpleDateFormat("dd.MM.yyyy").format(date);
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

  public boolean isConfirmed() {
    return confirmed;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }
}
