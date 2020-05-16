package report.models;

import java.util.*;

import excel.enums.EStyle;
import excel.wraps.WrapSheet;
import org.jetbrains.annotations.*;
import report.enums.EHeader;

/**
 * Created by Inventor on 27.11.2017
 */
public final class Header {
  @NotNull
  @SuppressWarnings("MapReplaceableByEnumMap")
  private final Map<EHeader, String> valueMap = new LinkedHashMap<>();
  @NotNull
  private final WrapSheet sheet;

  public Header(@NotNull WrapSheet sheet) {
    this.sheet = sheet;
  }

  @NotNull
  public Header add(@NotNull EHeader key, boolean value) {
    return add(key, value ? "Да" : "Нет");
  }

  @NotNull
  public Header add(@NotNull EHeader key, @Nullable String value) {
    valueMap.put(key, value);
    return this;
  }

  @Nullable
  public String remove(@NotNull EHeader key) {
    return valueMap.remove(key);
  }

  public void build() {
    for (Map.Entry<EHeader, String> entry : valueMap.entrySet()) {
      EHeader header = entry.getKey();
      String value = entry.getValue();
      if (header == EHeader.NAME) sheet.createRow().createCell(value, EStyle.BOLD);
      else sheet.createRow().createCell(header.getDesc() + value, EStyle.NORMAL);
    }

    sheet.incRowCurrentIndex();
    sheet.incRowCurrentIndex();
  }
}
