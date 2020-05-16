package server.protocol2.manager;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.08.19
 */
public class VatValue implements Serializable {
  private static final long serialVersionUID = -4750242647661870391L;
  private static final ConcurrentHashMap<Integer, VatValue> cache = new ConcurrentHashMap<>();
  private static final VatValue UNKNOWN = new VatValue(0, "Не задан");

  static {
    cache.put(UNKNOWN.getId(), UNKNOWN);
  }

  private final int id;
  @NotNull
  private final String name;

  private VatValue(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static VatValue getInstance(int id, @NotNull String name) {
    VatValue result = cache.get(id);
    if (result != null) return result;
    VatValue vatValue = new VatValue(id, name);
    result = cache.putIfAbsent(vatValue.getId(), vatValue);
    if (result == null) result = vatValue;
    return result;
  }

  @NotNull
  public static VatValue getUnknown() {
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
    if (!(o instanceof VatValue)) return false;
    VatValue that = (VatValue) o;
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
