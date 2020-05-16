package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.*;
import report.utils.SortedMap;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 24.11.2017
 */
public final class Form17 extends AFormFilter<List<Form17.ActionEventInfo>> {
  public Form17(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_17, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionEventInfo> actionEventInfoList) {
    if (actionEventInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Цена", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во продан. билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма продан. билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во возвращ. билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма возвращ. билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка при продаже", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка для возврата", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого сумма", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('B', 8);//Итого
    FormulaSubtotals formulaSubtotalsActionEvents = new FormulaSubtotals('B', 8);//Итого по всем ценам

    formulaSubtotalsActionEvents.setRowStart(sheet.getRowCurrentIndex() + 1);
    for (ActionEventInfo actionEventInfo : actionEventInfoList) {
      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 8);
      WrapRow row = sheet.createRow().createCell("Представление: " + actionEventInfo.name + '-' + actionEventInfo.showTime, EStyle.BOLD_242_CENTER);
      for (int i = 0; i < 8; i++) row.createCell(EStyle.EMPTY);

      formulaSubtotalsActionEvent.reset();
      formulaSubtotalsActionEvent.setRowStart(sheet.getRowCurrentIndex());
      for (PriceInfo priceInfo : actionEventInfo.priceInfoList) {
        sheet.createRow()
            .createCell(priceInfo.price, EStyle.BOLD_242_LEFT_TOP)
            .createCell(priceInfo.countTicketSold, EStyle.NORMAL_242)
            .createCell(priceInfo.sumTicketSold, EStyle.NORMAL_242_MONEY)
            .createCell(priceInfo.countTicketRefund, EStyle.NORMAL_242)
            .createCell(priceInfo.sumTicketRefund, EStyle.NORMAL_242_MONEY)
            .createCell(priceInfo.countTicketTotal, EStyle.NORMAL_242)
            .createCell(priceInfo.sumTicketDiscountSold, EStyle.NORMAL_242_MONEY)
            .createCell(priceInfo.sumTicketDiscountRefund, EStyle.NORMAL_242_MONEY)
            .createCell(priceInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
      }
      formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      sheet.createRow().createCell("Итого", EStyle.BOLD_216)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsActionEvents.setRowEnd(sheet.getRowCurrentIndex() - 2);

    sheet.createRow().createCell("Итого по всем ценам", EStyle.BOLD_191)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY);

    sheet.setColumnWidth(0, 5200);
    sheet.setColumnWidth(1, 2600);
    sheet.setColumnWidth(2, 3100);
    sheet.setColumnWidth(3, 2600);
    sheet.setColumnWidth(4, 3100);
    sheet.setColumnWidth(5, 3100);
    sheet.setColumnWidth(6, 3100);
    sheet.setColumnWidth(7, 3100);
  }

  @NotNull
  public Form17 generateData(@Nullable List<OrderObj> orderList, @Nullable List<TicketObj> ticketList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");
    if (ticketList == null) throw ValidationException.absent("Список билетов");

    Set<TicketObj> ticketSoldSet = new HashSet<>();
    for (OrderObj order : orderList) ticketSoldSet.addAll(order.getTicketList());
    Set<TicketObj> ticketRefundSet = new HashSet<>(ticketList);

    SortedMap<ActionEventObj, Map<BigDecimal, Set<TicketObj>>> actionEventMap = new SortedMap<>(Comparators.ACTION_EVENT_BY_SHOW_TIME);
    fillActionEventMap(actionEventMap, ticketSoldSet);
    fillActionEventMap(actionEventMap, ticketRefundSet);

    List<ActionEventInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, Map<BigDecimal, Set<TicketObj>>> actionEvent : actionEventMap.entrySorted()) {
      List<PriceInfo> priceInfoList = new ArrayList<>();
      for (Map.Entry<BigDecimal, Set<TicketObj>> price : actionEvent.getValue().entrySet()) {
        int countTicketSold = 0;
        int countTicketRefund = 0;
        BigDecimal sumTicketSold = BigDecimal.ZERO;
        BigDecimal sumTicketRefund = BigDecimal.ZERO;
        BigDecimal sumTicketDiscountSold = BigDecimal.ZERO;
        BigDecimal sumTicketDiscountRefund = BigDecimal.ZERO;

        for (TicketObj ticket : price.getValue()) {
          if (ticketSoldSet.contains(ticket)) {
            countTicketSold++;
            sumTicketSold = sumTicketSold.add(price.getKey());
            if (isDiscount()) sumTicketDiscountSold = sumTicketDiscountSold.add(ticket.getDiscount());
          }

          if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
            if (ticketRefundSet.contains(ticket)) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(price.getKey());
              if (isDiscount()) sumTicketDiscountRefund = sumTicketDiscountRefund.add(ticket.getDiscount());
            }
          }
        }

        priceInfoList.add(new PriceInfo(price.getKey(), countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund, sumTicketDiscountSold, sumTicketDiscountRefund));
      }
      result.add(new ActionEventInfo(actionEvent.getKey().getActionName(), actionEvent.getKey().getShowTime(), priceInfoList));
    }
    setData(result);

    return this;
  }

  private void fillActionEventMap(@NotNull SortedMap<ActionEventObj, Map<BigDecimal, Set<TicketObj>>> actionEventMap, @NotNull Set<TicketObj> ticketSet) {
    for (TicketObj ticket : ticketSet) {
      Map<BigDecimal, Set<TicketObj>> priceMap = actionEventMap.get(ticket.getActionEvent());
      if (priceMap == null) actionEventMap.put(ticket.getActionEvent(), priceMap = new TreeMap<>());
      BigDecimal price = getPrice(ticket);
      Set<TicketObj> tSet = priceMap.get(price);
      if (tSet == null) priceMap.put(price, tSet = new HashSet<>());
      tSet.add(ticket);
    }
  }

  protected static final class ActionEventInfo {
    @NotNull
    private final String name;
    @NotNull
    private final String showTime;
    @NotNull
    private final List<PriceInfo> priceInfoList;

    private ActionEventInfo(@NotNull String name, @NotNull String showTime, @NotNull List<PriceInfo> priceInfoList) {
      this.name = name;
      this.showTime = showTime;
      this.priceInfoList = priceInfoList;
    }
  }

  private static final class PriceInfo {
    @NotNull
    private final BigDecimal price;
    private final int countTicketSold;
    private final int countTicketRefund;
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketSold;
    @NotNull
    private final BigDecimal sumTicketRefund;
    @NotNull
    private final BigDecimal sumTicketTotal;
    @NotNull
    private final BigDecimal sumTicketDiscountSold;
    @NotNull
    private final BigDecimal sumTicketDiscountRefund;

    private PriceInfo(@NotNull BigDecimal price, int countTicketSold, int countTicketRefund, @NotNull BigDecimal sumTicketSold,
                      @NotNull BigDecimal sumTicketRefund, @NotNull BigDecimal sumTicketDiscountSold, @NotNull BigDecimal sumTicketDiscountRefund) {
      this.price = price;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketSold = sumTicketSold;
      this.sumTicketRefund = sumTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
      this.sumTicketDiscountSold = sumTicketDiscountSold;
      this.sumTicketDiscountRefund = sumTicketDiscountRefund;
    }
  }
}
