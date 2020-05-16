package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.11.17
 */
public class AcquiringObj implements Serializable {
  private static final long serialVersionUID = -1301453575796981969L;
  private static final ConcurrentHashMap<Integer, AcquiringObj> cache = new ConcurrentHashMap<>();
  private static final AcquiringObj NONE = new AcquiringObj(0, "Нет");

  static {
    cache.put(NONE.getId(), NONE);
  }

  private final int id;
  @NotNull
  private final String name;

  private AcquiringObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static AcquiringObj getInstance(int id, @NotNull String name) {
    AcquiringObj result = cache.get(id);
    if (result != null) return result;
    AcquiringObj acquiringObj = new AcquiringObj(id, name);
    if (id < 0) return acquiringObj;
    result = cache.putIfAbsent(acquiringObj.getId(), acquiringObj);
    if (result == null) result = acquiringObj;
    return result;
  }

  @NotNull
  public static AcquiringObj getNone() {
    return NONE;
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
    if (!(o instanceof AcquiringObj)) return false;
    AcquiringObj that = (AcquiringObj) o;
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
