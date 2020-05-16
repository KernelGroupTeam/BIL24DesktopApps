package excel.enums;

import excel.wraps.WrapFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.07.2017
 */
public enum EDataFormat {
  MONEY() {
    @NotNull
    @Override
    public Short createFormat(@NotNull XSSFWorkbook book) {
      return new WrapFormat(book).getFormat(3);
    }
  },
  COUNT() {
    @NotNull
    @Override
    public Short createFormat(@NotNull XSSFWorkbook book) {
      return new WrapFormat(book).getFormat(4);
    }
  },
  ;

  @NotNull
  public abstract Short createFormat(@NotNull XSSFWorkbook book);
}
