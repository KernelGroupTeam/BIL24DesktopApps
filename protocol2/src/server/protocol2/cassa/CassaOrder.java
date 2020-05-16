package server.protocol2.cassa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.PaymentMethodObj;

/**
 * Created by Inventor on 05.11.2018
 */
public class CassaOrder implements Serializable {
  private static final long serialVersionUID = 3850723870197812820L;
  @NotNull
  private static final DateFormat formatDDMMYYYYHHMM = new SimpleDateFormat("dd.MM.yyyy HH:mm");//Формат даты заказа
  private long id;//Идентификатор заказа
  @NotNull
  private String date;//Дата заказа
  @NotNull
  private BigDecimal price;//Номинальная стоимость заказа
  @NotNull
  private BigDecimal discount;//Скидка заказа
  @NotNull
  private BigDecimal serviceCharge;//Сервисный сбор заказа
  @NotNull
  private OrderStatusObj status;//Статус заказа
  @NotNull
  private PaymentMethodObj paymentMethod;//Способ оплаты заказа
  @Nullable
  private String fullName;//Полное имя покупателя
  private int orderTimeout;//Время жизни заказа(ожидание оплаты) в минутах
  @NotNull
  private List<CassaSeatOrder> seatList;//Список мест. Пустой если есть билеты
  @NotNull
  private List<CassaTicket> ticketList;//Список билетов

  public CassaOrder(long id, long date, @NotNull BigDecimal price, @NotNull BigDecimal discount,
                    @NotNull BigDecimal serviceCharge, @NotNull OrderStatusObj status, @NotNull PaymentMethodObj paymentMethod,
                    @Nullable String fullName, int orderTimeout, @NotNull List<CassaSeatOrder> seatList, @NotNull List<CassaTicket> ticketList) {
    this.id = id;
    this.date = formatDate(date);
    this.price = price;
    this.discount = discount;
    this.serviceCharge = serviceCharge;
    this.status = status;
    this.paymentMethod = paymentMethod;
    this.fullName = fullName;
    this.orderTimeout = orderTimeout;
    this.seatList = seatList;
    this.ticketList = ticketList;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getDate() {
    return date;
  }

  @NotNull
  public BigDecimal getPrice() {
    return price;
  }

  @NotNull
  public BigDecimal getDiscount() {
    return discount;
  }

  @NotNull
  public BigDecimal getServiceCharge() {
    return serviceCharge;
  }

  @NotNull
  public BigDecimal getTotalSum() {
    return price.subtract(discount).add(serviceCharge);
  }

  @NotNull
  public OrderStatusObj getStatus() {
    return status;
  }

  @NotNull
  public PaymentMethodObj getPaymentMethod() {
    return paymentMethod;
  }

  @Nullable
  public String getFullName() {
    return fullName;
  }

  public int getOrderTimeout() {
    return orderTimeout;
  }

  @NotNull
  public List<CassaSeatOrder> getSeatList() {
    return seatList;
  }

  @NotNull
  public List<CassaTicket> getTicketList() {
    return ticketList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CassaOrder that = (CassaOrder) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaOrder{id=" + id + ", date=" + date + ", price=" + price + ", discount=" + discount + ", serviceCharge=" + serviceCharge +
        ", status=" + status + ", paymentMethod=" + paymentMethod + ", fullName=" + fullName + ", orderTimeout=" + orderTimeout +
        ", seatList=" + seatList.size() + ", ticketList=" + ticketList.size() + '}';
  }

  @NotNull
  private static synchronized String formatDate(long date) {
    return formatDDMMYYYYHHMM.format(new Date(date));
  }

  @NotNull
  public static synchronized Date parseDate(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHMM.parse(date);
  }
}
