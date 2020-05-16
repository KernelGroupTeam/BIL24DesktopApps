package report.forms;

import java.text.ParseException;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.*;
import report.models.Filter;
import report.reporter.enums.EPeriodType;
import report.utils.Comparators;
import report.utils.SortedMap;
import report.utils.*;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 12.03.2020
 */
public final class Form23 extends AFormFilter<List<Form23.ActionEventInfo>> {
  public Form23(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_23, null, sign, filter, false, false);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().remove(EHeader.ACQUIRING);
    getHeader().remove(EHeader.ACTION_EVENT);
    getHeader().remove(EHeader.AGENT);
    getHeader().remove(EHeader.FRONTEND);
    getHeader().remove(EHeader.SYSTEM);
    getHeader().remove(EHeader.GATEWAY);
    getHeader().remove(EHeader.FULL_REPORT);
    getHeader().remove(EHeader.ALL_STATUSES);
    getHeader().remove(EHeader.PERIOD_TYPE);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionEventInfo> actionEventInfoList) {
    if (actionEventInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("ID\nзаказа", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("ID\nбилета", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Начало\nсеанса", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Кол-во\nбилетов", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Категория", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Цена", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Скидка", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Штрих-код", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Статус\nвладельца", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Статус оплаты билета", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Время обмена\nэлектронного\nбилета", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP)
        .createCell("Подпись\nответственного\nсотрудника", EStyle.CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP);

    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('D', 'F', 'G');//Итого
    FormulaSubtotals formulaSubtotalsActionEvents = new FormulaSubtotals('D', 'F', 'G');//Итого по всем сеансам

    formulaSubtotalsActionEvents.setRowStart(sheet.getRowCurrentIndex() + 1);
    for (ActionEventInfo actionEventInfo : actionEventInfoList) {
      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 11);
      WrapRow row = sheet.createRow().createCell("Представление: " + actionEventInfo.name + '-' + actionEventInfo.showTime, EStyle.BOLD_242_CENTER);
      for (int i = 0; i < 11; i++) row.createCell(EStyle.EMPTY);

      formulaSubtotalsActionEvent.reset();
      formulaSubtotalsActionEvent.setRowStart(sheet.getRowCurrentIndex());
      for (TicketObj ticket : actionEventInfo.ticketList) {
        Date showDate = null;
        try {
          showDate = ticket.getActionEvent().getShowTimeDate();
        } catch (ParseException ignored) {//Не должно возникать, т.к. проверили в generateData
        }
        sheet.createRow()
            .createCell(ticket.getOrderId(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(ticket.getId(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(DateFormats.format(showDate, DateFormats.ETemplate.HHmm), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(1, EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(ticket.getCategory(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_JUSTIFY_CENTER)
            .createCell(ticket.getPrice(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER_MONEY)
            .createCell(ticket.getDiscount(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER_MONEY)
            .createCell(ticket.getBarcode(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(ticket.getHolderStatus().getDesc(), EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell("Оплачен", EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER)
            .createCell(EStyle.CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER);
      }
      formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      sheet.createRow()
          .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
          .createCell("Итого", EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
          .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
          .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY)
          .createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsActionEvents.setRowEnd(sheet.getRowCurrentIndex() - 2);

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 2);
    sheet.createRow()
        .createCell("Итого по всем сеансам", EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY)
        .createCell(formulaSubtotalsActionEvents.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY);

    sheet.setColumnPixel(0, 54);
    sheet.setColumnPixel(1, 54);
    sheet.setColumnPixel(2, 54);
    sheet.setColumnPixel(3, 54);
    sheet.setColumnPixel(4, 145);
    sheet.setColumnPixel(5, 54);
    sheet.setColumnPixel(6, 54);
    sheet.setColumnPixel(7, 110);
    sheet.setColumnPixel(8, 110);
    sheet.setColumnPixel(9, 110);
    sheet.setColumnPixel(10, 75);
    sheet.setColumnPixel(11, 110);
  }

  @NotNull
  public Form23 generateData(@Nullable List<OrderObj> orderList) throws ExcelReportException {
    if (orderList == null) throw ValidationException.absent("Список заказов");
    EPeriodType periodType = getFilter().getPeriodType();
    if (periodType == null) throw new NullPointerException("filter periodType is null");
    if (periodType != EPeriodType.SHOWS) throw new ExcelReportException("Должен быть выбран период \"сеансов\"");

    SortedMap<ActionEventObj, List<TicketObj>> actionEventMap = new SortedMap<>(Comparators.ACTION_EVENT_BY_SHOW_TIME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        ActionEventObj actionEvent = ticket.getActionEvent();
        String showTime = actionEvent.getShowTime();
        if (!ActionEventObj.checkFormat(showTime)) throw new ExcelReportException("Неверный формат даты " + showTime + " билета ticketId=" + ticket.getId());
        List<TicketObj> ticketList = actionEventMap.get(actionEvent);
        if (ticketList == null) actionEventMap.put(actionEvent, ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<ActionEventInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, List<TicketObj>> entry : actionEventMap.entrySorted()) {
      ActionEventObj actionEvent = entry.getKey();
      result.add(new ActionEventInfo(actionEvent.getActionName(), actionEvent.getShowTime(), entry.getValue()));
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
    private final List<TicketObj> ticketList;

    private ActionEventInfo(@NotNull String name, @NotNull String showTime, @NotNull List<TicketObj> ticketList) {
      this.name = name;
      this.showTime = showTime;
      this.ticketList = ticketList;
    }
  }
}
