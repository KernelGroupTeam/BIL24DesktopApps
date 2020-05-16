package server.protocol2.reporter;

import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 26.01.2018.
 */
public class ReportParamsQuotaSaleObj extends ReportParamsObj {
  private static final long serialVersionUID = 7258593128648697589L;
  @NotNull
  private RAction action;
  @Nullable
  private Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap;

  public ReportParamsQuotaSaleObj(long id, int formId, @NotNull RAction action, @Nullable Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap) {
    super(id, formId);
    this.action = action;
    this.actionEventQuotaMap = actionEventQuotaMap;
  }

  @NotNull
  public RAction getAction() {
    return action;
  }

  @Nullable
  public Map<Integer, Map<Long, Set<String>>> getActionEventQuotaMap() {
    return actionEventQuotaMap;
  }

  @Nullable
  public Map<Long, Set<String>> getActionEventQuotaMap(int invoiceTypeId) {
    if (actionEventQuotaMap == null) return null;
    return actionEventQuotaMap.get(invoiceTypeId);
  }
}
