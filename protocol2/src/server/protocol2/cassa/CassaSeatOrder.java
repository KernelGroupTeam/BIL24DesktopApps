package server.protocol2.cassa;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 30.01.2019
 */
public class CassaSeatOrder extends CassaSeatReserve {
  private static final long serialVersionUID = -3074258387433209494L;
  @NotNull
  private BigDecimal discount;//Скидка места
  @NotNull
  private CassaAction<CassaActionEvent> action;//Представление места. В списке сеансов строго только один сеанс.

  public CassaSeatOrder(long id, @NotNull CassaCategoryPrice categoryPrice, @Nullable CassaSeatLocation location,
                        @NotNull BigDecimal discount, @NotNull BigDecimal serviceCharge, @NotNull CassaAction<CassaActionEvent> action) {
    super(id, categoryPrice, location, serviceCharge);
    this.discount = discount;
    this.action = action;
  }

  @NotNull
  public BigDecimal getDiscount() {
    return discount;
  }

  @NotNull
  public CassaAction<CassaActionEvent> getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "CassaSeatReserve{id=" + getId() + ", categoryPrice=" + getCategoryPrice() + ", location=" + getLocation() +
        ", discount=" + discount + ", serviceCharge=" + getServiceCharge() + ", action=" + action + '}';
  }
}
