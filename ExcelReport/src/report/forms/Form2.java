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
public final class Form2 extends AFormFilter<List<Form2.ActionInfo>> {
  public Form2(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_2, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionInfo> actionInfoList) {
    if (actionInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Представление", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Агент", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во возвр. билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Стоимость возвр. Билетов", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAction = new FormulaSubtotals('C', 2);//Итого
    FormulaSubtotals formulaSubtotalsActions = new FormulaSubtotals('C', 2);//Итого по всем представлениям

    formulaSubtotalsActions.setRowStart(sheet.getRowCurrentIndex());
    for (ActionInfo actionInfo : actionInfoList) {
      sheet.setRowRememberIndex();

      formulaSubtotalsAction.reset();
      formulaSubtotalsAction.setRowStart(sheet.getRowCurrentIndex());
      for (AgentInfo agentInfo : actionInfo.agentInfoList) {
        sheet.createRow()
            .createCell(actionInfo.name, EStyle.BOLD_242_LEFT_TOP)
            .createCell(agentInfo.name, EStyle.NORMAL_242)
            .createCell(agentInfo.countTicketRefund, EStyle.NORMAL_242)
            .createCell(agentInfo.sumTicketRefund, EStyle.NORMAL_242_MONEY);
      }
      formulaSubtotalsAction.setRowEnd(sheet.getRowCurrentIndex() - 1);

      if (sheet.getRowRememberIndex() != sheet.getRowCurrentIndex() - 1) sheet.addMergedRegion(sheet.getRowRememberIndex(), sheet.getRowCurrentIndex() - 1, 0, 0);

      WrapRow row = sheet.createRow()
          .createCell("Итого", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      while (formulaSubtotalsAction.hasNext()) row.createCell(formulaSubtotalsAction.nextFormula(), formulaSubtotalsAction.isLastAfterNext() ? EStyle.BOLD_216_MONEY : EStyle.BOLD_216);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsActions.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow()
        .createCell("Итого по всем представлениям", EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191);
    while (formulaSubtotalsActions.hasNext()) row.createCell(formulaSubtotalsActions.nextFormula(), formulaSubtotalsActions.isLastAfterNext() ? EStyle.BOLD_191_MONEY : EStyle.BOLD_191);

    sheet.autoSizeColumn(0);
    sheet.autoSizeColumn(1);
    sheet.setColumnWidth(2, 2300);
    sheet.setColumnWidth(3, 2800);
  }

  @NotNull
  public Form2 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Map<ActionEventObj, SortedMap<RAgent, List<TicketObj>>> actionMap = new TreeMap<>(Comparators.ACTION_EVENT_BY_NAME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        if (ticket.getHolderStatus() != TicketObj.HolderStatus.REFUND) continue;
        SortedMap<RAgent, List<TicketObj>> agentMap = actionMap.get(ticket.getActionEvent());
        if (agentMap == null) actionMap.put(ticket.getActionEvent(), agentMap = new SortedMap<>(Comparators.AGENT_BY_NAME));
        List<TicketObj> ticketList = agentMap.get(order.getAgent());
        if (ticketList == null) agentMap.put(order.getAgent(), ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<ActionInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, SortedMap<RAgent, List<TicketObj>>> action : actionMap.entrySet()) {
      List<AgentInfo> agentInfoList = new ArrayList<>();
      for (Map.Entry<RAgent, List<TicketObj>> agent : action.getValue().entrySorted()) {
        int countTicketRefund = 0;
        BigDecimal sumTicketRefund = BigDecimal.ZERO;

        for (TicketObj ticket : agent.getValue()) {
          if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
            countTicketRefund++;
            sumTicketRefund = sumTicketRefund.add(getPrice(ticket));
          }
        }

        agentInfoList.add(new AgentInfo(agent.getKey().getName(), countTicketRefund, sumTicketRefund));
      }
      result.add(new ActionInfo(action.getKey().getActionName(), agentInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class ActionInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<AgentInfo> agentInfoList;

    private ActionInfo(@NotNull String name, @NotNull List<AgentInfo> agentInfoList) {
      this.name = name;
      this.agentInfoList = agentInfoList;
    }
  }

  private static final class AgentInfo {
    @NotNull
    private final String name;
    private final int countTicketRefund;
    @NotNull
    private final BigDecimal sumTicketRefund;

    private AgentInfo(@NotNull String name, int countTicketRefund, @NotNull BigDecimal sumTicketRefund) {
      this.name = name;
      this.countTicketRefund = countTicketRefund;
      this.sumTicketRefund = sumTicketRefund;
    }
  }
}
