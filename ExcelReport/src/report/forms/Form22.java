package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.WrapSheet;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.Comparators;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 23.11.2017
 */
public final class Form22 extends AFormFilter<List<Form22.ActionInfo>> {
  public Form22(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_22, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionInfo> actionInfoList) {
    if (actionInfoList.isEmpty()) return;

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 1, 5);
    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 6, 10);
    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 11, 15);
    sheet.createRow()
        .createCell(EStyle.EMPTY)
        .createCell("Проданные билеты", EStyle.NORMAL_CENTER_CENTER)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell("Возвращенные билеты", EStyle.NORMAL_CENTER_CENTER)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell("Итого", EStyle.NORMAL_CENTER_CENTER)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY)
        .createCell(EStyle.EMPTY);

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Представление", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во\nБилетов,\nшт", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Номинал,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("С/Сбор,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во\nБилетов,\nшт", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Номинал,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("С/Сбор,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во\nБилетов,\nшт", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Номинал,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("С/Сбор,\nруб", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого,\nруб", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('B', 15);//Итого

    formulaSubtotalsAgent.setRowStart(sheet.getRowCurrentIndex());
    for (ActionInfo actionInfo : actionInfoList) {
      sheet.createRow()
          .createCell(actionInfo.name, EStyle.NORMAL_242)
          .createCell(actionInfo.countTicketSold, EStyle.NORMAL_242)
          .createCell(actionInfo.sumTicketSold, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketSoldDiscount, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketSoldCharge, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketSoldTotal, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.countTicketRefund, EStyle.NORMAL_242)
          .createCell(actionInfo.sumTicketRefund, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketRefundDiscount, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketRefundCharge, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketRefundTotal, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.countTicketTotal, EStyle.NORMAL_242)
          .createCell(actionInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketTotalDiscount, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketTotalCharge, EStyle.NORMAL_242_MONEY)
          .createCell(actionInfo.sumTicketTotalTotal, EStyle.NORMAL_242_MONEY);
    }
    formulaSubtotalsAgent.setRowEnd(sheet.getRowCurrentIndex() - 1);

    sheet.createRow()
        .createCell("Итого", EStyle.BOLD_191)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY);

    sheet.autoSizeColumn(0);
    sheet.setColumnPixel(1, 77);
    sheet.setColumnPixel(2, 77);
    sheet.setColumnPixel(3, 77);
    sheet.setColumnPixel(4, 77);
    sheet.setColumnPixel(5, 77);
    sheet.setColumnPixel(6, 77);
    sheet.setColumnPixel(7, 77);
    sheet.setColumnPixel(8, 77);
    sheet.setColumnPixel(9, 77);
    sheet.setColumnPixel(10, 77);
    sheet.setColumnPixel(11, 77);
    sheet.setColumnPixel(12, 77);
    sheet.setColumnPixel(13, 77);
    sheet.setColumnPixel(14, 77);
    sheet.setColumnPixel(15, 77);
  }

  @NotNull
  public Form22 generateData(@Nullable List<OrderObj> orderList, @Nullable List<TicketObj> ticketList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");
    if (ticketList == null) throw ValidationException.absent("Список билетов");

    Set<TicketObj> ticketSoldSet = new HashSet<>();
    for (OrderObj order : orderList) ticketSoldSet.addAll(order.getTicketList());
    Set<TicketObj> ticketRefundSet = new HashSet<>(ticketList);

    Map<ActionEventObj, Set<TicketObj>> actionMap = new TreeMap<>(Comparators.ACTION_EVENT_BY_NAME);
    fillAgentMap(actionMap, ticketSoldSet);
    fillAgentMap(actionMap, ticketRefundSet);

    List<ActionInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, Set<TicketObj>> action : actionMap.entrySet()) {
      int countTicketSold = 0;
      BigDecimal sumTicketSold = BigDecimal.ZERO;
      BigDecimal sumTicketSoldDiscount = BigDecimal.ZERO;
      BigDecimal sumTicketSoldCharge = BigDecimal.ZERO;

      int countTicketRefund = 0;
      BigDecimal sumTicketRefund = BigDecimal.ZERO;
      BigDecimal sumTicketRefundDiscount = BigDecimal.ZERO;
      BigDecimal sumTicketRefundCharge = BigDecimal.ZERO;

      for (TicketObj ticket : action.getValue()) {
        if (ticketSoldSet.contains(ticket)) {
          countTicketSold++;
          sumTicketSold = sumTicketSold.add(ticket.getPrice());
          if (isDiscount()) sumTicketSoldDiscount = sumTicketSoldDiscount.add(ticket.getDiscount());
          if (isCharge()) sumTicketSoldCharge = sumTicketSoldCharge.add(ticket.getCharge());
        }

        if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
          if (ticketRefundSet.contains(ticket)) {
            countTicketRefund++;
            sumTicketRefund = sumTicketRefund.add(ticket.getPrice());
            if (isDiscount()) sumTicketRefundDiscount = sumTicketRefundDiscount.add(ticket.getDiscount());
            if (isCharge()) sumTicketRefundCharge = sumTicketRefundCharge.add(ticket.getCharge());
          }
        }
      }

      result.add(new ActionInfo(action.getKey().getActionName(), countTicketSold, sumTicketSold, sumTicketSoldDiscount, sumTicketSoldCharge, countTicketRefund, sumTicketRefund, sumTicketRefundDiscount, sumTicketRefundCharge));
    }
    setData(result);

    return this;
  }

  private static void fillAgentMap(@NotNull Map<ActionEventObj, Set<TicketObj>> actionMap, @NotNull Set<TicketObj> ticketSet) {
    for (TicketObj ticket : ticketSet) {
      Set<TicketObj> tSet = actionMap.get(ticket.getActionEvent());
      if (tSet == null) actionMap.put(ticket.getActionEvent(), tSet = new HashSet<>());
      tSet.add(ticket);
    }
  }

  protected static final class ActionInfo {
    @NotNull
    private final String name;
    private final int countTicketSold;
    @NotNull
    private final BigDecimal sumTicketSold;
    @NotNull
    private final BigDecimal sumTicketSoldDiscount;
    @NotNull
    private final BigDecimal sumTicketSoldCharge;
    @NotNull
    private final BigDecimal sumTicketSoldTotal;
    private final int countTicketRefund;
    @NotNull
    private final BigDecimal sumTicketRefund;
    @NotNull
    private final BigDecimal sumTicketRefundDiscount;
    @NotNull
    private final BigDecimal sumTicketRefundCharge;
    @NotNull
    private final BigDecimal sumTicketRefundTotal;
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketTotal;
    @NotNull
    private final BigDecimal sumTicketTotalDiscount;
    @NotNull
    private final BigDecimal sumTicketTotalCharge;
    @NotNull
    private final BigDecimal sumTicketTotalTotal;

    private ActionInfo(@NotNull String name, int countTicketSold, @NotNull BigDecimal sumTicketSold, @NotNull BigDecimal sumTicketSoldDiscount,
                       @NotNull BigDecimal sumTicketSoldCharge, int countTicketRefund, @NotNull BigDecimal sumTicketRefund,
                       @NotNull BigDecimal sumTicketRefundDiscount, @NotNull BigDecimal sumTicketRefundCharge) {
      this.name = name;
      this.countTicketSold = countTicketSold;
      this.sumTicketSold = sumTicketSold;
      this.sumTicketSoldDiscount = sumTicketSoldDiscount;
      this.sumTicketSoldCharge = sumTicketSoldCharge;
      this.sumTicketSoldTotal = sumTicketSold.subtract(sumTicketSoldDiscount).add(sumTicketSoldCharge);
      this.countTicketRefund = countTicketRefund;
      this.sumTicketRefund = sumTicketRefund;
      this.sumTicketRefundDiscount = sumTicketRefundDiscount;
      this.sumTicketRefundCharge = sumTicketRefundCharge;
      this.sumTicketRefundTotal = sumTicketRefund.subtract(sumTicketRefundDiscount).add(sumTicketRefundCharge);
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
      this.sumTicketTotalDiscount = sumTicketSoldDiscount.subtract(sumTicketRefundDiscount);
      this.sumTicketTotalCharge = sumTicketSoldCharge.subtract(sumTicketRefundCharge);
      this.sumTicketTotalTotal = sumTicketSoldTotal.subtract(sumTicketRefundTotal);
    }
  }
}
