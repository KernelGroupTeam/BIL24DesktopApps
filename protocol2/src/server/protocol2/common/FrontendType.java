package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.15
 */
public class FrontendType implements Serializable {
  private static final long serialVersionUID = -3732135168640298344L;
  private static final ConcurrentHashMap<Integer, FrontendType> cache = new ConcurrentHashMap<>();
  private final int id;
  @NotNull
  private final String name;

  private FrontendType(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static FrontendType getInstance(int id, @NotNull String name) {
    FrontendType result = cache.get(id);
    if (result != null) return result;
    FrontendType frontendType = new FrontendType(id, name);
    if (id <= 0) return frontendType;
    result = cache.putIfAbsent(frontendType.getId(), frontendType);
    if (result == null) result = frontendType;
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
    if (!(o instanceof FrontendType)) return false;
    FrontendType that = (FrontendType) o;
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
