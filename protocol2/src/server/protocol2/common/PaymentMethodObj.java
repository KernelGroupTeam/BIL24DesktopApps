package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 09.11.2018
 */
public class PaymentMethodObj implements Serializable {
  private static final long serialVersionUID = -8995139600363947402L;
  private static final ConcurrentHashMap<Integer, PaymentMethodObj> cache = new ConcurrentHashMap<>();
  private static final PaymentMethodObj UNKNOWN = new PaymentMethodObj(0, "Неизвестно");

  static {
    cache.put(UNKNOWN.getId(), UNKNOWN);
  }

  private final int id;
  @NotNull
  private final String name;

  private PaymentMethodObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static PaymentMethodObj getInstance(int id, @NotNull String name) {
    PaymentMethodObj result = cache.get(id);
    if (result != null) return result;
    PaymentMethodObj methodObj = new PaymentMethodObj(id, name);
    result = cache.putIfAbsent(methodObj.getId(), methodObj);
    if (result == null) result = methodObj;
    return result;
  }

  @NotNull
  public static PaymentMethodObj getUnknown() {
    return UNKNOWN;
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
    if (!(o instanceof PaymentMethodObj)) return false;
    PaymentMethodObj that = (PaymentMethodObj) o;
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
