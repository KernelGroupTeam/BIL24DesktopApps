package server.protocol2.cassa;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 09.11.2018
 */
public class OrderStatusObj implements Serializable {
  private static final long serialVersionUID = -7203142695170239059L;
  private static final ConcurrentHashMap<Integer, OrderStatusObj> cache = new ConcurrentHashMap<>();
  private final int id;
  @NotNull
  private final String name;

  private OrderStatusObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static OrderStatusObj getInstance(int id, @NotNull String name) {
    OrderStatusObj result = cache.get(id);
    if (result != null) return result;
    OrderStatusObj statusObj = new OrderStatusObj(id, name);
    result = cache.putIfAbsent(statusObj.getId(), statusObj);
    if (result == null) result = statusObj;
    return result;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderStatusObj)) return false;
    OrderStatusObj that = (OrderStatusObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return name;
  }
}
