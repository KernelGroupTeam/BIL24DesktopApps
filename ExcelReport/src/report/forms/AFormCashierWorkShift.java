package report.forms;

import java.math.BigDecimal;
import java.util.List;

import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 20.09.2018
 * Класс для отчетов формирующихся по смене кассира
 */
public abstract class AFormCashierWorkShift<Data> extends AForm<Data> {
  @NotNull
  private final RCashierWorkShift cashierWorkShift;//Смена
  private final boolean charge;//С учетом сервисного сбора
  private final boolean discount;//С учетом скидки

  protected AFormCashierWorkShift(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign, @Nullable RCashierWorkShift cashierWorkShift, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(form, sheetName, sign);
    if (cashierWorkShift == null) throw ValidationException.absent("Смена");
    if (charge == null) throw ValidationException.absent("С учетом сервисного сбора");
    if (discount == null) throw ValidationException.absent("С учетом скидки");
    this.cashierWorkShift = cashierWorkShift;
    this.charge = charge;
    this.discount = discount;
  }

  @Override
  protected final void fillHeader() {
    super.fillHeader();
    List<RFrontend> frontendList = cashierWorkShift.getFrontendList();
    String frontendString = "";
    for (RFrontend frontend : frontendList) {
      //noinspection StringConcatenationInLoop
      frontendString += frontend.getName() + ", ";
    }
    if (!frontendString.isEmpty()) frontendString = frontendString.substring(0, frontendString.length() - 2);
    getHeader()
        .add(EHeader.PERIOD, "С " + cashierWorkShift.getStartDate() + " по " + cashierWorkShift.getEndDate())
        .add(EHeader.AGENT, cashierWorkShift.getAgent().getName())
        .add(EHeader.FRONTEND, frontendString)
        .add(EHeader.CHARGE, charge)
        .add(EHeader.DISCOUNT, discount);
  }

  @NotNull
  public final RCashierWorkShift getCashierWorkShift() {
    return cashierWorkShift;
  }

  public final boolean isCharge() {
    return charge;
  }

  public final boolean isDiscount() {
    return discount;
  }

  @NotNull
  protected final BigDecimal getPrice(@NotNull TicketObj ticket) {
    BigDecimal result = ticket.getPrice();
    if (this.charge) result = result.add(ticket.getCharge());
    if (this.discount) result = result.subtract(ticket.getDiscount());
    return result;
  }
}
