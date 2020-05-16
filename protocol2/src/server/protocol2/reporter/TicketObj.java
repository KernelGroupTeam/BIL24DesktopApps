package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.BarcodeFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.10.15
 */
public class TicketObj implements Serializable {
  private static final long serialVersionUID = -1665613445423786508L;
  private static final DateFormat formatDDMMYYYYHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private static final ActionEventObj DEF_ACTION_EVENT = new ActionEventObj(0);
  private long id;
  private long seatId;
  private long orderId;
  @Nullable
  private SeatLocationObj seatLocation = null;
  @NotNull
  private String category = "";
  @NotNull
  private BigDecimal price = BigDecimal.ZERO;
  @NotNull
  private BigDecimal discount = BigDecimal.ZERO;
  @NotNull
  private BigDecimal charge = BigDecimal.ZERO;
  @NotNull
  private BigDecimal totalPrice = BigDecimal.ZERO;
  @Nullable
  private String discountReason;
  @NotNull
  private String barcode = "";
  @NotNull
  private BarcodeFormat barcodeFormat = BarcodeFormat.getNone();
  @NotNull
  private ActionEventObj actionEvent = DEF_ACTION_EVENT;
  @NotNull
  private HolderStatus holderStatus = HolderStatus.NEVER_USE;
  @Nullable
  private String refundDate;//в формате дд.мм.гггг чч:мм:сс

  public TicketObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public long getSeatId() {
    return seatId;
  }

  public void setSeatId(long seatId) {
    this.seatId = seatId;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  @Nullable
  public SeatLocationObj getSeatLocation() {
    return seatLocation;
  }

  public void setSeatLocation(@Nullable SeatLocationObj seatLocation) {
    this.seatLocation = seatLocation;
  }

  @NotNull
  public String getCategory() {
    return category;
  }

  public void setCategory(@NotNull String category) {
    this.category = category;
  }

  @NotNull
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(@NotNull BigDecimal price) {
    this.price = price;
  }

  @NotNull
  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(@NotNull BigDecimal discount) {
    this.discount = discount;
  }

  @NotNull
  public BigDecimal getCharge() {
    return charge;
  }

  public void setCharge(@NotNull BigDecimal charge) {
    this.charge = charge;
  }

  @NotNull
  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(@NotNull BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  @Nullable
  public String getDiscountReason() {
    return discountReason;
  }

  public void setDiscountReason(@Nullable String discountReason) {
    this.discountReason = discountReason;
  }

  @NotNull
  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(@NotNull String barcode) {
    this.barcode = barcode;
  }

  @NotNull
  public BarcodeFormat getBarcodeFormat() {
    return barcodeFormat;
  }

  public void setBarcodeFormat(@NotNull BarcodeFormat barcodeFormat) {
    this.barcodeFormat = barcodeFormat;
  }

  @NotNull
  public ActionEventObj getActionEvent() {
    return actionEvent;
  }

  public void setActionEvent(@NotNull ActionEventObj actionEvent) {
    this.actionEvent = actionEvent;
  }

  @NotNull
  public HolderStatus getHolderStatus() {
    return holderStatus;
  }

  public void setHolderStatus(@NotNull HolderStatus holderStatus) {
    this.holderStatus = holderStatus;
  }

  @Nullable
  public String getRefundDate() {
    return refundDate;
  }

  public void setRefundDate(@Nullable String refundDate) {
    this.refundDate = refundDate;
  }

  @NotNull
  public String[] createExportData() {
    String[] result = new String[9];
    if (seatLocation != null) {
      result[0] = seatLocation.getSector();
      result[1] = seatLocation.getRow();
      result[2] = seatLocation.getNumber();
    } else {
      result[3] = category;
    }
    result[4] = price.toPlainString();
    result[5] = barcode;
    result[6] = barcodeFormat.getName();
    result[7] = actionEvent.getShowTime();//в формате дд.мм.гггг чч:мм
    result[8] = actionEvent.getActionName();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TicketObj)) return false;
    TicketObj ticketObj = (TicketObj) o;
    return id == ticketObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmmss.parse(date);
  }

  public enum HolderStatus {
    NEVER_USE(0, "Не использовал"), CHECK_IN(1, "Вошел"), CHECK_OUT(2, "Вышел"), REFUND_GATEWAY(4, "Возврат билета в ВБС"), REFUND(3, "Вернул билет"), CHECK_IN_BY_CONTROLLER(5, "Впустил контроллер");
    private static final HolderStatus[] en;

    static {
      en = new HolderStatus[]{NEVER_USE, CHECK_IN, CHECK_OUT, REFUND, REFUND_GATEWAY, CHECK_IN_BY_CONTROLLER};
      for (int i = 0; i < en.length; i++) {
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<HolderStatus> enList = Arrays.asList(en);
      for (HolderStatus value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    HolderStatus(int id, @NotNull String desc) {
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
    public static HolderStatus getHolderStatus(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
