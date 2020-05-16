package server.protocol2.cassa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 18.10.2018
 */
public class CassaReserve implements Serializable {
  private static final long serialVersionUID = 4738210461197647518L;
  private int cartTimeout;//Время жизни резерва в секундах
  @NotNull
  private List<CassaAction<CassaActionEvent>> actionList;//Список представлений забронированных мест
  @NotNull
  private Map<Long, List<CassaSeatReserve>> seatMap;//Карта соответствий сеанса представления и забронированных мест на этом сеансе. key - идентификатор сеанса
  @Nullable
  private transient BigDecimal price;//Номинальная стоимость забронированных мест
  @Nullable
  private transient BigDecimal serviceCharge;//Сервисный сбор забронированных мест

  public CassaReserve(int cartTimeout, @NotNull List<CassaAction<CassaActionEvent>> actionList, @NotNull Map<Long, List<CassaSeatReserve>> seatMap) {
    this.cartTimeout = cartTimeout;
    this.actionList = actionList;
    this.seatMap = seatMap;
  }

  public int getCartTimeout() {
    return cartTimeout;
  }

  @NotNull
  public List<CassaAction<CassaActionEvent>> getActionList() {
    return actionList;
  }

  @NotNull
  public Map<Long, List<CassaSeatReserve>> getSeatMap() {
    return seatMap;
  }

  @NotNull
  public BigDecimal getPrice() {
    if (price == null) {
      BigDecimal price = BigDecimal.ZERO;
      for (List<CassaSeatReserve> seatList : seatMap.values()) {
        for (CassaSeatReserve seat : seatList) {
          price = price.add(seat.getCategoryPrice().getPrice());
        }
      }
      this.price = price;
    }
    return price;
  }

  @NotNull
  public BigDecimal getServiceCharge() {
    if (serviceCharge == null) {
      BigDecimal serviceCharge = BigDecimal.ZERO;
      for (List<CassaSeatReserve> seatList : seatMap.values()) {
        for (CassaSeatReserve seat : seatList) {
          serviceCharge = serviceCharge.add(seat.getServiceCharge());
        }
      }
      this.serviceCharge = serviceCharge;
    }
    return serviceCharge;
  }

  @Override
  public String toString() {
    return "CassaReserve{cartTimeout=" + cartTimeout + ", actionList=" + actionList.size() + ", seatMap=" + seatMap.size() + '}';
  }
}
