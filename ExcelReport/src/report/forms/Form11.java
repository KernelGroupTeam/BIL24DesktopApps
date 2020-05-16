package report.forms;

import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import report.models.Filter;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 25.11.2017
 */
public final class Form11 extends AFormFilter<List<TicketObj>> {
  public Form11(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_11, null, sign, filter, false, false);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<TicketObj> ticketList) {
    if (ticketList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("ID\nзаказа", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("ID\nбилета", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("ID\nместа", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Сектор", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Ряд", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Место", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Категория", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Цена", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Скидка", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Сервисный\nсбор", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Итого", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Штрих-код", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Статус\nвладельца", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("ID\nсеанса", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Начало\nсеанса", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("ID\nместа\nпроведения", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Название\nместа\nпроведения", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("ID\nпредставления", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP)
        .createCell("Название\nпредставления", EStyle.SMALL_NORMAL_CENTER_CENTER_WRAP);

    FormulaSubtotals formulaSubtotals = new FormulaSubtotals('H', 4);//Итого
    formulaSubtotals.setRowStart(sheet.getRowCurrentIndex());
    for (TicketObj ticket : ticketList) {
      SeatLocationObj seatLocation = ticket.getSeatLocation();
      ActionEventObj actionEvent = ticket.getActionEvent();

      WrapRow row = sheet.createRow()
          .createCell(ticket.getOrderId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(ticket.getId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(ticket.getSeatId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER);
      if (seatLocation == null) {
        row
            .createCell(EStyle.SMALL_NORMAL_242_CENTER_CENTER)
            .createCell(EStyle.SMALL_NORMAL_242_CENTER_CENTER)
            .createCell(EStyle.SMALL_NORMAL_242_CENTER_CENTER);
      } else {
        row
            .createCell(seatLocation.getSector(), EStyle.SMALL_NORMAL_242_CENTER_JUSTIFY)
            .createCell(seatLocation.getRow(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
            .createCell(seatLocation.getNumber(), EStyle.SMALL_NORMAL_242_CENTER_CENTER);
      }
      row
          .createCell(ticket.getCategory(), EStyle.SMALL_NORMAL_242_CENTER_JUSTIFY)
          .createCell(ticket.getPrice(), EStyle.SMALL_NORMAL_242_CENTER_CENTER_MONEY)
          .createCell(ticket.getDiscount(), EStyle.SMALL_NORMAL_242_CENTER_CENTER_MONEY)
          .createCell(ticket.getCharge(), EStyle.SMALL_NORMAL_242_CENTER_CENTER_MONEY)
          .createCell(ticket.getTotalPrice(), EStyle.SMALL_NORMAL_242_CENTER_CENTER_MONEY)
          .createCell(ticket.getBarcode(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(ticket.getHolderStatus().getDesc(), EStyle.SMALL_NORMAL_242_CENTER_JUSTIFY)
          .createCell(actionEvent.getId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(actionEvent.getShowTime(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(actionEvent.getVenueId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(actionEvent.getVenueName(), EStyle.SMALL_NORMAL_242_CENTER_JUSTIFY)
          .createCell(actionEvent.getActionId(), EStyle.SMALL_NORMAL_242_CENTER_CENTER)
          .createCell(actionEvent.getActionName(), EStyle.SMALL_NORMAL_242_CENTER_JUSTIFY);
    }
    formulaSubtotals.setRowEnd(sheet.getRowCurrentIndex() - 1);

    sheet.incRowCurrentIndex();

    WrapRow row = sheet.createRow().createCell("Итого", EStyle.SMALL_BOLD_216);
    for (int i = 0; i < 4; i++) row.createCell(EStyle.SMALL_BOLD_216);
    row
        .createCell(ticketList.size(), EStyle.SMALL_BOLD_216)
        .createCell(EStyle.SMALL_BOLD_216);
    while (formulaSubtotals.hasNext()) row.createCell(formulaSubtotals.nextFormula(), EStyle.SMALL_BOLD_216_MONEY);
    for (int i = 0; i < 8; i++) row.createCell(EStyle.SMALL_BOLD_216);

    sheet.setColumnWidth(0, 1200);
    sheet.setColumnWidth(1, 1600);
    sheet.setColumnWidth(2, 1800);
    sheet.setColumnWidth(3, 1500);
    sheet.setColumnWidth(4, 1100);
    sheet.setColumnWidth(5, 1200);
    sheet.setColumnWidth(6, 2000);
    sheet.setColumnWidth(7, 1500);
    sheet.setColumnWidth(8, 1500);
    sheet.setColumnWidth(9, 1500);
    sheet.setColumnWidth(10, 1500);
    sheet.setColumnWidth(11, 2700);
    sheet.setColumnWidth(12, 2000);
    sheet.setColumnWidth(13, 1400);
    sheet.setColumnWidth(14, 2900);
    sheet.setColumnWidth(15, 1600);
    sheet.setColumnWidth(16, 2900);
    sheet.setColumnWidth(17, 1600);
    sheet.setColumnWidth(18, 2900);

    sheet.setOrientation(PrintOrientation.LANDSCAPE);
    sheet.setMargin(Sheet.LeftMargin, .2d);
    sheet.setMargin(Sheet.RightMargin, .2d);
    sheet.setMargin(Sheet.TopMargin, .2d);
    sheet.setMargin(Sheet.BottomMargin, .2d);
  }

  @NotNull
  public Form11 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    List<TicketObj> result = new ArrayList<>();
    for (OrderObj order : orderList) result.addAll(order.getTicketList());
    setData(result);

    return this;
  }
}
