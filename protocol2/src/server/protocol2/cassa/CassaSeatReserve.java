package server.protocol2.cassa;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 05.11.2018
 */
public class CassaSeatReserve extends CassaSeat {
  private static final long serialVersionUID = 4251562975573343321L;
  @NotNull
  private BigDecimal serviceCharge;//Сервисный сбор места

  public CassaSeatReserve(long id, @NotNull CassaCategoryPrice categoryPrice, @Nullable CassaSeatLocation location, @NotNull BigDecimal serviceCharge) {
    super(id, categoryPrice, location);
    this.serviceCharge = serviceCharge;
  }

  @NotNull
  public BigDecimal getServiceCharge() {
    return serviceCharge;
  }

  @Override
  public String toString() {
    return "CassaSeatReserve{id=" + getId() + ", categoryPrice=" + getCategoryPrice() + ", location=" + getLocation() + ", serviceCharge=" + serviceCharge + '}';
  }
}
