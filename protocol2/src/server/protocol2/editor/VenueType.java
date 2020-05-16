package server.protocol2.editor;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.07.17
 */
public class VenueType implements Serializable {
  private static final long serialVersionUID = -7388378664250902427L;
  private static final ConcurrentHashMap<Integer, VenueType> cache = new ConcurrentHashMap<>();
  private static final VenueType UNKNOWN = new VenueType(0, "Неопределенный");
  private final int id;
  @NotNull
  private final String name;

  private VenueType(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static VenueType getInstance(int id, @NotNull String name) {
    VenueType result = cache.get(id);
    if (result != null) return result;
    VenueType venueType = new VenueType(id, name);
    result = cache.putIfAbsent(venueType.getId(), venueType);
    if (result == null) result = venueType;
    return result;
  }

  public static VenueType getUnknown() {
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
    if (!(o instanceof VenueType)) return false;
    VenueType that = (VenueType) o;
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
