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
 * Created by Inventor on 26.11.2017
 */
public final class Form10 extends AFormFilter<List<Form10.AgentInfo>> {
  public Form10(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_10, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<AgentInfo> agentInfoList) {
    if (agentInfoList.isEmpty()) return;

    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('B', 6);//Итого
    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('B', 6);//Итого по всем ценам
    FormulaSubtotals formulaSubtotalsAgents = new FormulaSubtotals('B', 6);//Итого по всем агентам

    formulaSubtotalsAgents.setRowStart(sheet.getRowCurrentIndex() + 3);
    for (AgentInfo agentInfo : agentInfoList) {
      sheet.createRow()
          .createCell("Агент", EStyle.BOLD)
          .createCell(agentInfo.name, EStyle.BOLD);

      sheet.createRow()
          .setHeightInPoints(47.25f)
          .createCell("Цена", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Кол-во продан. билетов", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Сумма продан. билетов", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Кол-во возвращ. билетов", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Сумма возвращ. билетов", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Итого билетов", EStyle.NORMAL_JUSTIFY_TOP)
          .createCell("Итого сумма", EStyle.NORMAL_JUSTIFY_TOP);

      formulaSubtotalsAgent.reset();
      formulaSubtotalsAgent.setRowStart(sheet.getRowCurrentIndex() + 1);
      for (ActionEventInfo actionEventInfo : agentInfo.actionEventInfoList) {
        sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 6);
        WrapRow row = sheet.createRow().createCell("Представление: " + actionEventInfo.name + '-' + actionEventInfo.showTime, EStyle.BOLD_242_CENTER);
        for (int i = 0; i < 6; i++) row.createCell(EStyle.EMPTY);

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
              .createCell(priceInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
        }
        formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

        row = sheet.createRow().createCell("Итого", EStyle.BOLD_216);
        while (formulaSubtotalsActionEvent.hasNext()) {
          row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216);
          //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
          row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.BOLD_216_MONEY);
        }

        sheet.incRowCurrentIndex();
      }
      formulaSubtotalsAgent.setRowEnd(sheet.getRowCurrentIndex() - 2);

      WrapRow row = sheet.createRow().createCell("Итого по всем ценам", EStyle.BOLD_191);
      while (formulaSubtotalsAgent.hasNext()) {
        row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191);
        //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
        row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_191_MONEY);
      }

      sheet.incRowCurrentIndex();
      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsAgents.setRowEnd(sheet.getRowCurrentIndex() - 3);

    WrapRow row = sheet.createRow().createCell("Итого по всем агентам", EStyle.BOLD_BLUE);
    while (formulaSubtotalsAgents.hasNext()) {
      row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_BLUE);
      //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
      row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_BLUE_MONEY);
    }

    sheet.setColumnWidth(0, 5500);
    sheet.setColumnWidth(1, 2600);
    sheet.setColumnWidth(2, 3100);
    sheet.setColumnWidth(3, 2600);
    sheet.setColumnWidth(4, 3100);
    sheet.setColumnWidth(5, 2600);
    sheet.setColumnWidth(6, 3100);
  }

  @NotNull
  public Form10 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    SortedMap<RAgent, SortedMap<ActionEventObj, Map<BigDecimal, List<TicketObj>>>> agentMap = new SortedMap<>(Comparators.AGENT_BY_NAME);
    for (OrderObj order : orderList) {
      SortedMap<ActionEventObj, Map<BigDecimal, List<TicketObj>>> actionEventMap = agentMap.get(order.getAgent());
      if (actionEventMap == null) agentMap.put(order.getAgent(), actionEventMap = new SortedMap<>(Comparators.ACTION_EVENT_BY_SHOW_TIME));
      for (TicketObj ticket : order.getTicketList()) {
        Map<BigDecimal, List<TicketObj>> priceMap = actionEventMap.get(ticket.getActionEvent());
        if (priceMap == null) actionEventMap.put(ticket.getActionEvent(), priceMap = new TreeMap<>());
        BigDecimal price = getPrice(ticket);
        List<TicketObj> ticketList = priceMap.get(price);
        if (ticketList == null) priceMap.put(price, ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<AgentInfo> result = new ArrayList<>();
    for (Map.Entry<RAgent, SortedMap<ActionEventObj, Map<BigDecimal, List<TicketObj>>>> agent : agentMap.entrySorted()) {
      List<ActionEventInfo> actionEventInfoList = new ArrayList<>();
      for (Map.Entry<ActionEventObj, Map<BigDecimal, List<TicketObj>>> actionEvent : agent.getValue().entrySorted()) {
        List<PriceInfo> priceInfoList = new ArrayList<>();
        for (Map.Entry<BigDecimal, List<TicketObj>> price : actionEvent.getValue().entrySet()) {
          int countTicketSold = 0;
          int countTicketRefund = 0;
          BigDecimal sumTicketSold = BigDecimal.ZERO;
          BigDecimal sumTicketRefund = BigDecimal.ZERO;

          for (TicketObj ticket : price.getValue()) {
            countTicketSold++;
            sumTicketSold = sumTicketSold.add(price.getKey());

            if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(price.getKey());
            }
          }

          priceInfoList.add(new PriceInfo(price.getKey(), countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund));
        }
        actionEventInfoList.add(new ActionEventInfo(actionEvent.getKey().getActionName(), actionEvent.getKey().getShowTime(), priceInfoList));
      }
      result.add(new AgentInfo(agent.getKey().getName(), actionEventInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class AgentInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<ActionEventInfo> actionEventInfoList;

    private AgentInfo(@NotNull String name, @NotNull List<ActionEventInfo> actionEventInfoList) {
      this.name = name;
      this.actionEventInfoList = actionEventInfoList;
    }
  }

  private static final class ActionEventInfo {
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

    private PriceInfo(@NotNull BigDecimal price, int countTicketSold, int countTicketRefund, @NotNull BigDecimal sumTicketSold, @NotNull BigDecimal sumTicketRefund) {
      this.price = price;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketSold = sumTicketSold;
      this.sumTicketRefund = sumTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
    }
  }
}
