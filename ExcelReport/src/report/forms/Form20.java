package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.*;
import excel.interfaces.IFormula;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.Comparators;
import report.utils.*;
import report.utils.SortedMap;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 07.01.2020
 */
public final class Form20 extends AFormFilter<List<Form20.AgentInfo>> {
  @NotNull
  private static final BigDecimal CONST_1_PERCENT = new BigDecimal("0.01");
  @NotNull
  private static final BigDecimal CONST_5_000 = BigDecimal.valueOf(5_000);
  @NotNull
  private static final BigDecimal CONST_500_000 = BigDecimal.valueOf(500_000);

  public Form20(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_20, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader()
        .add(EHeader.NAME, getHeader().remove(EHeader.NAME))
        .add(EHeader.NUMBER, Long.toString(System.currentTimeMillis()))
        .add(EHeader.CREATED, DateFormats.format(new Date(), DateFormats.ETemplate.ddMMyyyyHHmmss))
        .add(EHeader.PERIOD, getHeader().remove(EHeader.PERIOD))
        .add(EHeader.ACQUIRING, getHeader().remove(EHeader.ACQUIRING))
        .add(EHeader.ORGANIZER, getHeader().remove(EHeader.ORGANIZER))
        .add(EHeader.CITY, getHeader().remove(EHeader.CITY))
        .add(EHeader.VENUE, getHeader().remove(EHeader.VENUE))
        .add(EHeader.ACTION, getHeader().remove(EHeader.ACTION))
        .add(EHeader.ACTION_EVENT, getHeader().remove(EHeader.ACTION_EVENT))
        .add(EHeader.OPERATOR, "ООО РЕГИОН ИНН 2320211100")
        .add(EHeader.AGENT, getHeader().remove(EHeader.AGENT))
        .add(EHeader.FRONTEND, getHeader().remove(EHeader.FRONTEND))
        .add(EHeader.SYSTEM, getHeader().remove(EHeader.SYSTEM))
        .add(EHeader.GATEWAY, getHeader().remove(EHeader.GATEWAY))
        .add(EHeader.FULL_REPORT, getHeader().remove(EHeader.FULL_REPORT))
        .add(EHeader.ALL_STATUSES, getHeader().remove(EHeader.ALL_STATUSES))
        .add(EHeader.PERIOD_TYPE, getHeader().remove(EHeader.PERIOD_TYPE))
        .add(EHeader.CHARGE, isCharge())
        .add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<AgentInfo> agentInfoList) {
    if (agentInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Агент", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Представление", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Скидка\n(руб.)", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("С/Сбор\n(руб.)", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма\n(руб.)", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('C', 4);//Итого
    FormulaSums formulaSumsPaymentUsing = new FormulaSums('F');//Оплата за использование BIL24
    FormulaSubtotals formulaSubtotalsAgents = new FormulaSubtotals('C', 4);//Итого по всем агентам

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
            .createCell(actionInfo.sumTicketDiscount, EStyle.NORMAL_242_MONEY)
            .createCell(actionInfo.sumTicketCharge, EStyle.NORMAL_242_MONEY)
            .createCell(actionInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
      }
      formulaSubtotalsAgent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      if (sheet.getRowRememberIndex() != sheet.getRowCurrentIndex() - 1) sheet.addMergedRegion(sheet.getRowRememberIndex(), sheet.getRowCurrentIndex() - 1, 0, 0);

      WrapRow row = sheet.createRow()
          .createCell("Итого", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      for (; formulaSubtotalsAgent.hasNext();) {
        if (formulaSubtotalsAgent.isFirstBeforeNext()) row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_216);
        else row.createCell(formulaSubtotalsAgent.nextFormula(), EStyle.BOLD_216_MONEY);
      }

      sheet.createRow()
          .createCell("Оплата за использование BIL24", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216)
          .createCell(agentInfo.sumPaymentUsing, EStyle.BOLD_216_COUNT);
      formulaSumsPaymentUsing.add(sheet.getRowCurrentIndex() - 1);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsAgents.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow()
        .createCell("Итого по всем агентам", EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191);
    for (; formulaSubtotalsAgents.hasNext();) {
      if (formulaSubtotalsAgent.isFirstBeforeNext()) row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_191);
      else if (formulaSubtotalsAgents.isLastBeforeNext()) {
        IFormula formula = formulaSubtotalsAgents.nextFormula();
        row.createCellFormula(formula.getFormula() + "-" + formula.getColumn() + (sheet.getRowCurrentIndex() + 1), EStyle.BOLD_191_MONEY);
      }
      else row.createCell(formulaSubtotalsAgents.nextFormula(), EStyle.BOLD_191_MONEY);
    }

    sheet.createRow()
        .createCell("Оплата за использование BIL24 по всем агентам", EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(EStyle.BOLD_191)
        .createCell(formulaSumsPaymentUsing.next(), EStyle.BOLD_191_COUNT);

    sheet.incRowCurrentIndex();

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("При продаже билетов Пользователь использовал билетную платформу BIL24 в соответствии с договором-офертой, размещенным по адресу", EStyle.NORMAL);
    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("https://bil24.pro/operator_agreement.pdf. Стороны не имеют претензий, связанных с использованием платформы и выполнением договора-оферты.", EStyle.NORMAL);

    sheet.incRowCurrentIndex();
    sheet.incRowCurrentIndex();

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("Подписи сторон:", EStyle.NORMAL);
    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("Пользователь: ______________________/_____________/", EStyle.NORMAL);
    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("Оператор:        ______________________/_____________/", EStyle.NORMAL);

    sheet.autoSizeColumn(0);
    sheet.autoSizeColumn(1);
    sheet.setColumnWidth(2, 2300);
    sheet.setColumnWidth(3, 2800);
    sheet.setColumnWidth(4, 2800);
    sheet.setColumnWidth(5, 2800);
    sheet.setColumnWidth(6, 2800);
  }

  @NotNull
  public Form20 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
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
        BigDecimal sumTicketDiscount = BigDecimal.ZERO;
        BigDecimal sumTicketCharge = BigDecimal.ZERO;
        BigDecimal sumTicketTotal = BigDecimal.ZERO;

        for (TicketObj ticket : action.getValue()) {
          countTicketSold++;
          if (isDiscount()) sumTicketDiscount = sumTicketDiscount.add(ticket.getDiscount());
          if (isCharge()) sumTicketCharge = sumTicketCharge.add(ticket.getCharge());
          sumTicketTotal = sumTicketTotal.add(getPrice(ticket));
        }

        actionInfoList.add(new ActionInfo(action.getKey().getActionName(), countTicketSold, sumTicketDiscount, sumTicketCharge, sumTicketTotal));
      }
      result.add(new AgentInfo(agent.getKey().getName(), actionInfoList, generateSumPaymentUsing(actionInfoList)));
    }
    setData(result);

    return this;
  }

  @NotNull
  private static BigDecimal generateSumPaymentUsing(@NotNull List<ActionInfo> actionInfoList) {
    BigDecimal sumTicketTotal = BigDecimal.ZERO;
    for (ActionInfo actionInfo : actionInfoList) sumTicketTotal = sumTicketTotal.add(actionInfo.sumTicketTotal);
    if (sumTicketTotal.compareTo(CONST_500_000) > 0) return sumTicketTotal.multiply(CONST_1_PERCENT);
    return CONST_5_000;
  }

  protected static final class AgentInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<ActionInfo> actionInfoList;
    @NotNull
    private final BigDecimal sumPaymentUsing;

    private AgentInfo(@NotNull String name, @NotNull List<ActionInfo> actionInfoList, @NotNull BigDecimal sumPaymentUsing) {
      this.name = name;
      this.actionInfoList = actionInfoList;
      this.sumPaymentUsing = sumPaymentUsing;
    }
  }

  private static final class ActionInfo {
    @NotNull
    private final String name;
    private final int countTicketSold;
    @NotNull
    private final BigDecimal sumTicketDiscount;
    @NotNull
    private final BigDecimal sumTicketCharge;
    @NotNull
    private final BigDecimal sumTicketTotal;


    private ActionInfo(@NotNull String name, int countTicketSold, @NotNull BigDecimal sumTicketDiscount,
                       @NotNull BigDecimal sumTicketCharge, @NotNull BigDecimal sumTicketTotal) {
      this.name = name;
      this.countTicketSold = countTicketSold;
      this.sumTicketDiscount = sumTicketDiscount;
      this.sumTicketCharge = sumTicketCharge;
      this.sumTicketTotal = sumTicketTotal;
    }
  }
}
