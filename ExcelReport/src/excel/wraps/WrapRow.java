package excel.wraps;

import java.math.BigDecimal;

import excel.enums.EStyle;
import excel.interfaces.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 25.11.2017
 */
public final class WrapRow {
  @NotNull
  private final XSSFRow row;
  @NotNull
  private final IStyle style;
  private int columnIndex;

  public WrapRow(@NotNull XSSFRow row, @NotNull IStyle style) {
    this.row = row;
    this.style = style;
  }

  @NotNull
  public WrapRow setHeightInPoints(float height) {
    row.setHeightInPoints(height);
    return this;
  }

  @NotNull
  public WrapRow createCell(@NotNull EStyle style) {
    return createCell(null, style);
  }

  /**
   * Создание ячейки идет всегда с последующим увеличение индекса столбца
   */
  @NotNull
  public WrapRow createCell(@Nullable Object value, @NotNull EStyle estyle) {
    if (value == null) createCell(estyle, CellType.STRING);
    else if (value instanceof Integer) createCell(estyle, CellType.NUMERIC).setCellValue((int)value);
    else if (value instanceof Long) createCell(estyle, CellType.NUMERIC).setCellValue((long)value);
    else if (value instanceof Double) createCell(estyle, CellType.NUMERIC).setCellValue((double)value);
    else if (value instanceof BigDecimal) createCell(estyle, CellType.NUMERIC).setCellValue(((BigDecimal)value).doubleValue());
    else if (value instanceof String) createCell(estyle, CellType.STRING).setCellValue((String)value);
    else if (value instanceof IFormula) createCellFormula(((IFormula)value).getFormula(), estyle);
    else throw new IllegalArgumentException("Неизвестный тип value");
    return this;
  }

  @NotNull
  public WrapRow createCellFormula(@NotNull String formula, @NotNull EStyle estyle) {
    createCell(estyle, CellType.FORMULA).setCellFormula(formula);
    return this;
  }

  @NotNull
  private XSSFCell createCell(@NotNull EStyle estyle, @NotNull CellType cellType) {
    XSSFCell cell = row.createCell(columnIndex++, cellType);
    cell.setCellStyle(style.getStyle(estyle));
    return cell;
  }
}
