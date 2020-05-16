package server.protocol2.reporter;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 26.01.2018.
 */
public class ReportParamsQuotaObj extends ReportParamsObj {
  private static final long serialVersionUID = 6512098110189874054L;
  @NotNull
  private RAction action;
  @NotNull
  private Set<Long> allowedSeatIdSet;

  public ReportParamsQuotaObj(long id, int formId, @NotNull RAction action, @NotNull Set<Long> allowedSeatIdSet) {
    super(id, formId);
    this.action = action;
    this.allowedSeatIdSet = allowedSeatIdSet;
  }

  @NotNull
  public RAction getAction() {
    return action;
  }

  @NotNull
  public Set<Long> getAllowedSeatIdSet() {
    return allowedSeatIdSet;
  }
}
