package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.Comparators;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 25.11.2017
 */
public final class Form5 extends AFormFilter<List<Form5.TotalInfo>> {
  public Form5(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_5, null, sign, filter, false, false);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<TotalInfo> totalInfoList) {
    if (totalInfoList.isEmpty()) return;

    sheet.createRow()
        .createCell("ID заказа", EStyle.NORMAL_191_CENTER_CENTER)
        .createCell("Сектор", EStyle.NORMAL_191_CENTER_CENTER)
        .createCell("Ряд", EStyle.NORMAL_191_CENTER_CENTER)
        .createCell("Место", EStyle.NORMAL_191_CENTER_CENTER)
        .createCell("Категория", EStyle.NORMAL_191_CENTER_CENTER)
        .createCell("Номинальная\nцена", EStyle.NORMAL_191_CENTER_CENTER_WRAP);

    for (TotalInfo totalInfo : totalInfoList) {
      EStyle[][] styles = {
          {EStyle.NORMAL_216_RIGHT, EStyle.NORMAL_WHITE_RIGHT},
          {EStyle.NORMAL_216_RIGHT_MONEY, EStyle.NORMAL_WHITE_RIGHT_MONEY}
      };
      int styleIndex = 0;

      for (TicketInfo ticketInfo : totalInfo.ticketInfoList) {
        sheet.createRow()
            .createCell(ticketInfo.orderId, styles[0][styleIndex])
            .createCell(ticketInfo.sector, styles[0][styleIndex])
            .createCell(ticketInfo.row, styles[0][styleIndex])
            .createCell(ticketInfo.number, styles[0][styleIndex])
            .createCell(ticketInfo.category, styles[0][styleIndex])
            .createCell(ticketInfo.price, styles[1][styleIndex]);
        styleIndex = styleIndex == 0 ? 1 : 0;
      }

      sheet.incRowCurrentIndex();

      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 4);
      WrapRow row = sheet.createRow().createCell(String.format("Итого: %d билетов на сумму %,.2f руб", totalInfo.countTicketTotal, totalInfo.sumTicketTotal), EStyle.BOLD_191_CENTER);
      for (int i = 0; i < 4; i++) row.createCell(EStyle.EMPTY);
    }

    sheet.setColumnWidth(0, 2700);
    sheet.autoSizeColumn(1);
    sheet.setColumnWidth(2, 2200);
    sheet.setColumnWidth(3, 2600);
    sheet.autoSizeColumn(4);
    sheet.setColumnWidth(5, 3600);
  }

  @NotNull
  public Form5 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    List<TicketObj> ticketList = new ArrayList<>();
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) continue;
        ticketList.add(ticket);
      }
    }
    Collections.sort(ticketList, Comparators.TICKET_BY_SEAT_LOCATION);

    List<TotalInfo> result = new ArrayList<>();
    List<TicketInfo> ticketInfoList = new ArrayList<>();
    BigDecimal sumTicketTotal = BigDecimal.ZERO;
    for (TicketObj ticket : ticketList) {
      sumTicketTotal = sumTicketTotal.add(ticket.getPrice());

      SeatLocationObj seatLocation = ticket.getSeatLocation();
      TicketInfo ticketInfo;
      if (seatLocation == null) ticketInfo = new TicketInfo(ticket.getOrderId(), "0", "0", "0", ticket.getCategory(), ticket.getPrice());
      else ticketInfo = new TicketInfo(ticket.getOrderId(), seatLocation.getSector(), seatLocation.getRow(), seatLocation.getNumber(), ticket.getCategory(), ticket.getPrice());
      ticketInfoList.add(ticketInfo);
    }
    result.add(new TotalInfo(sumTicketTotal, ticketInfoList));
    setData(result);

    return this;
  }

  protected static final class TotalInfo {
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketTotal;
    @NotNull
    private final List<TicketInfo> ticketInfoList;

    private TotalInfo(@NotNull BigDecimal sumTicketTotal, @NotNull List<TicketInfo> ticketInfoList) {
      this.countTicketTotal = ticketInfoList.size();
      this.sumTicketTotal = sumTicketTotal;
      this.ticketInfoList = ticketInfoList;
    }
  }

  private static final class TicketInfo {
    private final long orderId;
    @NotNull
    private final String sector;
    @NotNull
    private final String row;
    @NotNull
    private final String number;
    @NotNull
    private final String category;
    @NotNull
    private final BigDecimal price;

    private TicketInfo(long orderId, @NotNull String sector, @NotNull String row, @NotNull String number, @NotNull String category, @NotNull BigDecimal price) {
      this.orderId = orderId;
      this.sector = sector;
      this.row = row;
      this.number = number;
      this.category = category;
      this.price = price;
    }
  }
}
