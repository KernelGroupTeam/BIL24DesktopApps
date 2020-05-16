package server.protocol2.editor;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 05.11.15
 */
public class EventSeatObj implements Serializable {
  private static final long serialVersionUID = -8679593762681122677L;
  private static final CategoryPriceObj DEF_CATEGORY_PRICE = new CategoryPriceObj(0);
  private long id;
  @NotNull
  private CategoryPriceObj categoryPrice = DEF_CATEGORY_PRICE;
  @NotNull
  private State state = State.INACCESSIBLE;
  private boolean undefinedCategory = false;
  @Nullable
  private SeatLocationObj seatLocation = null;
  @Nullable
  private String barcode = null;

  public EventSeatObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public CategoryPriceObj getCategoryPrice() {
    return categoryPrice;
  }

  public void setCategoryPrice(@NotNull CategoryPriceObj categoryPrice) {
    this.categoryPrice = categoryPrice;
  }

  @NotNull
  public State getState() {
    return state;
  }

  public void setState(@NotNull State state) {
    this.state = state;
  }

  public boolean isUndefinedCategory() {
    return undefinedCategory;
  }

  public void setUndefinedCategory(boolean undefinedCategory) {
    this.undefinedCategory = undefinedCategory;
  }

  @Nullable
  public SeatLocationObj getSeatLocation() {
    return seatLocation;
  }

  public void setSeatLocation(@Nullable SeatLocationObj seatLocation) {
    this.seatLocation = seatLocation;
  }

  @Nullable
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(@Nullable String barcode) {
    this.barcode = barcode;
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
