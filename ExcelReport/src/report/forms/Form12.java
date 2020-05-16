package report.forms;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.interfaces.IFormula;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.*;
import report.utils.DateFormats;
import server.protocol2.common.PaymentMethodObj;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 21.09.2018
 */
public final class Form12 extends AFormCashierWorkShift<Form12.CashierWorkShifhtInfo> {
  public Form12(@Nullable String sign, @Nullable RCashierWorkShift cashierWorkShift, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_12, null, sign, cashierWorkShift, charge, discount);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull CashierWorkShifhtInfo cashierWorkShifhtInfo) {
    if (cashierWorkShifhtInfo.receiptInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Время", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Представление", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сеанс", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Цена билета", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во проданных билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого по безналу", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого наличными", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого сумма", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsReceipt = new FormulaSubtotals('E', 4);//Итого по чеку
    FormulaSubtotals formulaSubtotalsCashierWorkShift = new FormulaSubtotals('E', 4);//Итого по смене

    formulaSubtotalsCashierWorkShift.setRowStart(sheet.getRowCurrentIndex());
    for (ReceiptInfo receiptInfo : cashierWorkShifhtInfo.receiptInfoList) {
      formulaSubtotalsReceipt.reset();
      formulaSubtotalsReceipt.setRowStart(sheet.getRowCurrentIndex());
      for (ActionInfo actionInfo : receiptInfo.actionInfoList) {
        for (ActionEventInfo actionEventInfo : actionInfo.actionEventInfoList) {
          for (PriceInfo priceInfo : actionEventInfo.priceInfoList) {
            for (PaymentInfo paymentInfo : priceInfo.paymentInfoList) {
              sheet.createRow()
                  .createCell(receiptInfo.date, EStyle.NORMAL_242)
                  .createCell(actionInfo.name, EStyle.NORMAL_242)
                  .createCell(actionEventInfo.date, EStyle.NORMAL_242)
                  .createCell(priceInfo.price, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.countTicketSold, EStyle.NORMAL_242)
                  .createCell(paymentInfo.isPOS ? paymentInfo.sumTicketTotal : BigDecimal.ZERO, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.isPOS ? BigDecimal.ZERO : paymentInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
            }
          }
        }
      }
      formulaSubtotalsReceipt.setRowEnd(sheet.getRowCurrentIndex() - 1);

      WrapRow row = sheet.createRow()
          .createCell("Итого по чеку", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      while (formulaSubtotalsReceipt.hasNext()) {
        row.createCell(formulaSubtotalsReceipt.nextFormula(), formulaSubtotalsReceipt.isFirstAfterNext() ? EStyle.BOLD_216 : EStyle.BOLD_216_MONEY);
      }

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsCashierWorkShift.setRowEnd(sheet.getRowCurrentIndex() - 2);

    if (!cashierWorkShifhtInfo.refundInfoList.isEmpty()) {
      FormulaSubtotals formulaSubtotalsRefund = new FormulaSubtotals('E', 4);//Итого по возвратам
      formulaSubtotalsRefund.setRowStart(sheet.getRowCurrentIndex());

      sheet.setRowRememberIndex();
      for (ActionInfo actionInfo : cashierWorkShifhtInfo.refundInfoList) {
        for (ActionEventInfo actionEventInfo : actionInfo.actionEventInfoList) {
          for (PriceInfo priceInfo : actionEventInfo.priceInfoList) {
            for (PaymentInfo paymentInfo : priceInfo.paymentInfoList) {
              sheet.createRow()
                  .createCell("Возвращенных билетов", EStyle.NORMAL_242_CENTER_CENTER)
                  .createCell(actionInfo.name, EStyle.NORMAL_242)
                  .createCell(actionEventInfo.date, EStyle.NORMAL_242)
                  .createCell(priceInfo.price, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.countTicketSold, EStyle.NORMAL_242)
                  .createCell(paymentInfo.isPOS ? paymentInfo.sumTicketTotal : BigDecimal.ZERO, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.isPOS ? BigDecimal.ZERO : paymentInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY)
                  .createCell(paymentInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
            }
          }
        }
      }
      formulaSubtotalsRefund.setRowEnd(sheet.getRowCurrentIndex() - 1);

      if (sheet.getRowRememberIndex() != sheet.getRowCurrentIndex() - 1) sheet.addMergedRegion(sheet.getRowRememberIndex(), sheet.getRowCurrentIndex() - 1, 0, 0);

      WrapRow row = sheet.createRow()
          .createCell("Итого по возвратам", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      while (formulaSubtotalsRefund.hasNext()) {
        row.createCell(formulaSubtotalsRefund.nextFormula(), formulaSubtotalsRefund.isFirstAfterNext() ? EStyle.BOLD_216 : EStyle.BOLD_216_MONEY);
      }

      sheet.incRowCurrentIndex();
    }

    WrapRow row = sheet.createRow()
        .createCell("Итого по смене", EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191);
    while (formulaSubtotalsCashierWorkShift.hasNext()) {
      IFormula formula = formulaSubtotalsCashierWorkShift.nextFormula();
      String formulaStr = formula.getFormula() + "-" + formula.getColumn() + (sheet.getRowCurrentIndex() - 2);
      row.createCellFormula(formulaStr, formulaSubtotalsCashierWorkShift.isFirstAfterNext() ? EStyle.BOLD_191 : EStyle.BOLD_191_MONEY);
    }

    sheet.autoSizeColumn(0);
    sheet.autoSizeColumn(1);
    sheet.autoSizeColumn(2);
    sheet.setColumnWidth(3, 2400);
    sheet.setColumnWidth(4, 3000);
    sheet.setColumnWidth(5, 3000);
    sheet.setColumnWidth(6, 3000);
    sheet.setColumnWidth(7, 3000);
  }

  @NotNull
  public Form12 generateData(@Nullable List<OrderObj> orderList) throws ExcelReportException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    //Формируем карту по чекам
    Map<Date, Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>>> receiptMap = new TreeMap<>();
    for (OrderObj order : orderList) {
      Date date;
      try {
        date = OrderObj.parseFormat(order.getDate());
      } catch (ParseException e) {
        throw new ExcelReportException("Неверный формат даты " + order.getDate() + " заказа orderId=" + order.getId());
      }
      if (order.getPaymentMethod().getId() == PaymentMethodObj.getUnknown().getId())
        throw new ExcelReportException("Неизвестный тип оплаты заказа orderId=" + order.getId());
      Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>> actionMap = new TreeMap<>();
      fillActionMap(actionMap, order, false);
      receiptMap.put(date, actionMap);
    }

    //Формируем карту по возвратам
    Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>> refundMap = new TreeMap<>();
    for (OrderObj order : orderList) {
      fillActionMap(refundMap, order, true);
    }

    List<ReceiptInfo> receiptInfoList = new ArrayList<>(receiptMap.size());
    for (Map.Entry<Date, Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>>> receipt : receiptMap.entrySet()) {
      List<ActionInfo> actionInfoList = createActionInfoList(receipt.getValue());
      receiptInfoList.add(new ReceiptInfo(DateFormats.format(receipt.getKey(), DateFormats.ETemplate.ddMMyyyyHHmm), actionInfoList));
    }

    List<ActionInfo> refundInfoList = createActionInfoList(refundMap);

    setData(new CashierWorkShifhtInfo(receiptInfoList, refundInfoList));

    return this;
  }

  private void fillActionMap(@NotNull Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>> actionMap, @NotNull OrderObj order, boolean onlyRefund) throws ExcelReportException {
    for (TicketObj ticket : order.getTicketList()) {
      if (ticket.getHolderStatus() != TicketObj.HolderStatus.REFUND && onlyRefund) continue;
      ActionEventObj actionEvent = ticket.getActionEvent();
      Date showTimeDate;
      try {
        showTimeDate = actionEvent.getShowTimeDate();
      } catch (ParseException e) {
        throw new ExcelReportException("Неверный формат даты " + actionEvent.getShowTime() + " сеанса в заказе orderId=" + order.getId() + " билета ticketId=" + ticket.getId());
      }
      Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>> actionEventMap = actionMap.get(actionEvent.getActionName());
      if (actionEventMap == null) actionMap.put(actionEvent.getActionName(), actionEventMap = new TreeMap<>());
      Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>> priceMap = actionEventMap.get(showTimeDate);
      if (priceMap == null) actionEventMap.put(showTimeDate, priceMap = new TreeMap<>());
      BigDecimal price = getPrice(ticket);
      Map<PaymentMethodObj, List<TicketObj>> paymentMap = priceMap.get(price);
      if (paymentMap == null) priceMap.put(price, paymentMap = new HashMap<>());
      List<TicketObj> ticketList = paymentMap.get(order.getPaymentMethod());
      if (ticketList == null) paymentMap.put(order.getPaymentMethod(), ticketList = new ArrayList<>());
      ticketList.add(ticket);
    }
  }

  @NotNull
  private static List<ActionInfo> createActionInfoList(@NotNull Map<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>> map) {
    List<ActionInfo> result = new ArrayList<>(map.size());
    for (Map.Entry<String, Map<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>>> action : map.entrySet()) {
      List<ActionEventInfo> actionEventInfoList = new ArrayList<>(action.getValue().size());
      for (Map.Entry<Date, Map<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>>> actionEvent : action.getValue().entrySet()) {
        List<PriceInfo> priceInfoList = new ArrayList<>(actionEvent.getValue().size());
        for (Map.Entry<BigDecimal, Map<PaymentMethodObj, List<TicketObj>>> price : actionEvent.getValue().entrySet()) {
          List<PaymentInfo> paymentInfoList = new ArrayList<>(price.getValue().size());
          for (Map.Entry<PaymentMethodObj, List<TicketObj>> payment : price.getValue().entrySet()) {
            boolean isPOS = payment.getKey().getId() == 2;
            int countTicketSold = payment.getValue().size();
            BigDecimal sumTicketTotal = price.getKey().multiply(BigDecimal.valueOf(countTicketSold));
            paymentInfoList.add(new PaymentInfo(isPOS, countTicketSold, sumTicketTotal));
          }
          priceInfoList.add(new PriceInfo(price.getKey(), paymentInfoList));
        }
        actionEventInfoList.add(new ActionEventInfo(DateFormats.format(actionEvent.getKey(), DateFormats.ETemplate.HHmm), priceInfoList));
      }
      result.add(new ActionInfo(action.getKey(), actionEventInfoList));
    }
    return result;
  }

  protected static final class CashierWorkShifhtInfo {
    @NotNull
    private final List<ReceiptInfo> receiptInfoList;//Информация по чекам
    @NotNull
    private final List<ActionInfo> refundInfoList;//Информация по возвратам

    private CashierWorkShifhtInfo(@NotNull List<ReceiptInfo> receiptInfoList, @NotNull List<ActionInfo> refundInfoList) {
      this.receiptInfoList = receiptInfoList;
      this.refundInfoList = refundInfoList;
    }
  }

  private static final class ReceiptInfo {
    @NotNull
    private final String date;
    @NotNull
    private final List<ActionInfo> actionInfoList;

    private ReceiptInfo(@NotNull String date, @NotNull List<ActionInfo> actionInfoList) {
      this.date = date;
      this.actionInfoList = actionInfoList;
    }
  }

  private static final class ActionInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<ActionEventInfo> actionEventInfoList;

    private ActionInfo(@NotNull String name, @NotNull List<ActionEventInfo> actionEventInfoList) {
      this.name = name;
      this.actionEventInfoList = actionEventInfoList;
    }
  }

  private static final class ActionEventInfo {
    @NotNull
    private final String date;
    @NotNull
    private final List<PriceInfo> priceInfoList;

    private ActionEventInfo(@NotNull String date, @NotNull List<PriceInfo> priceInfoList) {
      this.date = date;
      this.priceInfoList = priceInfoList;
    }
  }

  private static final class PriceInfo {
    @NotNull
    private final BigDecimal price;
    @NotNull
    private final List<PaymentInfo> paymentInfoList;

    private PriceInfo(@NotNull BigDecimal price, @NotNull List<PaymentInfo> paymentInfoList) {
      this.price = price;
      this.paymentInfoList = paymentInfoList;
    }
  }

  private static final class PaymentInfo {
    private final boolean isPOS;
    private final int countTicketSold;
    @NotNull
    private final BigDecimal sumTicketTotal;

    private PaymentInfo(boolean isPOS, int countTicketSold, @NotNull BigDecimal sumTicketTotal) {
      this.isPOS = isPOS;
      this.countTicketSold = countTicketSold;
      this.sumTicketTotal = sumTicketTotal;
    }
  }
}
