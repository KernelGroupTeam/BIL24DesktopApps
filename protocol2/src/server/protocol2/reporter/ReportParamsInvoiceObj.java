package server.protocol2.reporter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.01.2018.
 */
public class ReportParamsInvoiceObj extends ReportParamsObj {
  private static final long serialVersionUID = 2524397563672738394L;
  private long actionId;
  private long actionEventId;
  @NotNull
  private String quotaNumber;

  public ReportParamsInvoiceObj(long id, int formId, long actionId, long actionEventId, @NotNull String quotaNumber) {
    super(id, formId);
    this.actionId = actionId;
    this.actionEventId = actionEventId;
    this.quotaNumber = quotaNumber;
  }

  public long getActionId() {
    return actionId;
  }

  public long getActionEventId() {
    return actionEventId;
  }

  @NotNull
  public String getQuotaNumber() {
    return quotaNumber;
  }
}
