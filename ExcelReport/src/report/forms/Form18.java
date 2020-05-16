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
 * Created by Inventor on 15.04.2019
 */
public final class Form18 extends AFormFilter<List<Form18.AgentInfo>> {
  public Form18(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_18, null, sign, filter, charge, discount);
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
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<AgentInfo> agentInfoList) {
    if (agentInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Агент", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("FID", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во заказов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во проданных билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Количество возвращенных билетов", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Кол-во билетов (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_TOP)
        .createCell("Сумма (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_TOP);

    FormulaSubtotals formulaSubtotalsAgent = new FormulaSubtotals('C', 5);//Итого
    FormulaSubtotals formulaSubtotalsAgents = new FormulaSubtotals('C', 5);//Итого по всем агентам

    formulaSubtotalsAgents.setRowStart(sheet.getRowCurrentIndex());
    for (AgentInfo agentInfo : agentInfoList) {
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

    sheet.autoSizeColumn(0);
    sheet.autoSizeColumn(1);
    sheet.setColumnWidth(2, 2300);
    sheet.setColumnWidth(3, 2800);
    sheet.setColumnWidth(4, 4000);
    sheet.setColumnWidth(5, 4800);
    sheet.setColumnWidth(6, 5500);
  }

  @NotNull
  public Form18 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Period period;
    if (getFilter().isFullReport()) period = Period.create(orderList);
    else period = getFilter().getPeriod();
    if (period == null) throw ValidationException.absent("Период");
    getHeader().add(EHeader.PERIOD, "С " + period.getFromFormat() + " по " + period.getToFormat());

    SortedMap<RAgent, SortedMap<RFrontend, List<OrderObj>>> agentMap = new SortedMap<>(Comparators.AGENT_BY_NAME);
    for (OrderObj order : orderList) {
      SortedMap<RFrontend, List<OrderObj>> frontendMap = agentMap.get(order.getAgent());
      if (frontendMap == null) agentMap.put(order.getAgent(), frontendMap = new SortedMap<>(Comparators.FRONTEND_BY_NAME));
      List<OrderObj> orders = frontendMap.get(order.getFrontend());
      if (orders == null) frontendMap.put(order.getFrontend(), orders = new ArrayList<>());
      orders.add(order);
    }

    List<AgentInfo> result = new ArrayList<>();
    for (Map.Entry<RAgent, SortedMap<RFrontend, List<OrderObj>>> agent : agentMap.entrySorted()) {
      List<FrontendInfo> frontendInfoList = new ArrayList<>();
      for (Map.Entry<RFrontend, List<OrderObj>> frontend : agent.getValue().entrySorted()) {
        int countOrder = 0;
        int countTicketSold = 0;
        int countTicketRefund = 0;
        BigDecimal sumTicketSold = BigDecimal.ZERO;
        BigDecimal sumTicketRefund = BigDecimal.ZERO;

        for (OrderObj order : frontend.getValue()) {
          countOrder++;

          for (TicketObj ticket : order.getTicketList()) {
            countTicketSold++;
            sumTicketSold = sumTicketSold.add(getPrice(ticket));

            if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(getPrice(ticket));
            }
          }
        }

        frontendInfoList.add(new FrontendInfo(frontend.getKey().getName(), countOrder, countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund));
      }
      result.add(new AgentInfo(agent.getKey().getName(), frontendInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class AgentInfo {
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
