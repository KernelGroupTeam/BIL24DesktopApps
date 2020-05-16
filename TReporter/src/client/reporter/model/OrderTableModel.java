package client.reporter.model;

import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import client.reporter.Env;
import org.jetbrains.annotations.*;
import server.protocol2.UserType;
import server.protocol2.common.*;
import server.protocol2.reporter.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class OrderTableModel extends AbstractTableModel {
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String[] columnNames = {"ID", "Дата создания", "ДБ", "Дата истечения", "Дата завершения",
      "Сумма", "Скидка", "С/сбор", "Итого", "Билетов", "Сумма фильтр", "Скидка фильтр", "С/сбор фильтр", "Итого фильтр", "Билетов фильтр",
      "Статус", "Агент", "Интерфейс", "FID", "ВБС", "e-mail доставки", "Отправлено", "Телефон", "ФИ",
      "ID клиента", "ID сессии", "Эквайринг", "ID транзакции", "Статус транзакции", "Комментарий транзакции"};
  private static final Class<?>[] columnClasses = {Long.class, String.class, String.class, String.class, String.class,
      BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, Integer.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, BigDecimal.class, Integer.class,
      String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
      Long.class, String.class, String.class, String.class, String.class, String.class};
  private Object[][] data = new Object[0][0];
  @NotNull
  private List<OrderObj> orderList = Collections.emptyList();

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

  public void setData(@NotNull List<OrderObj> orderList) {
    this.orderList = orderList;
    Object[][] buffer = new Object[orderList.size() + 1][columnNames.length];
    BigDecimal sum = BigDecimal.ZERO;
    BigDecimal filteredSum = BigDecimal.ZERO;
    BigDecimal discount = BigDecimal.ZERO;
    BigDecimal filteredDiscount = BigDecimal.ZERO;
    BigDecimal charge = BigDecimal.ZERO;
    BigDecimal filteredCharge = BigDecimal.ZERO;
    BigDecimal totalSum = BigDecimal.ZERO;
    BigDecimal filteredTotalSum = BigDecimal.ZERO;
    int ticketQuantity = 0;
    int filteredTicketQuantity = 0;
    boolean showGatewayOrganizer = (Env.user.getUserType() == UserType.OPERATOR || Env.user.getUserType() == UserType.AGENT);
    for (int i = 0; i < orderList.size(); i++) {
      OrderObj element = orderList.get(i);
      if (element.getStatus() == OrderObj.Status.PAID) {
        sum = sum.add(element.getSum());
        filteredSum = filteredSum.add(element.getFilteredSum());
        discount = discount.add(element.getDiscount());
        filteredDiscount = filteredDiscount.add(element.getFilteredDiscount());
        charge = charge.add(element.getCharge());
        filteredCharge = filteredCharge.add(element.getFilteredCharge());
        totalSum = totalSum.add(element.getTotalSum());
        filteredTotalSum = filteredTotalSum.add(element.getFilteredTotalSum());
        ticketQuantity += element.getTicketQuantity();
        filteredTicketQuantity += element.getFilteredTicketQuantity();
      }
      buffer[i][0] = element.getId();
      buffer[i][1] = dateFormat(element.getDate());
      buffer[i][2] = element.isLongReservation() ? "да" : "нет";
      buffer[i][3] = dateFormat(element.getExpiration());
      buffer[i][4] = dateFormat(element.getProcessing());
      buffer[i][5] = element.getSum();
      buffer[i][6] = element.getDiscount();
      buffer[i][7] = element.getCharge();
      buffer[i][8] = element.getTotalSum();
      buffer[i][9] = element.getTicketQuantity();
      buffer[i][10] = element.getFilteredSum();
      buffer[i][11] = element.getFilteredDiscount();
      buffer[i][12] = element.getFilteredCharge();
      buffer[i][13] = element.getFilteredTotalSum();
      buffer[i][14] = element.getFilteredTicketQuantity();
      buffer[i][15] = element.getStatus().getDesc();
      buffer[i][16] = element.getAgent().getName();
      buffer[i][17] = element.getFrontend().getName();
      buffer[i][18] = element.getFrontend().getIdStr();
      StringBuilder str = new StringBuilder();
      for (GatewayOrderObj gatewayOrderObj : element.getGatewayOrderList()) {
        GatewayObj gateway = gatewayOrderObj.getGateway();
        str.append('[').append(gateway.getId()).append("][");
        if (showGatewayOrganizer) str.append(gateway.getNameWithOrganizer());
        else str.append(gateway.getName());
        str.append("]id=").append(gatewayOrderObj.getOrderId()).append(';');
      }
      int len = str.length();
      if (len > 0 && str.charAt(len - 1) == ';') str.setLength(len - 1);
      buffer[i][19] = str.toString();
      buffer[i][20] = element.getEmail();
      if (element.getEmailSent() != null) {
        buffer[i][21] = element.getEmailSent() ? "да" : "нет";
      } else {
        buffer[i][21] = "";
      }
      buffer[i][22] = element.getPhone();
      buffer[i][23] = element.getFullName();
      buffer[i][24] = element.getUser().getId();
      buffer[i][25] = element.getUser().getSessionId();
      PaymentMethodObj paymentMethod = element.getPaymentMethod();
      buffer[i][26] = (paymentMethod.getId() == 2 ? paymentMethod.getName() : element.getAcquiring().getName());
      buffer[i][27] = element.getPaymentBankId();
      buffer[i][28] = element.getPaymentBankStatus();
      buffer[i][29] = element.getPaymentBankMessage();
    }
    int lastRow = buffer.length - 1;
    buffer[lastRow][1] = "Итого: " + orderList.size();
    buffer[lastRow][5] = sum;
    buffer[lastRow][6] = discount;
    buffer[lastRow][7] = charge;
    buffer[lastRow][8] = totalSum;
    buffer[lastRow][9] = ticketQuantity;
    buffer[lastRow][10] = filteredSum;
    buffer[lastRow][11] = filteredDiscount;
    buffer[lastRow][12] = filteredCharge;
    buffer[lastRow][13] = filteredTotalSum;
    buffer[lastRow][14] = filteredTicketQuantity;
    if (orderList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }

  @Nullable
  private String dateFormat(@Nullable String date) {
    if (date == null) return null;
    try {
      return dateFormat.format(OrderObj.parseFormat(date));
    } catch (ParseException e) {
      return date;
    }
  }

  @NotNull
  public OrderObj getOrder(int row) {
    return orderList.get(row);
  }

  @NotNull
  public List<TicketObj> getTickets(int[] rows) {
    List<TicketObj> result = new ArrayList<>();
    for (int row : rows) {
      if (row < orderList.size()) {
        OrderObj order = orderList.get(row);
        result.addAll(order.getTicketList());
        result.addAll(order.getSeatList());
      }
    }
    return result;
  }

  @Nullable
  public OrderObj.Status getOrderStatus(int row) {
    if (row >= orderList.size()) return null;
    return orderList.get(row).getStatus();
  }

  public boolean isNotSent(int row) {
    if (row >= orderList.size()) return false;
    OrderObj order = getOrder(row);
    Boolean emailSent = order.getEmailSent();
    return Boolean.FALSE.equals(emailSent);
  }

  public void setOrderEmail(long orderId, String email) {
    for (int i = 0; i < orderList.size(); i++) {
      OrderObj orderObj = orderList.get(i);
      if (orderObj.getId() == orderId) {
        orderObj.setEmail(email);
        orderObj.setEmailSent(Boolean.FALSE);
        data[i][17] = email;
        data[i][18] = "нет";
        fireTableCellUpdated(i, 17);
        fireTableCellUpdated(i, 18);
        return;
      }
    }
  }

  public void setOrderStatus(long orderId, @NotNull OrderObj.Status status) {
    for (int i = 0; i < orderList.size(); i++) {
      OrderObj orderObj = orderList.get(i);
      if (orderObj.getId() == orderId) {
        orderObj.setStatus(status);
        data[i][15] = status.getDesc();
        fireTableCellUpdated(i, 15);
        return;
      }
    }
  }

  public void setTicketHolderStatus(long ticketId, TicketObj.HolderStatus holderStatus) {
    for (OrderObj orderObj : orderList) {
      for (TicketObj ticketObj : orderObj.getTicketList()) {
        if (ticketObj.getId() == ticketId) {
          ticketObj.setHolderStatus(holderStatus);
          return;
        }
      }
    }
  }

  public void setTicketHolderStatus(Map<Long, TicketObj.HolderStatus> idHolderStatusMap) {
    for (OrderObj orderObj : orderList) {
      for (TicketObj ticketObj : orderObj.getTicketList()) {
        TicketObj.HolderStatus holderStatus = idHolderStatusMap.get(ticketObj.getId());
        if (holderStatus != null) {
          ticketObj.setHolderStatus(holderStatus);
        }
      }
    }
  }
}
