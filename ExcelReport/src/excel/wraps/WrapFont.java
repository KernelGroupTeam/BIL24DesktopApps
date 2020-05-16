package excel.wraps;

import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 08.07.2016.
 */
public final class WrapFont {
  @NotNull
  private final XSSFFont font;

  public WrapFont(@NotNull XSSFWorkbook book) {
    this.font = book.createFont();
  }

  @NotNull
  public XSSFFont get() {
    return font;
  }

  @NotNull
  public WrapFont setName(@NotNull String name) {
    font.setFontName(name);
    return this;
  }

  @NotNull
  public WrapFont setHeight(short height) {
    font.setFontHeightInPoints(height);
    return this;
  }

  @NotNull
  public WrapFont setBold() {
    font.setBold(true);
    return this;
  }
}
