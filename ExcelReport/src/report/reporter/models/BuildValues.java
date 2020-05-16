package report.reporter.models;

import java.util.*;

import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.models.Filter;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 28.01.2018
 */
public final class BuildValues {
  @NotNull
  private final Map<EBuilderKey, Object> values = new EnumMap<>(EBuilderKey.class);

  public BuildValues() {
  }

  @Nullable
  public EForm getForm() {
    return (EForm) values.get(EBuilderKey.FORM);
  }

  @NotNull
  public BuildValues setForm(@Nullable EForm value) {
    values.put(EBuilderKey.FORM, value);
    return this;
  }

  @Nullable
  public String getSign() {
    return (String) values.get(EBuilderKey.SIGN);
  }

  @NotNull
  public BuildValues setSign(@Nullable String value) {
    values.put(EBuilderKey.SIGN, value);
    return this;
  }

  @Nullable
  public Filter getFilter() {
    return (Filter) values.get(EBuilderKey.FILTER);
  }

  @NotNull
  public BuildValues setFilter(@Nullable Filter value) {
    values.put(EBuilderKey.FILTER, value);
    return this;
  }

  @Nullable
  public Boolean getCharge() {
    return (Boolean) values.get(EBuilderKey.CHARGE);
  }

  @NotNull
  public BuildValues setCharge(@Nullable Boolean value) {
    values.put(EBuilderKey.CHARGE, value);
    return this;
  }

  @Nullable
  public Boolean getDiscount() {
    return (Boolean) values.get(EBuilderKey.DISCOUNT);
  }

  @NotNull
  public BuildValues setDiscount(@Nullable Boolean value) {
    values.put(EBuilderKey.DISCOUNT, value);
    return this;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public List<OrderObj> getOrderList() {
    return (List<OrderObj>) values.get(EBuilderKey.ORDER_LIST);
  }

  @NotNull
  public BuildValues setOrderList(@Nullable List<OrderObj> value) {
    values.put(EBuilderKey.ORDER_LIST, value);
    return this;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Map<Long, List<EventSeatObj>> getActionEventSeatMap() {
    return (Map<Long, List<EventSeatObj>>) values.get(EBuilderKey.ACTION_EVENT_SEAT_MAP);
  }

  @NotNull
  public BuildValues setActionEventSeatMap(@Nullable Map<Long, List<EventSeatObj>> value) {
    values.put(EBuilderKey.ACTION_EVENT_SEAT_MAP, value);
    return this;
  }

  @SuppressWarnings("unchecked")
  public List<TicketObj> getTicketList() {
    return (List<TicketObj>) values.get(EBuilderKey.TICKET_LIST);
  }

  @NotNull
  public BuildValues setTicketList(@Nullable List<TicketObj> value) {
    values.put(EBuilderKey.TICKET_LIST, value);
    return this;
  }

  @Nullable
  public RAction getAction() {
    return (RAction) values.get(EBuilderKey.ACTION);
  }

  @NotNull
  public BuildValues setAction(@Nullable RAction value) {
    values.put(EBuilderKey.ACTION, value);
    return this;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Map<Integer, Map<Long, Set<String>>> getActionEventQuotaMap() {
    return (Map<Integer, Map<Long, Set<String>>>) values.get(EBuilderKey.ACTION_EVENT_QUOTA_MAP);
  }

  @NotNull
  public BuildValues setActionEventQuotaMap(@Nullable Map<Integer, Map<Long, Set<String>>> value) {
    values.put(EBuilderKey.ACTION_EVENT_QUOTA_MAP, value);
    return this;
  }

  @Nullable
  public ReportSaleInfo getReportSaleInfo() {
    return (ReportSaleInfo) values.get(EBuilderKey.REPORT_SALE_INFO);
  }

  @NotNull
  public BuildValues setReportSaleInfo(@Nullable ReportSaleInfo value) {
    values.put(EBuilderKey.REPORT_SALE_INFO, value);
    return this;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public Set<Long> getAllowedSeatIdSet() {
    return (Set<Long>) values.get(EBuilderKey.ALLOWED_SEAT_ID_SET);
  }

  @NotNull
  public BuildValues setAllowedSeatIdSet(@Nullable Set<Long> value) {
    values.put(EBuilderKey.ALLOWED_SEAT_ID_SET, value);
    return this;
  }

  @Nullable
  public QuotaDataObj getQuotaData() {
    return (QuotaDataObj) values.get(EBuilderKey.QUOTA_DATA);
  }

  @NotNull
  public BuildValues setQuotaData(@Nullable QuotaDataObj value) {
    values.put(EBuilderKey.QUOTA_DATA, value);
    return this;
  }

  @Nullable
  public RCashierWorkShift getCashierWorkShift() {
    return (RCashierWorkShift) values.get(EBuilderKey.CASHIER_WORK_SHIFT);
  }

  @NotNull
  public BuildValues setCashierWorkShift(@Nullable RCashierWorkShift value) {
    values.put(EBuilderKey.CASHIER_WORK_SHIFT, value);
    return this;
  }

  enum EBuilderKey {
    FORM,
    SIGN,
    FILTER,
    CHARGE,
    DISCOUNT,
    ORDER_LIST,
    ACTION_EVENT_SEAT_MAP,
    TICKET_LIST,
    ACTION,
    ACTION_EVENT_QUOTA_MAP,
    REPORT_SALE_INFO,
    ALLOWED_SEAT_ID_SET,
    QUOTA_DATA,
    CASHIER_WORK_SHIFT
  }
}
