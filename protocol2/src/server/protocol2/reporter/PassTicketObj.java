package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.07.17
 */
public class PassTicketObj implements Serializable {
  private static final long serialVersionUID = -7171857992655499488L;
  private static final UserObj DEF_USER = new UserObj(0);
  private long id;
  private long seatId;
  private long orderId;
  @Nullable
  private SeatLocationObj seatLocation = null;
  @NotNull
  private String category = "";
  @NotNull
  private UserObj user = DEF_USER;
  @Nullable
  private String email;//e-mail доставки (из ордера)

  public PassTicketObj(long id) {
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
  public UserObj getUser() {
    return user;
  }

  public void setUser(@NotNull UserObj user) {
    this.user = user;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PassTicketObj)) return false;
    PassTicketObj ticketObj = (PassTicketObj) o;
    return id == ticketObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
