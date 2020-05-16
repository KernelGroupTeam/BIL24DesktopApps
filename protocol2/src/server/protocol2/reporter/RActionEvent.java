package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.10.15
 */
public class RActionEvent implements Filterable, Serializable {
  private static final long serialVersionUID = -4437386227894603179L;
  private long id;
  private long actionId;
  @NotNull
  private String showTime;//в формате дд.мм.гггг чч:мм
  private boolean sellEnd;//продажи закончились
  private boolean quota;//сеанс использовует квоту

  public RActionEvent(long id, long actionId, @NotNull String showTime) {
    this.id = id;
    this.actionId = actionId;
    this.showTime = showTime;
  }

  public long getId() {
    return id;
  }

  public long getActionId() {
    return actionId;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  public boolean isSellEnd() {
    return sellEnd;
  }

  public void setSellEnd(boolean sellEnd) {
    this.sellEnd = sellEnd;
  }

  public boolean isQuota() {
    return quota;
  }

  public void setQuota(boolean quota) {
    this.quota = quota;
  }

  public boolean pass(@Nullable RAction action) {
    return action != null && action.getId() != 0 && action.getId() == actionId;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof RAction)) return false;
    return pass((RAction) filter);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RActionEvent)) return false;
    RActionEvent that = (RActionEvent) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return showTime;
  }
}
