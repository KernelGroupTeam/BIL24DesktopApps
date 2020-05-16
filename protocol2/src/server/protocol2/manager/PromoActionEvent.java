package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.03.19
 */
public class PromoActionEvent implements PromoScope, Serializable {
  private static final long serialVersionUID = 4722181945551821869L;
  private long id;
  private long actionId;
  @NotNull
  private String name;
  @NotNull
  private String showTime;//в формате дд.мм.гггг чч:мм

  public PromoActionEvent(long id, long actionId, @NotNull String name, @NotNull String showTime) {
    this.id = id;
    this.actionId = actionId;
    this.name = name;
    this.showTime = showTime;
  }

  @Override
  public long getId() {
    return id;
  }

  public long getActionId() {
    return actionId;
  }

  @Override
  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PromoActionEvent)) return false;
    PromoActionEvent that = (PromoActionEvent) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
