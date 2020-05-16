package excel.wraps;

import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 08.07.2016.
 */
public final class WrapFormat {
  @NotNull
  private final XSSFDataFormat format;

  public WrapFormat(@NotNull XSSFWorkbook book) {
    format = book.createDataFormat();
  }

  public short getFormat(int index) {
    return format.getFormat(format.getFormat((short) index));
  }
}
