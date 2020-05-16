package report.forms;

import java.math.*;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.Comparators;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 06.11.2018
 */
public final class Form13 extends AFormFilter<List<Form13.ActionInfo>> {
  @NotNull
  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

  public Form13(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_13, null, sign, filter, false, false);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionInfo> actionInfoList) {
    if (actionInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(60f)
        .createCell("Номинальная\nстоимость\nбилета", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Количество\nпроданных\nбилетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма\nпроданных\nбилетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Количество\nвозвращенных\nбилетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма\nвозвращенных\nбилетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого\nбилетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого\nсумма", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("% вознаграждения", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма\nвознаграждения", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAction = new FormulaSubtotals('B', 'C', 'D', 'E', 'F', 'G', 'H', 'J');//Итого
    FormulaSubtotals formulaSubtotalsActions = new FormulaSubtotals('B', 'C', 'D', 'E', 'F', 'G', 'H', 'J');//Итого по всем представлениям

    formulaSubtotalsActions.setRowStart(sheet.getRowCurrentIndex() + 1);
    for (ActionInfo actionInfo : actionInfoList) {
      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 9);
      WrapRow row = sheet.createRow().createCell("Представление: " + actionInfo.name, EStyle.BOLD_242_CENTER);
      for (int i = 0; i < 9; i++) row.createCell(EStyle.EMPTY);

      formulaSubtotalsAction.reset();
      formulaSubtotalsAction.setRowStart(sheet.getRowCurrentIndex());
      for (PriceInfo priceInfo : actionInfo.priceInfoList) {
        for (PercentChargeInfo percentChargeInfo : priceInfo.percentChargeInfoList) {
          sheet.createRow()
              .createCell(priceInfo.price, EStyle.BOLD_242_LEFT_MONEY)
              .createCell(percentChargeInfo.countTicketSold, EStyle.NORMAL_242)
              .createCell(percentChargeInfo.sumTicketSold, EStyle.NORMAL_242_MONEY)
              .createCell(percentChargeInfo.countTicketRefund, EStyle.NORMAL_242)
              .createCell(percentChargeInfo.sumTicketRefund, EStyle.NORMAL_242_MONEY)
              .createCell(percentChargeInfo.countTicketTotal, EStyle.NORMAL_242)
              .createCell(percentChargeInfo.sumTicketDiscount, EStyle.NORMAL_242_MONEY)
              .createCell(percentChargeInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY)
              .createCell(percentChargeInfo.percentCharge, EStyle.NORMAL_242_MONEY)
              .createCell(percentChargeInfo.sumTicketCharge, EStyle.NORMAL_242_MONEY);
        }
      }
      formulaSubtotalsAction.setRowEnd(sheet.getRowCurrentIndex() - 1);

      sheet.createRow()
          .createCell("Итого", EStyle.BOLD_216)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216_MONEY)
          .createCell(EStyle.BOLD_216)
          .createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_216_MONEY);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsActions.setRowEnd(sheet.getRowCurrentIndex() - 2);

    sheet.createRow()
        .setHeightInPoints(30f)
        .createCell("Итого по всем\nпредставлениям", EStyle.BOLD_191_LEFT_CENTER_WRAP)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191_MONEY)
        .createCell(EStyle.BOLD_191)
        .createCell(formulaSubtotalsActions.nextFormula(), EStyle.BOLD_191_MONEY);

    sheet.setColumnWidth(0, 4200);
    sheet.setColumnWidth(1, 3000);
    sheet.setColumnWidth(2, 3000);
    sheet.setColumnWidth(3, 3000);
    sheet.setColumnWidth(4, 3000);
    sheet.setColumnWidth(5, 2500);
    sheet.setColumnWidth(6, 2500);
    sheet.setColumnWidth(7, 2500);
    sheet.setColumnWidth(8, 2500);
    sheet.setColumnWidth(9, 2500);
  }

  @NotNull
  public Form13 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Map<ActionEventObj, Map<BigDecimal, Map<BigDecimal, List<TicketObj>>>> actionMap = new TreeMap<>(Comparators.ACTION_EVENT_BY_NAME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        Map<BigDecimal, Map<BigDecimal, List<TicketObj>>> priceMap = actionMap.get(ticket.getActionEvent());
        if (priceMap == null) actionMap.put(ticket.getActionEvent(), priceMap = new TreeMap<>());
        Map<BigDecimal, List<TicketObj>> percentChargeMap = priceMap.get(ticket.getPrice());
        if (percentChargeMap == null) priceMap.put(ticket.getPrice(), percentChargeMap = new TreeMap<>());
        BigDecimal percentCharge = getPercentCharge(ticket);
        List<TicketObj> ticketList = percentChargeMap.get(percentCharge);
        if (ticketList == null) percentChargeMap.put(percentCharge, ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<ActionInfo> result = new ArrayList<>(actionMap.size());
    for (Map.Entry<ActionEventObj, Map<BigDecimal, Map<BigDecimal, List<TicketObj>>>> action : actionMap.entrySet()) {
      List<PriceInfo> priceInfoList = new ArrayList<>(action.getValue().size());
      for (Map.Entry<BigDecimal, Map<BigDecimal, List<TicketObj>>> price : action.getValue().entrySet()) {
        List<PercentChargeInfo> percentChargeInfoList = new ArrayList<>(price.getValue().size());
        for (Map.Entry<BigDecimal, List<TicketObj>> percentCharge : price.getValue().entrySet()) {
          int countTicketSold = 0;
          BigDecimal sumTicketSold = BigDecimal.ZERO;
          int countTicketRefund = 0;
          BigDecimal sumTicketRefund = BigDecimal.ZERO;
          BigDecimal sumTicketDiscount = BigDecimal.ZERO;
          BigDecimal sumTicketCharge = BigDecimal.ZERO;
          for (TicketObj ticket : percentCharge.getValue()) {
            countTicketSold++;
            sumTicketSold = sumTicketSold.add(ticket.getPrice());
            sumTicketDiscount = sumTicketDiscount.add(ticket.getDiscount());
            sumTicketCharge = sumTicketCharge.add(ticket.getCharge());
            if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(ticket.getPrice());
              sumTicketDiscount = sumTicketDiscount.subtract(ticket.getDiscount());
              sumTicketCharge = sumTicketCharge.subtract(ticket.getCharge());
            }
          }
          percentChargeInfoList.add(new PercentChargeInfo(percentCharge.getKey(), countTicketSold, sumTicketSold, countTicketRefund, sumTicketRefund, sumTicketDiscount, sumTicketCharge));
        }
        priceInfoList.add(new PriceInfo(price.getKey(), percentChargeInfoList));
      }
      result.add(new ActionInfo(action.getKey().getActionName(), priceInfoList));
    }
    setData(result);

    return this;
  }

  @NotNull
  private static BigDecimal getPercentCharge(@NotNull TicketObj ticket) {
    BigDecimal value = ticket.getPrice().subtract(ticket.getDiscount());
    if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
    return ticket.getCharge().multiply(HUNDRED).divide(value, 2, RoundingMode.HALF_UP);
  }

  protected static final class ActionInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<PriceInfo> priceInfoList;

    private ActionInfo(@NotNull String name, @NotNull List<PriceInfo> priceInfoList) {
      this.name = name;
      this.priceInfoList = priceInfoList;
    }
  }

  private static final class PriceInfo {
    @NotNull
    private final BigDecimal price;
    @NotNull
    private final List<PercentChargeInfo> percentChargeInfoList;

    private PriceInfo(@NotNull BigDecimal price, @NotNull List<PercentChargeInfo> percentChargeInfoList) {
      this.price = price;
      this.percentChargeInfoList = percentChargeInfoList;
    }
  }

  private static final class PercentChargeInfo {
    @NotNull
    private final BigDecimal percentCharge;
    private final int countTicketSold;
    @NotNull
    private final BigDecimal sumTicketSold;
    private final int countTicketRefund;
    @NotNull
    private final BigDecimal sumTicketRefund;
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketDiscount;
    @NotNull
    private final BigDecimal sumTicketTotal;
    @NotNull
    private final BigDecimal sumTicketCharge;

    private PercentChargeInfo(@NotNull BigDecimal percentCharge, int countTicketSold, @NotNull BigDecimal sumTicketSold, int countTicketRefund,
                             @NotNull BigDecimal sumTicketRefund, @NotNull BigDecimal sumTicketDiscount, @NotNull BigDecimal sumTicketCharge) {
      this.percentCharge = percentCharge;
      this.countTicketSold = countTicketSold;
      this.sumTicketSold = sumTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.sumTicketRefund = sumTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketDiscount = sumTicketDiscount;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
      this.sumTicketCharge = sumTicketCharge;
    }
  }
}
