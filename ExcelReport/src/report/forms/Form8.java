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
import report.utils.Comparators;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 26.11.2017
 */
public final class Form8 extends AFormFilter<List<Form8.ActionInfo>> {
  public Form8(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_8, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionInfo> actionInfoList) {
    if (actionInfoList.isEmpty()) return;

    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('B', 14);//Итого
    FormulaSubtotals formulaSubtotalsAction = new FormulaSubtotals('B', 14);//Итого по представлению
    for (ActionInfo actionInfo : actionInfoList) {
      formulaSubtotalsAction.reset();
      formulaSubtotalsAction.setRowStart(sheet.getRowCurrentIndex() + 4);
      for (ActionEventInfo actionEventInfo : actionInfo.actionEventInfoList) {
        sheet.createRow()
            .createCell("Название мероприятия", EStyle.BOLD)
            .createCell(actionInfo.name, EStyle.BOLD);

        sheet.createRow()
            .createCell("Дата проведения", EStyle.BOLD)
            .createCell(actionEventInfo.showTime, EStyle.BOLD);

        sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex() + 1, 0, 0);
        for (int firstCol = 1, lastCol = 2; firstCol <= 13; firstCol += 2, lastCol += 2)
          sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), firstCol, lastCol);

        sheet.createRow()
            .createCell("Тариф", EStyle.NORMAL_CENTER_CENTER)
            .createCell("Получено (квота)", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Свободно", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Забронировано", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Продано билетов(номинал)", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Скидка", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Продано всего", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER)
            .createCell("Возвращено", EStyle.NORMAL_CENTER_CENTER)
            .createCell(EStyle.NORMAL_CENTER_CENTER);

        WrapRow row = sheet.createRow().createCell(EStyle.NORMAL_CENTER_CENTER);
        for (int i = 0; i < 7; i++) row.createCell("Билетов", EStyle.NORMAL_CENTER_CENTER).createCell("На сумму", EStyle.NORMAL_CENTER_CENTER);

        formulaSubtotalsActionEvent.reset();
        formulaSubtotalsActionEvent.setRowStart(sheet.getRowCurrentIndex());
        for (PriceInfo priceInfo : actionEventInfo.priceInfoList) {
          sheet.createRow()
              .createCell(priceInfo.price, EStyle.NORMAL_242)
              .createCell(priceInfo.countQuotaIn, EStyle.NORMAL_242)
              .createCell(priceInfo.sumQuotaIn, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countSeatAvailable, EStyle.NORMAL_242)
              .createCell(priceInfo.sumSeatAvailable, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countSeatReserved, EStyle.NORMAL_242)
              .createCell(priceInfo.sumSeatReserved, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countTicketSoldNominal, EStyle.NORMAL_242)
              .createCell(priceInfo.sumTicketSoldNominal, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countTicketDiscount, EStyle.NORMAL_242)
              .createCell(priceInfo.sumTicketDiscount, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countTicketSold, EStyle.NORMAL_242)
              .createCell(priceInfo.sumTicketSold, EStyle.NORMAL_242_COUNT)
              .createCell(priceInfo.countTicketRefund, EStyle.NORMAL_242)
              .createCell(priceInfo.sumTicketRefund, EStyle.NORMAL_242_COUNT);
        }
        formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

        row = sheet.createRow().createCell("Итого", EStyle.NORMAL_216);
        while (formulaSubtotalsActionEvent.hasNext()) {
          row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.NORMAL_216);
          //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
          row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.NORMAL_216_COUNT);
        }

        sheet.incRowCurrentIndex();
      }
      formulaSubtotalsAction.setRowEnd(sheet.getRowCurrentIndex() - 2);

      WrapRow row = sheet.createRow().createCell("Итого по представлению", EStyle.BOLD_191);
      while (formulaSubtotalsAction.hasNext()) {
        row.createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_191);
        //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
        row.createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_191_COUNT);
      }

      sheet.incRowCurrentIndex();
      sheet.incRowCurrentIndex();
    }

    sheet.setColumnWidth(0, 6000);
    for (int i1 = 1, i2 = 2; i2 <= 14; i1 += 2, i2 += 2) {
      sheet.setColumnWidth(i1, 2500);
      sheet.setColumnWidth(i2, 3500);
    }
  }

  @NotNull
  public Form8 generateData(@Nullable List<OrderObj> orderList, @Nullable Map<Long, List<EventSeatObj>> actionEventSeatMap) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");
    if (actionEventSeatMap == null) throw ValidationException.absent("Карта мест по сеансам");

    Map<String, Map<String, Map<BigDecimal, TempClass>>> actionMap = new TreeMap<>();
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        ActionEventObj actionEvent = ticket.getActionEvent();

        Map<String, Map<BigDecimal, TempClass>> actionEventMap = actionMap.get(actionEvent.getActionName());
        if (actionEventMap == null) actionMap.put(actionEvent.getActionName(), actionEventMap = new TreeMap<>(Comparators.STRING_BY_SHOW_TIME));
        Map<BigDecimal, TempClass> priceMap = actionEventMap.get(actionEvent.getShowTime());
        if (priceMap == null) {
          actionEventMap.put(actionEvent.getShowTime(), priceMap = new TreeMap<>());

          if (actionEventSeatMap.containsKey(actionEvent.getId())) {
            for (EventSeatObj eventSeat : actionEventSeatMap.get(actionEvent.getId())) {
              TempClass tempClass = priceMap.get(eventSeat.getPrice());
              if (tempClass == null) priceMap.put(eventSeat.getPrice(), tempClass = new TempClass());
              tempClass.eventSeatList.add(eventSeat);
            }
          }
        }
        TempClass tempClass = priceMap.get(ticket.getPrice());
        if (tempClass == null) priceMap.put(ticket.getPrice(), tempClass = new TempClass());
        tempClass.ticketList.add(ticket);
      }
    }

    List<ActionInfo> result = new ArrayList<>();
    for (Map.Entry<String, Map<String, Map<BigDecimal, TempClass>>> action : actionMap.entrySet()) {
      List<ActionEventInfo> actionEventInfoList = new ArrayList<>();
      for (Map.Entry<String, Map<BigDecimal, TempClass>> actionEvent : action.getValue().entrySet()) {
        List<PriceInfo> priceInfoList = new ArrayList<>();
        for (Map.Entry<BigDecimal, TempClass> price : actionEvent.getValue().entrySet()) {
          int countQuotaIn = 0;
          int countSeatAvailable = 0;
          int countSeatReserved = 0;
          int countTicketSoldNominal = 0;
          int countTicketSold = 0;
          int countTicketRefund = 0;
          int countTicketDiscount = 0;
          BigDecimal sumQuotaIn = BigDecimal.ZERO;
          BigDecimal sumSeatAvailable = BigDecimal.ZERO;
          BigDecimal sumSeatReserved = BigDecimal.ZERO;
          BigDecimal sumTicketSoldNominal = BigDecimal.ZERO;
          BigDecimal sumTicketSold = BigDecimal.ZERO;
          BigDecimal sumTicketRefund = BigDecimal.ZERO;
          BigDecimal sumTicketDiscount = BigDecimal.ZERO;

          for (EventSeatObj eventSeat : price.getValue().eventSeatList) {
            switch (eventSeat.getState()) {
              case AVAILABLE: {
                countSeatAvailable++;
                sumSeatAvailable = sumSeatAvailable.add(price.getKey());
                break;
              }
              case PRE_RESERVED:
              case RESERVED: {
                countSeatReserved++;
                sumSeatReserved = sumSeatReserved.add(price.getKey());
                break;
              }
            }
          }

          for (TicketObj ticket : price.getValue().ticketList) {
            countTicketSoldNominal++;
            countTicketSold++;
            sumTicketSoldNominal = sumTicketSoldNominal.add(price.getKey());
            sumTicketSold = sumTicketSold.add(getPrice(ticket));

            if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(getPrice(ticket));
            }

            if (isDiscount()) {
              if (ticket.getDiscount().compareTo(BigDecimal.ZERO) != 0) {
                countTicketDiscount++;
                sumTicketDiscount = sumTicketDiscount.add(ticket.getDiscount());
              }
            }
          }

          priceInfoList.add(new PriceInfo(price.getKey(), countQuotaIn, countSeatAvailable, countSeatReserved, countTicketSoldNominal, countTicketSold, countTicketRefund, countTicketDiscount, sumQuotaIn, sumSeatAvailable, sumSeatReserved, sumTicketSoldNominal, sumTicketSold, sumTicketRefund, sumTicketDiscount));
        }
        actionEventInfoList.add(new ActionEventInfo(actionEvent.getKey(), priceInfoList));
      }
      result.add(new ActionInfo(action.getKey(), actionEventInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class ActionInfo {
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
    private final String showTime;
    @NotNull
    private final List<PriceInfo> priceInfoList;

    private ActionEventInfo(@NotNull String showTime, @NotNull List<PriceInfo> priceInfoList) {
      this.showTime = showTime;
      this.priceInfoList = priceInfoList;
    }
  }

  private static final class PriceInfo {
    @NotNull
    private final BigDecimal price;
    private final int countQuotaIn;
    private final int countSeatAvailable;
    private final int countSeatReserved;
    private final int countTicketSoldNominal;
    private final int countTicketSold;
    private final int countTicketRefund;
    private final int countTicketDiscount;
    @NotNull
    private final BigDecimal sumQuotaIn;
    @NotNull
    private final BigDecimal sumSeatAvailable;
    @NotNull
    private final BigDecimal sumSeatReserved;
    @NotNull
    private final BigDecimal sumTicketSoldNominal;
    @NotNull
    private final BigDecimal sumTicketSold;
    @NotNull
    private final BigDecimal sumTicketRefund;
    @NotNull
    private final BigDecimal sumTicketDiscount;

    private PriceInfo(@NotNull BigDecimal price, int countQuotaIn, int countSeatAvailable, int countSeatReserved, int countTicketSoldNominal,
                      int countTicketSold, int countTicketRefund, int countTicketDiscount, @NotNull BigDecimal sumQuotaIn,
                      @NotNull BigDecimal sumSeatAvailable, @NotNull BigDecimal sumSeatReserved, @NotNull BigDecimal sumTicketSoldNominal,
                      @NotNull BigDecimal sumTicketSold, @NotNull BigDecimal sumTicketRefund, @NotNull BigDecimal sumTicketDiscount) {
      this.price = price;
      this.countQuotaIn = countQuotaIn;
      this.countSeatAvailable = countSeatAvailable;
      this.countSeatReserved = countSeatReserved;
      this.countTicketSoldNominal = countTicketSoldNominal;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketDiscount = countTicketDiscount;
      this.sumQuotaIn = sumQuotaIn;
      this.sumSeatAvailable = sumSeatAvailable;
      this.sumSeatReserved = sumSeatReserved;
      this.sumTicketSoldNominal = sumTicketSoldNominal;
      this.sumTicketSold = sumTicketSold;
      this.sumTicketRefund = sumTicketRefund;
      this.sumTicketDiscount = sumTicketDiscount;
    }
  }

  private static final class TempClass {
    @NotNull
    private final List<TicketObj> ticketList = new ArrayList<>();
    @NotNull
    private final List<EventSeatObj> eventSeatList = new ArrayList<>();

    private TempClass() {
    }
  }
}
