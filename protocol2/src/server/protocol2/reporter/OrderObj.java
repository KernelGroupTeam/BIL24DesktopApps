package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.10.15
 */
public class OrderObj implements Serializable {
  private static final long serialVersionUID = 212121641597094345L;
  private static final DateFormat formatDDMMYYYYHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private long id;
  @NotNull
  private String date;//в формате дд.мм.гггг чч:мм:сс
  @NotNull
  private UserObj user;
  @NotNull
  private RAgent agent;
  @NotNull
  private RFrontend frontend;
  @NotNull
  private PaymentMethodObj paymentMethod = PaymentMethodObj.getUnknown();
  private boolean longReservation;
  @NotNull
  private String expiration = "";
  @Nullable
  private String processing;
  @NotNull
  private List<TicketObj> ticketList = Collections.emptyList();
  @NotNull
  private List<TicketObj> seatList = Collections.emptyList();//для неоплаченных заказов, когда ticketList пуст
  @NotNull
  private List<GatewayOrderObj> gatewayOrderList = Collections.emptyList();
  @NotNull
  private BigDecimal sum = BigDecimal.ZERO;
  @NotNull
  private BigDecimal filteredSum = BigDecimal.ZERO;
  @NotNull
  private BigDecimal discount = BigDecimal.ZERO;
  @NotNull
  private BigDecimal filteredDiscount = BigDecimal.ZERO;
  @NotNull
  private BigDecimal charge = BigDecimal.ZERO;
  @NotNull
  private BigDecimal filteredCharge = BigDecimal.ZERO;
  @NotNull
  private BigDecimal totalSum = BigDecimal.ZERO;
  @NotNull
  private BigDecimal filteredTotalSum = BigDecimal.ZERO;
  private int ticketQuantity;
  private int filteredTicketQuantity;
  @NotNull
  private Status status = Status.NEW;
  @NotNull
  private AcquiringObj acquiring = AcquiringObj.getNone();
  @NotNull
  private String paymentBankId = "";
  @NotNull
  private String paymentBankStatus = "";
  @NotNull
  private String paymentBankMessage = "";
  @Nullable
  private String email;
  @Nullable
  private Boolean emailSent;
  @Nullable
  private String phone;
  @Nullable
  private String fullName;

  public OrderObj(long id, @NotNull String date, @NotNull UserObj user, @NotNull RAgent agent, @NotNull RFrontend frontend) {
    this.id = id;
    this.date = date;
    this.user = user;
    this.agent = agent;
    this.frontend = frontend;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getDate() {
    return date;
  }

  @NotNull
  public UserObj getUser() {
    return user;
  }

  @NotNull
  public RAgent getAgent() {
    return agent;
  }

  @NotNull
  public RFrontend getFrontend() {
    return frontend;
  }

  @NotNull
  public PaymentMethodObj getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(@NotNull PaymentMethodObj paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public boolean isLongReservation() {
    return longReservation;
  }

  public void setLongReservation(boolean longReservation) {
    this.longReservation = longReservation;
  }

  @NotNull
  public String getExpiration() {
    return expiration;
  }

  public void setExpiration(@NotNull String expiration) {
    this.expiration = expiration;
  }

  @Nullable
  public String getProcessing() {
    return processing;
  }

  public void setProcessing(@Nullable String processing) {
    this.processing = processing;
  }

  @NotNull
  public List<TicketObj> getTicketList() {
    return ticketList;
  }

  public void setTicketList(@NotNull List<TicketObj> ticketList) {
    this.ticketList = ticketList;
  }

  @NotNull
  public List<TicketObj> getSeatList() {
    return seatList;
  }

  public void setSeatList(@NotNull List<TicketObj> seatList) {
    this.seatList = seatList;
  }

  @NotNull
  public List<GatewayOrderObj> getGatewayOrderList() {
    return gatewayOrderList;
  }

  public void setGatewayOrderList(@NotNull List<GatewayOrderObj> gatewayOrderList) {
    this.gatewayOrderList = gatewayOrderList;
  }

  @NotNull
  public BigDecimal getSum() {
    return sum;
  }

  public void setSum(@NotNull BigDecimal sum) {
    this.sum = sum;
  }

  @NotNull
  public BigDecimal getFilteredSum() {
    return filteredSum;
  }

  public void setFilteredSum(@NotNull BigDecimal filteredSum) {
    this.filteredSum = filteredSum;
  }

  @NotNull
  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(@NotNull BigDecimal discount) {
    this.discount = discount;
  }

  @NotNull
  public BigDecimal getFilteredDiscount() {
    return filteredDiscount;
  }

  public void setFilteredDiscount(@NotNull BigDecimal filteredDiscount) {
    this.filteredDiscount = filteredDiscount;
  }

  @NotNull
  public BigDecimal getCharge() {
    return charge;
  }

  public void setCharge(@NotNull BigDecimal charge) {
    this.charge = charge;
  }

  @NotNull
  public BigDecimal getFilteredCharge() {
    return filteredCharge;
  }

  public void setFilteredCharge(@NotNull BigDecimal filteredCharge) {
    this.filteredCharge = filteredCharge;
  }

  @NotNull
  public BigDecimal getTotalSum() {
    return totalSum;
  }

  public void setTotalSum(@NotNull BigDecimal totalSum) {
    this.totalSum = totalSum;
  }

  @NotNull
  public BigDecimal getFilteredTotalSum() {
    return filteredTotalSum;
  }

  public void setFilteredTotalSum(@NotNull BigDecimal filteredTotalSum) {
    this.filteredTotalSum = filteredTotalSum;
  }

  public int getTicketQuantity() {
    return ticketQuantity;
  }

  public void setTicketQuantity(int ticketQuantity) {
    this.ticketQuantity = ticketQuantity;
  }

  public int getFilteredTicketQuantity() {
    return filteredTicketQuantity;
  }

  public void setFilteredTicketQuantity(int filteredTicketQuantity) {
    this.filteredTicketQuantity = filteredTicketQuantity;
  }

  @NotNull
  public Status getStatus() {
    return status;
  }

  public void setStatus(@NotNull Status status) {
    this.status = status;
  }

  @NotNull
  public AcquiringObj getAcquiring() {
    return acquiring;
  }

  public void setAcquiring(@NotNull AcquiringObj acquiring) {
    this.acquiring = acquiring;
  }

  @NotNull
  public String getPaymentBankId() {
    return paymentBankId;
  }

  public void setPaymentBankId(@NotNull String paymentBankId) {
    this.paymentBankId = paymentBankId;
  }

  @NotNull
  public String getPaymentBankStatus() {
    return paymentBankStatus;
  }

  public void setPaymentBankStatus(@NotNull String paymentBankStatus) {
    this.paymentBankStatus = paymentBankStatus;
  }

  @NotNull
  public String getPaymentBankMessage() {
    return paymentBankMessage;
  }

  public void setPaymentBankMessage(@NotNull String paymentBankMessage) {
    this.paymentBankMessage = paymentBankMessage;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  @Nullable
  public Boolean getEmailSent() {
    return emailSent;
  }

  public void setEmailSent(@Nullable Boolean emailSent) {
    this.emailSent = emailSent;
  }

  @Nullable
  public String getPhone() {
    return phone;
  }

  public void setPhone(@Nullable String phone) {
    this.phone = phone;
  }

  @Nullable
  public String getFullName() {
    return fullName;
  }

  public void setFullName(@Nullable String fullName) {
    this.fullName = fullName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderObj)) return false;
    OrderObj orderObj = (OrderObj) o;
    return id == orderObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmmss.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmmss.parse(date);
  }

  public enum Status {
    NEW(0, "Новый"),
    PROCESSING(1, "Обрабатывается"),
    PROCESSING_GATEWAY(4, "Обрабатывается ВБС"),
    PAID(2, "Оплачен"),
    CANCELLING_GATEWAY(5, "Отменяется ВБС"),
    CANCELLED(3, "Отменен");
    private static final Status[] en;

    static {
      en = new Status[]{NEW, PROCESSING, PAID, CANCELLED, PROCESSING_GATEWAY, CANCELLING_GATEWAY};
      for (int i = 0; i < en.length; i++) {
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<Status> enList = Arrays.asList(en);
      for (Status value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    Status(int id, @NotNull String desc) {
      this.id = id;
      this.desc = desc;
    }

    public int getId() {
      return id;
    }

    @NotNull
    public String getDesc() {
      return desc;
    }

    @Override
    public String toString() {
      return desc;
    }

    @NotNull
    public static Status getStatus(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
