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
 * Created by Inventor on 23.11.2017
 */
public final class Form3 extends AFormFilter<List<Form3.AgentInfo>> {
  public Form3(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_3, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<AgentInfo> agentInfoList) {
    if (agentInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Агент", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Представление", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во возвратов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("С/Сбор", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('C', 5);//Итого
    FormulaSubtotals formulaSubtotalsAgents = new FormulaSubtotals('C', 5);//Итого по всем агентам

    formulaSubtotalsAgents.setRowStart(sheet.getRowCurrentIndex());
    for (AgentInfo agentInfo : agentInfoList) {
      sheet.setRowRememberIndex();

      formulaSubtotalsAgent.reset();
      formulaSubtotalsAgent.setRowStart(sheet.getRowCurrentIndex());
      for (ActionInfo actionInfo : agentInfo.actionInfoList) {
        sheet.createRow()
            .createCell(agentInfo.name, EStyle.BOLD_242_LEFT_TOP)
            .createCell(actionInfo.name, EStyle.NORMAL_242)
            .createCell(actionInfo.countTicketSold, EStyle.NORMAL_242)
            .createCell(actionInfo.countTicketRefund, EStyle.NORMAL_242)
            .createCell(actionInfo.sumTicketDiscount, EStyle.NORMAL_242_MONEY)
            .createCell(actionInfo.sumTicketCharge, EStyle.NORMAL_242_MONEY)
            .createCell(actionInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
      }
      formulaSubtotalsAgent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      if (sheet.getRowRememberIndex() != sheet.getRowCurrentIndex() - 1) sheet.addMergedRegion(sheet.getRowRememberIndex(), sheet.getRowCurrentIndex() - 1, 0, 0);

      WrapRow row = sheet.createRow()
          .createCell("Итого", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      for (int i = 0; formulaSubtotalsAgent.hasNext(); i++) {
        if (i < 2) row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_216);
        else row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_216_MONEY);
      }

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsAgents.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow()
        .createCell("Итого по всем агентам", EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191);
    for (int i = 0; formulaSubtotalsAgents.hasNext(); i++) {
      if (i < 2) row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_191);
      else row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_191_MONEY);
    }

    sheet.autoSizeColumn(0);
    sheet.autoSizeColumn(1);
    sheet.setColumnWidth(2, 2300);
    sheet.setColumnWidth(3, 2800);
    sheet.setColumnWidth(4, 2800);
    sheet.setColumnWidth(5, 2800);
    sheet.setColumnWidth(6, 2800);
  }

  @NotNull
  public Form3 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    SortedMap<RAgent, Map<ActionEventObj, List<TicketObj>>> agentMap = new SortedMap<>(Comparators.AGENT_BY_NAME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        Map<ActionEventObj, List<TicketObj>> actionMap = agentMap.get(order.getAgent());
        if (actionMap == null) agentMap.put(order.getAgent(), actionMap = new TreeMap<>(Comparators.ACTION_EVENT_BY_NAME));
        List<TicketObj> ticketList = actionMap.get(ticket.getActionEvent());
        if (ticketList == null) actionMap.put(ticket.getActionEvent(), ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<AgentInfo> result = new ArrayList<>();
    for (Map.Entry<RAgent, Map<ActionEventObj, List<TicketObj>>> agent : agentMap.entrySorted()) {
      List<ActionInfo> actionInfoList = new ArrayList<>();
      for (Map.Entry<ActionEventObj, List<TicketObj>> action : agent.getValue().entrySet()) {
        int countTicketSold = 0;
        int countTicketRefund = 0;
        BigDecimal sumTicketDiscount = BigDecimal.ZERO;
        BigDecimal sumTicketCharge = BigDecimal.ZERO;
        BigDecimal sumTicketTotal = BigDecimal.ZERO;

        for (TicketObj ticket : action.getValue()) {
          countTicketSold++;
          if (isDiscount()) sumTicketDiscount = sumTicketDiscount.add(ticket.getDiscount());
          if (isCharge()) sumTicketCharge = sumTicketCharge.add(ticket.getCharge());
          sumTicketTotal = sumTicketTotal.add(getPrice(ticket));

          if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
            countTicketRefund++;
            if (isDiscount()) sumTicketDiscount = sumTicketDiscount.subtract(ticket.getDiscount());
            if (isCharge()) sumTicketCharge = sumTicketCharge.subtract(ticket.getCharge());
            sumTicketTotal = sumTicketTotal.subtract(getPrice(ticket));
          }
        }

        actionInfoList.add(new ActionInfo(action.getKey().getActionName(), countTicketSold, countTicketRefund, sumTicketDiscount, sumTicketCharge, sumTicketTotal));
      }
      result.add(new AgentInfo(agent.getKey().getName(), actionInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class AgentInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<ActionInfo> actionInfoList;

    private AgentInfo(@NotNull String name, @NotNull List<ActionInfo> actionInfoList) {
      this.name = name;
      this.actionInfoList = actionInfoList;
    }
  }

  private static final class ActionInfo {
    @NotNull
    private final String name;
    private final int countTicketSold;
    private final int countTicketRefund;
    @NotNull
    private final BigDecimal sumTicketDiscount;
    @NotNull
    private final BigDecimal sumTicketCharge;
    @NotNull
    private final BigDecimal sumTicketTotal;

    private ActionInfo(@NotNull String name, int countTicketSold, int countTicketRefund, @NotNull BigDecimal sumTicketDiscount,
                       @NotNull BigDecimal sumTicketCharge, @NotNull BigDecimal sumTicketTotal) {
      this.name = name;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.sumTicketDiscount = sumTicketDiscount;
      this.sumTicketCharge = sumTicketCharge;
      this.sumTicketTotal = sumTicketTotal;
    }
  }
}
