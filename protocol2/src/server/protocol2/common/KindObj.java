package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.15
 */
public class KindObj implements Serializable {
  private static final long serialVersionUID = 8838881544607726691L;
  private static final ConcurrentHashMap<Integer, KindObj> cache = new ConcurrentHashMap<>();
  private final int id;
  @NotNull
  private final String name;

  private KindObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static KindObj getInstance(int id, @NotNull String name) {
    KindObj result = cache.get(id);
    if (result != null) return result;
    KindObj kindObj = new KindObj(id, name);
    if (id < 0) return kindObj;
    result = cache.putIfAbsent(kindObj.getId(), kindObj);
    if (result == null) result = kindObj;
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
    if (!(o instanceof KindObj)) return false;
    KindObj that = (KindObj) o;
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
