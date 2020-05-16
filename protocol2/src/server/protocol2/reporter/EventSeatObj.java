package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 16.12.2016.
 */
public class EventSeatObj implements Serializable {
  private static final long serialVersionUID = 975112725344088418L;
  private long id;
  @NotNull
  private State state = State.INACCESSIBLE;
  @NotNull
  private BigDecimal price = BigDecimal.ZERO;

  public EventSeatObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public State getState() {
    return state;
  }

  public void setState(@NotNull State state) {
    this.state = state;
  }

  @NotNull
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@NotNull BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventSeatObj)) return false;
    EventSeatObj that = (EventSeatObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  public enum State {
    INACCESSIBLE(0, "Недоступно"),
    AVAILABLE(1, "Доступно"),
    PRE_RESERVED(2, "Предбронировано"),
    RESERVED(3, "Бронировано"),
    OCCUPIED(4, "Продано"),
    REFUND(5, "Квота возвращена");
    private static final State[] en;

    static {
      en = new State[]{INACCESSIBLE, AVAILABLE, PRE_RESERVED, RESERVED, OCCUPIED, REFUND};
      for (int i = 0; i < en.length; i++) {
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<State> enList = Arrays.asList(en);
      for (State value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    State(int id, @NotNull String desc) {
      this.id = id;
      this.desc = desc;
    }

    public int getId() {
      return id;
    }

    @NotNull
    public String getDesc() {
      return desc;
    }

    @Override
    public String toString() {
      return desc;
    }

    @NotNull
    public static State getState(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
