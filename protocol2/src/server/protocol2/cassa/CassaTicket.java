package server.protocol2.cassa;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 05.11.2018
 */
public class CassaTicket implements Serializable {
  private static final long serialVersionUID = 3870933286726713384L;
  private long id;//Идентификатор билета
  @NotNull
  private CassaSeatOrder seat;//Место билета
  @NotNull
  private TicketStatusObj status;//Статус билета
  @Nullable
  private String qrCodeImg;//QR код картинкой билета
  @Nullable
  private String barCodeImg;//Баркод(штрихкод) картинкой билета
  @NotNull
  private String barCodeNumber;//Баркод(штрихкод) билета

  public CassaTicket(long id, @NotNull CassaSeatOrder seat, @NotNull TicketStatusObj status, @Nullable String qrCodeImg,
                     @Nullable String barCodeImg, @NotNull String barCodeNumber) {
    this.id = id;
    this.seat = seat;
    this.status = status;
    this.qrCodeImg = qrCodeImg;
    this.barCodeImg = barCodeImg;
    this.barCodeNumber = barCodeNumber;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public CassaSeatOrder getSeat() {
    return seat;
  }

  @NotNull
  public BigDecimal getPrice() {
    return seat.getCategoryPrice().getPrice();
  }

  @NotNull
  public BigDecimal getTotalSum() {
    return getPrice().subtract(seat.getDiscount()).add(seat.getServiceCharge());
  }

  @NotNull
  public TicketStatusObj getStatus() {
    return status;
  }

  @Nullable
  public String getQrCodeImg() {
    return qrCodeImg;
  }

  @Nullable
  public String getBarCodeImg() {
    return barCodeImg;
  }

  @NotNull
  public String getBarCodeNumber() {
    return barCodeNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CassaTicket that = (CassaTicket) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaTicket{" + "id=" + id + ", seat=" + seat + ", status=" + status + ", qrCodeImg=" + qrCodeImg +
        ", barCodeImg=" + barCodeImg + ", barCodeNumber=" + barCodeNumber + '}';
  }
}
