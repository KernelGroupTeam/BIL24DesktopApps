package server.protocol2.reporter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 20.09.2018.
 */
public class ReportParamsCashierWorkShiftObj extends ReportParamsObj {
  private static final long serialVersionUID = 7873341589597382391L;
  @NotNull
  private RCashierWorkShift cashierWorkShift;
  private boolean charge;
  private boolean discount;

  public ReportParamsCashierWorkShiftObj(long id, int formId, @NotNull RCashierWorkShift cashierWorkShift, boolean charge, boolean discount) {
    super(id, formId);
    this.cashierWorkShift = cashierWorkShift;
    this.charge = charge;
    this.discount = discount;
  }

  @NotNull
  public RCashierWorkShift getCashierWorkShift() {
    return cashierWorkShift;
  }

  public boolean isCharge() {
    return charge;
  }

  public boolean isDiscount() {
    return discount;
  }
}
