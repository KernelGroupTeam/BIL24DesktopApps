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
 * Created by Inventor on 25.11.2017
 */
public final class Form6 extends AFormFilter<List<Form6.VenueInfo>> {
  public Form6(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_6, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<VenueInfo> venueInfoList) {
    if (venueInfoList.isEmpty()) return;

    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('C', 5);//Итого
    FormulaSubtotals formulaSubtotalsAgents = new FormulaSubtotals('C', 5);//Итого по всем агентам
    FormulaSubtotals formulaSubtotalsVenue = new FormulaSubtotals('C', 5);//Общий итог
    for (VenueInfo venueInfo : venueInfoList) {
      formulaSubtotalsVenue.reset();
      formulaSubtotalsVenue.setRowStart(sheet.getRowCurrentIndex() + 4);
      for (ActionInfo actionInfo : venueInfo.actionInfoList) {
        for (ActionEventInfo actionEventInfo : actionInfo.actionEventInfoList) {
          sheet.createRow()
              .createCell("Название мероприятия", EStyle.BOLD)
              .createCell(actionInfo.name, EStyle.BOLD);

          sheet.createRow()
              .createCell("Дата проведения", EStyle.BOLD)
              .createCell(actionEventInfo.showTime, EStyle.BOLD);

          sheet.createRow()
              .createCell("Место проведения", EStyle.BOLD)
              .createCell(venueInfo.name, EStyle.BOLD);

          sheet.createRow()
              .setHeightInPoints(47.25f)
              .createCell("Агент", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("FID", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("Кол-во заказов", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("Кол-во проданных билетов", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("Количество возвращенных билетов", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("Кол-во билетов (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_TOP)
              .createCell("Сумма (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_TOP);

          formulaSubtotalsAgents.reset();
          formulaSubtotalsAgents.setRowStart(sheet.getRowCurrentIndex());
          for (AgentInfo agentInfo : actionEventInfo.agentInfoList) {
            sheet.setRowRememberIndex();

            formulaSubtotalsAgent.reset();
            formulaSubtotalsAgent.setRowStart(sheet.getRowCurrentIndex());
            for (FrontendInfo frontendInfo : agentInfo.frontendInfoList) {
              sheet.createRow()
                  .createCell(agentInfo.name, EStyle.BOLD_242_LEFT_TOP)
                  .createCell(frontendInfo.name, EStyle.NORMAL_242)
                  .createCell(frontendInfo.countOrder, EStyle.NORMAL_242)
                  .createCell(frontendInfo.countTicketSold, EStyle.NORMAL_242)
                  .createCell(frontendInfo.countTicketRefund, EStyle.NORMAL_242)
                  .createCell(frontendInfo.countTicketTotal, EStyle.NORMAL_242)
                  .createCell(frontendInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
            }
            formulaSubtotalsAgent.setRowEnd(sheet.getRowCurrentIndex() - 1);

            if (sheet.getRowRememberIndex() != sheet.getRowCurrentIndex() - 1) sheet.addMergedRegion(sheet.getRowRememberIndex(), sheet.getRowCurrentIndex() - 1, 0, 0);

            WrapRow row = sheet.createRow()
                .createCell("Итого", EStyle.BOLD_216)
                .createCell(EStyle.BOLD_216);
            while (formulaSubtotalsAgent.hasNext()) row.createCell(formulaSubtotalsAgent.nextFormula(), formulaSubtotalsAgent.isLastAfterNext() ? EStyle.BOLD_216_MONEY : EStyle.BOLD_216);

            sheet.incRowCurrentIndex();
          }
          formulaSubtotalsAgents.setRowEnd(sheet.getRowCurrentIndex() - 2);

          WrapRow row = sheet.createRow()
              .createCell("Итого по всем агентам", EStyle.BOLD_191)
              .createCell(EStyle.BOLD_191);
          while (formulaSubtotalsAgents.hasNext()) row.createCell(formulaSubtotalsAgents.nextFormula(), formulaSubtotalsAgents.isLastAfterNext() ? EStyle.BOLD_191_MONEY : EStyle.BOLD_191);

          sheet.incRowCurrentIndex();
        }
      }
      formulaSubtotalsVenue.setRowEnd(sheet.getRowCurrentIndex() - 2);

      WrapRow row = sheet.createRow()
          .createCell("Общий итог", EStyle.BOLD_BLUE)
          .createCell(EStyle.BOLD_BLUE);
      while (formulaSubtotalsVenue.hasNext()) {
        if (formulaSubtotalsVenue.isFirstBeforeNext()) {
          String formula = formulaSubtotalsVenue.nextFormula().getFormula();
          if (venueInfo.countOrderOffset != 0) formula += "-" + venueInfo.countOrderOffset;
          row.createCellFormula(formula, EStyle.BOLD_BLUE);
        } else row.createCell(formulaSubtotalsVenue.nextFormula(), formulaSubtotalsVenue.isLastAfterNext() ? EStyle.BOLD_BLUE_MONEY : EStyle.BOLD_BLUE);
      }

      sheet.incRowCurrentIndex();
      sheet.incRowCurrentIndex();
    }

    sheet.setColumnWidth(0, 7200);
    sheet.setColumnWidth(1, 4800);
    sheet.setColumnWidth(2, 2300);
    sheet.setColumnWidth(3, 2800);
    sheet.setColumnWidth(4, 4000);
    sheet.setColumnWidth(5, 4800);
    sheet.setColumnWidth(6, 5500);
  }

  @NotNull
  public Form6 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Map<String, Map<String, Map<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>>>> venueMap = new TreeMap<>();
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        Map<String, Map<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>>> actionMap = venueMap.get(ticket.getActionEvent().getVenueName());
        if (actionMap == null) venueMap.put(ticket.getActionEvent().getVenueName(), actionMap = new TreeMap<>());
        Map<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>> actionEventMap = actionMap.get(ticket.getActionEvent().getActionName());
        if (actionEventMap == null) actionMap.put(ticket.getActionEvent().getActionName(), actionEventMap = new TreeMap<>(Comparators.STRING_BY_SHOW_TIME));
        SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>> agentMap = actionEventMap.get(ticket.getActionEvent().getShowTime());
        if (agentMap == null) actionEventMap.put(ticket.getActionEvent().getShowTime(), agentMap = new SortedMap<>(Comparators.AGENT_BY_NAME));
        SortedMap<RFrontend, List<TicketObj>> frontendMap = agentMap.get(order.getAgent());
        if (frontendMap == null) agentMap.put(order.getAgent(), frontendMap = new SortedMap<>(Comparators.FRONTEND_BY_NAME));
        List<TicketObj> ticketList = frontendMap.get(order.getFrontend());
        if (ticketList == null) frontendMap.put(order.getFrontend(), ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<VenueInfo> result = new ArrayList<>();
    for (Map.Entry<String, Map<String, Map<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>>>> venue : venueMap.entrySet()) {
      Set<Long> countOrderAllSet = new HashSet<>();
      int countOrderAll = 0;

      List<ActionInfo> actionInfoList = new ArrayList<>();
      for (Map.Entry<String, Map<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>>> action : venue.getValue().entrySet()) {
        List<ActionEventInfo> actionEventInfoList = new ArrayList<>();
        for (Map.Entry<String, SortedMap<RAgent, SortedMap<RFrontend, List<TicketObj>>>> actionEvent : action.getValue().entrySet()) {
          List<AgentInfo> agentInfoList = new ArrayList<>();
          for (Map.Entry<RAgent, SortedMap<RFrontend, List<TicketObj>>> agent : actionEvent.getValue().entrySorted()) {
            List<FrontendInfo> frontendInfoList = new ArrayList<>();
            for (Map.Entry<RFrontend, List<TicketObj>> frontend : agent.getValue().entrySorted()) {
              Set<Long> orderIdSet = new HashSet<>();
              int countTicketSold = 0;
              int countTicketRefund = 0;
              BigDecimal sumTicketSold = BigDecimal.ZERO;
              BigDecimal sumTicketRefund = BigDecimal.ZERO;

              for (TicketObj ticket : frontend.getValue()) {
                orderIdSet.add(ticket.getOrderId());
                countTicketSold++;
                sumTicketSold = sumTicketSold.add(getPrice(ticket));

                if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
                  countTicketRefund++;
                  sumTicketRefund = sumTicketRefund.add(getPrice(ticket));
                }
              }

              countOrderAllSet.addAll(orderIdSet);
              countOrderAll += orderIdSet.size();

              frontendInfoList.add(new FrontendInfo(frontend.getKey().getName(), orderIdSet.size(), countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund));
            }
            agentInfoList.add(new AgentInfo(agent.getKey().getName(), frontendInfoList));
          }
          actionEventInfoList.add(new ActionEventInfo(actionEvent.getKey(), agentInfoList));
        }
        actionInfoList.add(new ActionInfo(action.getKey(), actionEventInfoList));
      }
      result.add(new VenueInfo(venue.getKey(), countOrderAll - countOrderAllSet.size(), actionInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class VenueInfo {
    @NotNull
    private final String name;
    private final int countOrderOffset;
    @NotNull
    private final List<ActionInfo> actionInfoList;

    private VenueInfo(@NotNull String name, int countOrderOffset, @NotNull List<ActionInfo> actionInfoList) {
      this.name = name;
      this.countOrderOffset = countOrderOffset;
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
    private final String showTime;
    @NotNull
    private final List<AgentInfo> agentInfoList;

    private ActionEventInfo(@NotNull String showTime, @NotNull List<AgentInfo> agentInfoList) {
      this.showTime = showTime;
      this.agentInfoList = agentInfoList;
    }
  }

  private static final class AgentInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<FrontendInfo> frontendInfoList;

    private AgentInfo(@NotNull String name, @NotNull List<FrontendInfo> frontendInfoList) {
      this.name = name;
      this.frontendInfoList = frontendInfoList;
    }
  }

  private static final class FrontendInfo {
    @NotNull
    private final String name;
    private final int countOrder;
    private final int countTicketSold;
    private final int countTicketRefund;
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketTotal;

    private FrontendInfo(@NotNull String name, int countOrder, int countTicketSold, int countTicketRefund,
                        @NotNull BigDecimal sumTicketSold, @NotNull BigDecimal sumTicketRefund) {
      this.name = name;
      this.countOrder = countOrder;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
    }
  }
}
