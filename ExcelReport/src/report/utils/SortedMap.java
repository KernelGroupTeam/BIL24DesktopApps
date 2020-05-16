package report.utils;

import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 10.11.2017
 * Такого рода карта нужна только если ключ объект не примитива
 * Для оберток примитивов использовать обычный TreeMap
 */
public final class SortedMap<K, V> extends HashMap<K, V> {
  @NotNull
  private final Comparator<Map.Entry<K, ?>> comparator;
  @Nullable
  private List<Map.Entry<K, V>> entrySorted;

  public SortedMap(@NotNull Comparator<Map.Entry<K, ?>> comparator) {
    this.comparator = comparator;
  }

  @NotNull
  public List<Map.Entry<K, V>> entrySorted() {
    if (entrySorted == null) {
      entrySorted = new ArrayList<>(super.entrySet());
      Collections.sort(entrySorted, comparator);
    }
    return entrySorted;
  }

  @NotNull
  @Deprecated
  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException("Операция запрещена. Использовать entrySorted()");
  }
}
