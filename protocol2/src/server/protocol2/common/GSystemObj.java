package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.02.20
 */
public class GSystemObj implements Serializable {
  private static final long serialVersionUID = 4672664654752740785L;
  private static final ConcurrentHashMap<Integer, GSystemObj> cache = new ConcurrentHashMap<>();
  private static final GSystemObj NONE = new GSystemObj(0, "Нет");

  static {
    cache.put(NONE.getId(), NONE);
  }

  private final int id;
  @NotNull
  private final String name;

  private GSystemObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static GSystemObj getInstance(int id, @NotNull String name) {
    GSystemObj result = cache.get(id);
    if (result != null) return result;
    GSystemObj systemObj = new GSystemObj(id, name);
    if (id < 0) return systemObj;
    result = cache.putIfAbsent(systemObj.getId(), systemObj);
    if (result == null) result = systemObj;
    return result;
  }

  @NotNull
  public static GSystemObj getNone() {
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
    if (!(o instanceof GSystemObj)) return false;
    GSystemObj that = (GSystemObj) o;
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
