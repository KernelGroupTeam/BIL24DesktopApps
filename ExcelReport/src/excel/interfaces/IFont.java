package excel.interfaces;

import excel.enums.EFont;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.11.2017
 */
public interface IFont {
  @NotNull
  XSSFFont getFont(@NotNull EFont efont);
}
