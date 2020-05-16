package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.*;
import report.utils.*;
import report.utils.SortedMap;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 24.11.2017
 */
public final class Form19 extends AFormFilter<List<Form19.ActionEventInfo>> {
  public Form19(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_19, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    getHeader()
        .add(EHeader.NAME, getForm().getName())
        .add(EHeader.PERIOD, getHeader().remove(EHeader.PERIOD))
        .add(EHeader.CITY, getFilter().getCity().getName())
        .add(EHeader.VENUE, getFilter().getVenue().getName())
        .add(EHeader.ACTION, getFilter().getAction().getName());
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
        .createCell("Скидка", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Итого сумма", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('B', 7);//Итого
    FormulaSubtotals formulaSubtotalsActionEvents = new FormulaSubtotals('B', 7);//Итого по всем ценам

    formulaSubtotalsActionEvents.setRowStart(sheet.getRowCurrentIndex() + 1);
    for (ActionEventInfo actionEventInfo : actionEventInfoList) {
      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 7);
      WrapRow row = sheet.createRow().createCell("Представление: " + actionEventInfo.name + '-' + actionEventInfo.showTime, EStyle.BOLD_242_CENTER);
      for (int i = 0; i < 7; i++) row.createCell(EStyle.EMPTY);

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
            .createCell(priceInfo.sumTicketDiscount, EStyle.NORMAL_242_MONEY)
            .createCell(priceInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
      }
      formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      row = sheet.createRow().createCell("Итого", EStyle.BOLD_216);
      while (formulaSubtotalsActionEvent.hasNext()) {
        row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216);
        row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY);
        //Делаю проверку на последний элемент, т.к. кол-во формул НЕчетное
        if (formulaSubtotalsActionEvent.isLastBeforeNext()) row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY);
      }

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsActionEvents.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow().createCell("Итого по всем ценам", EStyle.BOLD_191);
    while (formulaSubtotalsActionEvents.hasNext()) {
      row.createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191);
      row.createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY);
      //Делаю проверку на последний элемент, т.к. кол-во формул НЕчетное
      if (formulaSubtotalsActionEvents.isLastBeforeNext()) row.createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.BOLD_191_MONEY);
    }

    sheet.setColumnWidth(0, 5200);
    sheet.setColumnWidth(1, 2600);
    sheet.setColumnWidth(2, 3100);
    sheet.setColumnWidth(3, 2600);
    sheet.setColumnWidth(4, 3100);
    sheet.setColumnWidth(5, 2600);
    sheet.setColumnWidth(6, 3100);
  }

  @NotNull
  public Form19 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Period period;
    if (getFilter().isFullReport()) period = Period.create(orderList);
    else period = getFilter().getPeriod();
    if (period == null) throw ValidationException.absent("Период");
    getHeader().add(EHeader.PERIOD, "С " + period.getFromFormat() + " по " + period.getToFormat());

    SortedMap<ActionEventObj, Map<BigDecimal, List<TicketObj>>> actionEventMap = new SortedMap<>(Comparators.ACTION_EVENT_BY_SHOW_TIME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        Map<BigDecimal, List<TicketObj>> priceMap = actionEventMap.get(ticket.getActionEvent());
        if (priceMap == null) actionEventMap.put(ticket.getActionEvent(), priceMap = new TreeMap<>());
        BigDecimal price = getPrice(ticket);
        List<TicketObj> ticketList = priceMap.get(price);
        if (ticketList == null) priceMap.put(price, ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<ActionEventInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, Map<BigDecimal, List<TicketObj>>> actionEvent : actionEventMap.entrySorted()) {
      List<PriceInfo> priceInfoList = new ArrayList<>();
      for (Map.Entry<BigDecimal, List<TicketObj>> price : actionEvent.getValue().entrySet()) {
        int countTicketSold = 0;
        int countTicketRefund = 0;
        BigDecimal sumTicketSold = BigDecimal.ZERO;
        BigDecimal sumTicketRefund = BigDecimal.ZERO;
        BigDecimal sumTicketDiscount = BigDecimal.ZERO;

        for (TicketObj ticket : price.getValue()) {
          countTicketSold++;
          sumTicketSold = sumTicketSold.add(price.getKey());
          if (isDiscount()) sumTicketDiscount = sumTicketDiscount.add(ticket.getDiscount());

          if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
            countTicketRefund++;
            sumTicketRefund = sumTicketRefund.add(price.getKey());
            if (isDiscount()) sumTicketDiscount = sumTicketDiscount.subtract(ticket.getDiscount());
          }
        }

        priceInfoList.add(new PriceInfo(price.getKey(), countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund, sumTicketDiscount));
      }
      result.add(new ActionEventInfo(actionEvent.getKey().getActionName(), actionEvent.getKey().getShowTime(), priceInfoList));
    }
    setData(result);

    return this;
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
    private final BigDecimal sumTicketDiscount;

    private PriceInfo(@NotNull BigDecimal price, int countTicketSold, int countTicketRefund, @NotNull BigDecimal sumTicketSold,
                     @NotNull BigDecimal sumTicketRefund, @NotNull BigDecimal sumTicketDiscount) {
      this.price = price;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketSold = sumTicketSold;
      this.sumTicketRefund = sumTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
      this.sumTicketDiscount = sumTicketDiscount;
    }
  }
}
