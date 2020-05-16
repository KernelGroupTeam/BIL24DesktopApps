package report.reporter.managers;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.*;
import report.forms.*;
import report.reporter.models.BuildValues;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 21.11.2017
 */
public final class BuildManager {
  @Nullable
  private static AForm<?> form;//Последний сформированный сохраненный отчет
  //-----------------------------------------------------------
  //Блок переменных нужных для генерации отчета
  //-----------------------------------------------------------
  @Nullable
  private static String sign;//Используется во всех отчетах
  //-----------------------------------------------------------
  //Блок переменных нужных для генерации отчета по фильтру
  //-----------------------------------------------------------
  @Nullable
  private static Boolean charge;//Используется в 1,2,3,4,6,7,8,10,12,17,18,19 отчетах
  @Nullable
  private static Boolean discount;//Используется в 1,2,3,4,6,7,8,10,12,17,18,19 отчетах
  @Nullable
  private static List<OrderObj> orderListFilter;//Используется в 1,2,3,4,5,6,7,8,10,11,13,16,17,18,19 отчетах
  @Nullable
  private static Map<Long, List<EventSeatObj>> actionEventSeatMap;//Используется только в 8 отчете
  @Nullable
  private static List<TicketObj> refundTicketListFilter;//Используется только в 17 отчете
  //-----------------------------------------------------------
  //Блок переменных нужных для генерации отчета по накладным
  //-----------------------------------------------------------
  @Nullable
  private static RAction action;//Используется в 9,14,15 отчетах
  @Nullable
  private static Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap;//Используется только в 9 отчете
  @Nullable
  private static ReportSaleInfo reportSaleInfo;//Используется только в 9 отчете
  @Nullable
  private static Set<Long> allowedSeatIdSet;//Используется в 14,15 отчетах
  @Nullable
  private static List<OrderObj> orderListQuota;//Используется в 14,15 отчетах
  //-----------------------------------------------------------
  //Блок переменных нужных для генерации накладных
  //-----------------------------------------------------------
  @Nullable
  private static QuotaDataObj quotaData;//Используется в только в накладных
  //-----------------------------------------------------------
  //Блок переменных нужных для генерации отчета по сменам
  //-----------------------------------------------------------
  @Nullable
  private static RCashierWorkShift cashierWorkShift;//Используется в 12 отчете
  @Nullable
  private static List<OrderObj> orderListCashierWorkShift;//Используется в 12 отчете
  //-----------------------------------------------------------

  private BuildManager() {
  }

  @Nullable
  public static AForm<?> getReport() {
    return form;
  }

  private static void setReport(@Nullable AForm<?> form) {
    BuildManager.form = form;
  }

  @Nullable
  public static String getSign() {
    return sign;
  }

  public static void setSign(@Nullable String sign) {
    BuildManager.sign = sign;
  }

  public static void setCharge(@Nullable Boolean charge) {
    BuildManager.charge = charge;
  }

  public static void setDiscount(@Nullable Boolean discount) {
    BuildManager.discount = discount;
  }

  @Nullable
  public static List<OrderObj> getOrderListFilter() {
    return orderListFilter;
  }

  public static void setOrderListFilter(@Nullable List<OrderObj> orderList) {
    BuildManager.orderListFilter = orderList;
  }

  public static void setActionEventSeatMap(@Nullable Map<Long, List<EventSeatObj>> actionEventSeatMap) {
    BuildManager.actionEventSeatMap = actionEventSeatMap;
  }

  public static void setRefundTicketListFilter(@Nullable List<TicketObj> refundTicketListFilter) {
    BuildManager.refundTicketListFilter = refundTicketListFilter;
  }

  public static void setAction(@Nullable RAction action) {
    BuildManager.action = action;
  }

  public static void setActionEventQuotaMap(@Nullable Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap) {
    BuildManager.actionEventQuotaMap = actionEventQuotaMap;
  }

  public static void setReportSaleInfo(@Nullable ReportSaleInfo reportSaleInfo) {
    BuildManager.reportSaleInfo = reportSaleInfo;
  }

  public static void setAllowedSeatIdSet(@Nullable Set<Long> allowedSeatIdSet) {
    BuildManager.allowedSeatIdSet = allowedSeatIdSet;
  }

  public static void setOrderListQuota(@Nullable List<OrderObj> orderList) {
    BuildManager.orderListQuota = orderList;
  }

  public static void setQuotaData(@Nullable QuotaDataObj quotaData) {
    BuildManager.quotaData = quotaData;
  }

  public static void setCashierWorkShift(@Nullable RCashierWorkShift cashierWorkShift) {
    BuildManager.cashierWorkShift = cashierWorkShift;
  }

  public static void setOrderListCashierWorkShift(@Nullable List<OrderObj> orderListCashierWorkShift) {
    BuildManager.orderListCashierWorkShift = orderListCashierWorkShift;
  }

  public static void build(@NotNull EForm form) throws IOException, ExcelReportException {
    BuildValues values = new BuildValues()
        .setForm(form)
        .setSign(sign)
        .setCharge(charge)
        .setDiscount(discount)
        .setActionEventSeatMap(actionEventSeatMap)
        .setTicketList(refundTicketListFilter)
        .setAction(action)
        .setActionEventQuotaMap(actionEventQuotaMap)
        .setReportSaleInfo(reportSaleInfo)
        .setAllowedSeatIdSet(allowedSeatIdSet)
        .setQuotaData(quotaData)
        .setCashierWorkShift(cashierWorkShift);
    switch (form) {
      case FORM_1:
      case FORM_2:
      case FORM_3:
      case FORM_4:
      case FORM_5:
      case FORM_6:
      case FORM_7:
      case FORM_8:
      case FORM_10:
      case FORM_11:
      case FORM_13:
      case FORM_16:
      case FORM_17:
      case FORM_18:
      case FORM_19:
      case FORM_20:
      case FORM_21:
      case FORM_22:
      case FORM_23:
        values.setFilter(FilterManager.getFilter()).setOrderList(orderListFilter);
        break;
      case FORM_12:
        values.setOrderList(orderListCashierWorkShift);
        break;
      case FORM_14:
      case FORM_15:
        values.setOrderList(orderListQuota);
        break;
    }
    build(values, EWriteType.FILE, true);
  }

  @NotNull
  public static AForm<?> build(@NotNull BuildValues values, @NotNull EWriteType write) throws IOException, ExcelReportException {
    return build(values, write, false);
  }

  @NotNull
  private static AForm<?> build(@NotNull BuildValues values, @NotNull EWriteType write, boolean save) throws IOException, ExcelReportException {
    EForm form = values.getForm();
    if (form == null) throw ValidationException.absent("Форма");
    AForm<?> report = null;
    switch (form) {
      case FORM_1:
        report = new Form1(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_2:
        report = new Form2(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_3:
        report = new Form3(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_4:
        report = new Form4(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_5:
        report = new Form5(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case FORM_6:
        report = new Form6(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_7:
        report = new Form7(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_8:
        report = new Form8(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList(), values.getActionEventSeatMap());
        break;
      case FORM_9:
        report = new Form9(values.getSign(), values.getAction(), values.getActionEventQuotaMap()).generateData(values.getReportSaleInfo());
        break;
      case FORM_10:
        report = new Form10(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_11:
        report = new Form11(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case FORM_12:
        report = new Form12(values.getSign(), values.getCashierWorkShift(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_13:
        report = new Form13(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case FORM_14:
        report = new Form14(values.getSign(), values.getAction(), values.getAllowedSeatIdSet()).generateData(values.getOrderList());
        break;
      case FORM_15:
        report = new Form15(values.getSign(), values.getAction(), values.getAllowedSeatIdSet()).generateData(values.getOrderList());
        break;
      case FORM_16:
        report = new Form16(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case FORM_17:
        report = new Form17(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList(), values.getTicketList());
        break;
      case FORM_18:
        report = new Form18(null, values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_19:
        report = new Form19(null, values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_20:
        report = new Form20(null, values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList());
        break;
      case FORM_21:
        report = new Form21(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case FORM_22:
        report = new Form22(values.getSign(), values.getFilter(), values.getCharge(), values.getDiscount()).generateData(values.getOrderList(), values.getTicketList());
        break;
      case FORM_23:
        report = new Form23(values.getSign(), values.getFilter()).generateData(values.getOrderList());
        break;
      case INVOICE_IN:
        report = new FormInvoiceIn(values.getSign(), values.getQuotaData()).generateData();
        break;
      case INVOICE_OUT:
        report = new FormInvoiceOut(values.getSign(), values.getQuotaData()).generateData();
        break;
    }
    report.build();
    if (write == EWriteType.FILE) {
      report.writeToFile();
      report.open();
    } else if (write == EWriteType.BYTES) {
      report.writeToBytes();
    }
    if (save) setReport(report);
    return report;
  }
}
