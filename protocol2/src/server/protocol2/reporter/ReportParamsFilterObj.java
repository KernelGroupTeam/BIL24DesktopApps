package server.protocol2.reporter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.01.2018.
 */
public class ReportParamsFilterObj extends ReportParamsObj {
  private static final long serialVersionUID = 4653589990318253639L;
  @NotNull
  private FilterObj filter;
  private boolean charge;
  private boolean discount;

  public ReportParamsFilterObj(long id, int formId, @NotNull FilterObj filter, boolean charge, boolean discount) {
    super(id, formId);
    this.filter = filter;
    this.charge = charge;
    this.discount = discount;
  }

  @NotNull
  public FilterObj getFilter() {
    return filter;
  }

  public boolean isCharge() {
    return charge;
  }

  public boolean isDiscount() {
    return discount;
  }
}
