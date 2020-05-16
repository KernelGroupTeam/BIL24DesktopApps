package report.forms;

import java.text.ParseException;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.WrapSheet;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.*;
import report.models.Filter;
import report.utils.DateFormats;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 25.11.2017
 */
public final class Form21 extends AFormFilter<List<TicketObj>> {
  public Form21(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_21, null, sign, filter, false, false);
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
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<TicketObj> ticketList) {
    if (ticketList.isEmpty()) return;

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

    FormulaSubtotals formulaSubtotals = new FormulaSubtotals('D', 'F', 'G');//Итого
    formulaSubtotals.setRowStart(sheet.getRowCurrentIndex());
    for (TicketObj ticket : ticketList) {
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
    formulaSubtotals.setRowEnd(sheet.getRowCurrentIndex() - 1);

    sheet.incRowCurrentIndex();

    sheet.createRow()
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell("Итого", EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(formulaSubtotals.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER)
        .createCell(formulaSubtotals.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY)
        .createCell(formulaSubtotals.nextFormula(), EStyle.CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY);

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
  public Form21 generateData(@Nullable List<OrderObj> orderList) throws ExcelReportException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    List<TicketObj> result = new ArrayList<>();
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        String showTime = ticket.getActionEvent().getShowTime();
        if (!ActionEventObj.checkFormat(showTime)) throw new ExcelReportException("Неверный формат даты " + showTime + " билета ticketId=" + ticket.getId());
        result.add(ticket);
      }
    }
    setData(result);

    return this;
  }
}
