package excel.interfaces;

import excel.enums.EStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 25.11.2017
 */
public interface IStyle {
  @NotNull
  XSSFCellStyle getStyle(@NotNull EStyle estyle);
}
