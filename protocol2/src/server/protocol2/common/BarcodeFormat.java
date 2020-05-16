package server.protocol2.common;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.11.16
 */
public class BarcodeFormat implements Serializable {
  private static final long serialVersionUID = -8283667849885466337L;
  private static final ConcurrentHashMap<Integer, BarcodeFormat> cache = new ConcurrentHashMap<>();
  private static final BarcodeFormat NONE = new BarcodeFormat(-1, "Нет");
  private final int id;
  @NotNull
  private final String name;

  private BarcodeFormat(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static BarcodeFormat getInstance(int id, @NotNull String name) {
    BarcodeFormat result = cache.get(id);
    if (result != null) return result;
    BarcodeFormat barcodeFormat = new BarcodeFormat(id, name);
    result = cache.putIfAbsent(barcodeFormat.getId(), barcodeFormat);
    if (result == null) result = barcodeFormat;
    return result;
  }

  @NotNull
  public static BarcodeFormat getNone() {
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
    if (!(o instanceof BarcodeFormat)) return false;
    BarcodeFormat that = (BarcodeFormat) o;
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
