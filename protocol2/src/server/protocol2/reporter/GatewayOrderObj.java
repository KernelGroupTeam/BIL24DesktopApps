package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import server.protocol2.common.GatewayObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.01.16
 */
public class GatewayOrderObj implements Serializable {
  private static final long serialVersionUID = 4909889604637791972L;
  @NotNull
  private GatewayObj gateway;
  @NotNull
  private Object orderId;

  public GatewayOrderObj(@NotNull GatewayObj gateway, @NotNull Object orderId) {
    this.gateway = gateway;
    this.orderId = orderId;
  }

  @NotNull
  public GatewayObj getGateway() {
    return gateway;
  }

  @NotNull
  public Object getOrderId() {
    return orderId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GatewayOrderObj)) return false;
    GatewayOrderObj that = (GatewayOrderObj) o;
    return gateway.equals(that.gateway) && orderId.equals(that.orderId);
  }

  @Override
  public int hashCode() {
    int result = gateway.hashCode();
    result = 31 * result + orderId.hashCode();
    return result;
  }
}
