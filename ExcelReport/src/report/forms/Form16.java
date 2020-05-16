package report.forms;

import java.util.*;

import excel.enums.EStyle;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import report.models.Filter;
import report.utils.Comparators;
import report.utils.SortedMap;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 04.12.2017
 */
public final class Form16 extends AFormFilter<List<Form16.ActionEventInfo>> {
  public Form16(@Nullable String sign, @Nullable Filter filter) throws ValidationException {
    super(EForm.FORM_16, null, sign, filter, false, false);
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<ActionEventInfo> actionEventInfoList) {
    if (actionEventInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(47.25f)
        .createCell("Сектор", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Ряд", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Место", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Штрихкод", EStyle.NORMAL_CENTER_CENTER);

    for (ActionEventInfo actionEventInfo : actionEventInfoList) {
      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 3);
      WrapRow row = sheet.createRow().createCell("Представление: " + actionEventInfo.name + '-' + actionEventInfo.showTime, EStyle.BOLD_242_CENTER);
      for (int i = 0; i < 3; i++) row.createCell(EStyle.EMPTY);

      for (TicketInfo ticketInfo : actionEventInfo.ticketInfoList) {
        sheet.createRow()
            .createCell(ticketInfo.sector, EStyle.NORMAL_242)
            .createCell(ticketInfo.row, EStyle.NORMAL_242)
            .createCell(ticketInfo.number, EStyle.NORMAL_242)
            .createCell(ticketInfo.barcode, EStyle.NORMAL_242);
      }

      sheet.incRowCurrentIndex();
    }

    sheet.autoSizeColumn(0);
    sheet.setColumnWidth(1, 2200);
    sheet.setColumnWidth(2, 2600);
    sheet.autoSizeColumn(3);
  }

  @NotNull
  public Form16 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    SortedMap<ActionEventObj, List<TicketObj>> actionEventMap = new SortedMap<>(Comparators.ACTION_EVENT_BY_SHOW_TIME);
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) continue;
        List<TicketObj> ticketList = actionEventMap.get(ticket.getActionEvent());
        if (ticketList == null) actionEventMap.put(ticket.getActionEvent(), ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<ActionEventInfo> result = new ArrayList<>();
    for (Map.Entry<ActionEventObj, List<TicketObj>> actionEvent : actionEventMap.entrySorted()) {
      Collections.sort(actionEvent.getValue(), Comparators.TICKET_BY_SEAT_LOCATION);
      List<TicketInfo> ticketInfoList = new ArrayList<>(actionEvent.getValue().size());
      for (TicketObj ticket : actionEvent.getValue()) {
        SeatLocationObj seatLocation = ticket.getSeatLocation();
        TicketInfo ticketInfo;
        if (seatLocation == null) {
          ticketInfo = new TicketInfo("_", "_", "_", ticket.getBarcode());
        }
        else {
          String sector = seatLocation.getSector();
          if (isDigit(sector)) sector = "Сектор " + sector;
          ticketInfo = new TicketInfo(sector, seatLocation.getRow(), seatLocation.getNumber(), ticket.getBarcode());
        }
        ticketInfoList.add(ticketInfo);
      }
      result.add(new ActionEventInfo(actionEvent.getKey().getActionName(), actionEvent.getKey().getShowTime(), ticketInfoList));
    }
    setData(result);

    return this;
  }

  private static boolean isDigit(@NotNull String sector) {
    for (char ch : sector.toCharArray()) {
      if (!Character.isDigit(ch)) return false;
    }
    return true;
  }

  protected static final class ActionEventInfo {
    @NotNull
    private final String name;
    @NotNull
    private final String showTime;
    @NotNull
    private final List<TicketInfo> ticketInfoList;

    private ActionEventInfo(@NotNull String name, @NotNull String showTime, @NotNull List<TicketInfo> ticketInfoList) {
      this.name = name;
      this.showTime = showTime;
      this.ticketInfoList = ticketInfoList;
    }
  }

  private static final class TicketInfo {
    @NotNull
    private final String sector;
    @NotNull
    private final String row;
    @NotNull
    private final String number;
    @NotNull
    private final String barcode;

    private TicketInfo(@NotNull String sector, @NotNull String row, @NotNull String number, @NotNull String barcode) {
      this.sector = sector;
      this.row = row;
      this.number = number;
      this.barcode = barcode;
    }
  }
}
