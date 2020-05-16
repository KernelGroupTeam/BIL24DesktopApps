package client.reporter.model;

import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.*;
import server.protocol2.common.BarcodeFormat;
import server.protocol2.reporter.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class TicketTableModel extends AbstractTableModel {
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static final String[] columnNames = {"ID заказа", "ID билета", "ID места",
      "Сектор", "Ряд", "Место", "Категория",
      "Цена", "Скидка", "Причина скидки", "<html><center>Сервисный<br>сбор</center></html>", "Итого",
      "Штрих-код", "Формат ШК", "<html><center>Статус<br>владельца</center></html>", "<html><center>Дата<br>возврата</center></html>", "<html><center>ID<br>сеанса</center></html>", "Начало сеанса",
      "<html><center>ID места<br>проведения</center></html>", "Название места проведения", "<html><center>ID<br>представления</center></html>", "Название представления"};
  private static final Class<?>[] columnClasses = {Long.class, Long.class, Long.class,
      String.class, String.class, String.class, String.class,
      BigDecimal.class, BigDecimal.class, String.class, BigDecimal.class, BigDecimal.class,
      String.class, String.class, String.class, String.class, Long.class, String.class,
      Long.class, String.class, Long.class, String.class};
  private Object[][] data = new Object[0][0];
  @NotNull
  private List<TicketObj> ticketList = Collections.emptyList();
  private boolean discountReason = false;

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnClasses[columnIndex];
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  public void setData(List<TicketObj> ticketList) {
    this.ticketList = ticketList;
    discountReason = false;
    Object[][] buffer = new Object[ticketList.size() + 1][columnNames.length];
    BigDecimal price = BigDecimal.ZERO;
    BigDecimal discount = BigDecimal.ZERO;
    BigDecimal charge = BigDecimal.ZERO;
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (int i = 0; i < ticketList.size(); i++) {
      TicketObj element = ticketList.get(i);
      boolean fakeTicket = isFakeTicket(element);
      price = price.add(element.getPrice());
      discount = discount.add(element.getDiscount());
      charge = charge.add(element.getCharge());
      totalPrice = totalPrice.add(element.getTotalPrice());
      buffer[i][0] = element.getOrderId();
      buffer[i][1] = fakeTicket ? null : element.getId();
      buffer[i][2] = element.getSeatId();
      SeatLocationObj seatLocation = element.getSeatLocation();
      if (seatLocation != null) {
        buffer[i][3] = seatLocation.getSector();
        buffer[i][4] = seatLocation.getRow();
        buffer[i][5] = seatLocation.getNumber();
      } else {
        buffer[i][3] = "";
        buffer[i][4] = "";
        buffer[i][5] = "";
      }
      buffer[i][6] = element.getCategory();
      buffer[i][7] = element.getPrice();
      buffer[i][8] = element.getDiscount();
      if (element.getDiscountReason() != null) discountReason = true;
      buffer[i][9] = element.getDiscountReason();
      buffer[i][10] = element.getCharge();
      buffer[i][11] = element.getTotalPrice();
      buffer[i][12] = fakeTicket ? null : element.getBarcode();
      buffer[i][13] = fakeTicket ? null : element.getBarcodeFormat().getName();
      buffer[i][14] = fakeTicket ? null : element.getHolderStatus().getDesc();
      buffer[i][15] = dateFormat(element.getRefundDate());
      ActionEventObj actionEvent = element.getActionEvent();
      buffer[i][16] = actionEvent.getId();
      try {
        buffer[i][17] = dateFormat.format(actionEvent.getShowTimeDate());
      } catch (ParseException e) {
        buffer[i][17] = actionEvent.getShowTime();
      }
      buffer[i][18] = actionEvent.getVenueId();
      buffer[i][19] = actionEvent.getVenueName();
      buffer[i][20] = actionEvent.getActionId();
      buffer[i][21] = actionEvent.getActionName();
    }
    int lastRow = buffer.length - 1;
    buffer[lastRow][5] = "Итого: " + ticketList.size();
    buffer[lastRow][7] = price;
    buffer[lastRow][8] = discount;
    buffer[lastRow][10] = charge;
    buffer[lastRow][11] = totalPrice;
    if (ticketList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }

  @Nullable
  private String dateFormat(@Nullable String date) {
    if (date == null) return null;
    try {
      return dateFormat.format(TicketObj.parseFormat(date));
    } catch (ParseException e) {
      return date;
    }
  }

  public void refresh() {
    setData(ticketList);
  }

  @NotNull
  public TicketObj getTicket(int row) {
    return ticketList.get(row);
  }

  @NotNull
  public List<Long> getTicketIdList(int[] rows) {
    List<Long> result = new ArrayList<>();
    for (int row : rows) {
      if (row < ticketList.size()) {
        TicketObj ticket = ticketList.get(row);
        if (!isFakeTicket(ticket)) result.add(ticket.getId());
      }
    }
    return result;
  }

  @Nullable
  public TicketObj.HolderStatus getHolderStatus(int row) {
    if (row >= ticketList.size()) return null;
    return ticketList.get(row).getHolderStatus();
  }

  public static boolean isFakeTicket(@NotNull TicketObj ticket) {//еще не билет, просто место в заказе
    return ticket.getBarcodeFormat().equals(BarcodeFormat.getNone());
  }

  public boolean isDiscountReason() {
    return discountReason;
  }
}
