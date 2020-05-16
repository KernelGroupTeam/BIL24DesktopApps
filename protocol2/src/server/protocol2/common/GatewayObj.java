package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.15
 */
public class GatewayObj implements Serializable {
  private static final long serialVersionUID = 4116015592412182102L;
  private static final ConcurrentHashMap<Integer, GatewayObj> cache = new ConcurrentHashMap<>();
  private static final GatewayObj NONE = new GatewayObj(0, 0, "Нет", null, null);

  static {
    cache.put(NONE.getId(), NONE);
  }

  private final int id;
  private final int systemId;
  @NotNull
  private final String name;
  @Nullable
  private final Long organizerId;
  @Nullable
  private final String organizerName;

  private GatewayObj(int id, int systemId, @NotNull String name, @Nullable Long organizerId, @Nullable String organizerName) {
    this.id = id;
    this.systemId = systemId;
    this.name = name;
    this.organizerId = organizerId;
    this.organizerName = organizerName;
  }

  @NotNull
  public static GatewayObj getInstance(int id, int systemId, @NotNull String name, @Nullable Long organizerId, @Nullable String organizerName) {
    GatewayObj result = cache.get(id);
    if (result != null) return result;
    GatewayObj gatewayObj = new GatewayObj(id, systemId, name, organizerId, organizerName);
    if (id < 0) return gatewayObj;
    result = cache.putIfAbsent(gatewayObj.getId(), gatewayObj);
    if (result == null) result = gatewayObj;
    return result;
  }

  @NotNull
  public static GatewayObj getNone() {
    return NONE;
  }

  public int getId() {
    return id;
  }

  public int getSystemId() {
    return systemId;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Nullable
  public Long getOrganizerId() {
    return organizerId;
  }

  @Nullable
  public String getOrganizerName() {
    return organizerName;
  }

  @NotNull
  public String getNameWithOrganizer() {
    if (organizerName == null) return name;
    return name + " (" + organizerName + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GatewayObj)) return false;
    GatewayObj that = (GatewayObj) o;
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
