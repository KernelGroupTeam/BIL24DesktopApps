package server.protocol2.cassa;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 09.11.2018
 */
public class TicketStatusObj implements Serializable {
  private static final long serialVersionUID = -1271483173185458367L;
  private static final ConcurrentHashMap<Integer, TicketStatusObj> cache = new ConcurrentHashMap<>();
  private final int id;
  @NotNull
  private final String name;

  private TicketStatusObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static TicketStatusObj getInstance(int id, @NotNull String name) {
    TicketStatusObj result = cache.get(id);
    if (result != null) return result;
    TicketStatusObj statusObj = new TicketStatusObj(id, name);
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
    if (!(o instanceof TicketStatusObj)) return false;
    TicketStatusObj that = (TicketStatusObj) o;
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
