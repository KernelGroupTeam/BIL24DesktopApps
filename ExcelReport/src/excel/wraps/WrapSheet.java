package excel.wraps;

import java.util.*;

import excel.enums.*;
import excel.interfaces.*;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.11.2017
 */
public final class WrapSheet implements IFont, IDataFormat, IStyle {
  private static final double PIXEL_K = 36.36d;
  @NotNull
  private final XSSFSheet sheet;
  @NotNull
  private final Map<EFont, XSSFFont> fontMap = new EnumMap<>(EFont.class);
  @NotNull
  private final Map<EDataFormat, Short> dataFormatMap = new EnumMap<>(EDataFormat.class);
  @NotNull
  private final Map<EStyle, XSSFCellStyle> styleMap = new EnumMap<>(EStyle.class);
  private int rowRememberIndex;
  private int rowCurrentIndex;

  public WrapSheet(@NotNull XSSFSheet sheet) {
    this.sheet = sheet;
  }

  public void addMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
    sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
  }

  public void autoSizeColumn(int column) {
    sheet.autoSizeColumn(column);
  }

  public void setColumnPixel(int columnIndex, int pixel) {
    setColumnWidth(columnIndex, (int)(PIXEL_K * pixel));
  }

  public void setColumnWidth(int columnIndex, int width) {
    sheet.setColumnWidth(columnIndex, width);
  }

  public void setOrientation(@NotNull PrintOrientation orientation) {
    sheet.getPrintSetup().setOrientation(orientation);
  }

  public void setMargin(short margin, double size) {
    sheet.setMargin(margin, size);
  }

  @NotNull
  public XSSFRow getRow(int rowIndex) {
    return sheet.getRow(rowIndex);
  }

  @NotNull
  @Override
  public XSSFCellStyle getStyle(@NotNull EStyle estyle) {
    XSSFCellStyle style = styleMap.get(estyle);
    if (style == null) styleMap.put(estyle, style = estyle.createStyle(sheet.getWorkbook(), this, this));
    return style;
  }

  @NotNull
  @Override
  public XSSFFont getFont(@NotNull EFont efont) {
    XSSFFont font = fontMap.get(efont);
    if (font == null) fontMap.put(efont, font = efont.createFont(sheet.getWorkbook()));
    return font;
  }

  @NotNull
  @Override
  public Short getDataFormat(@NotNull EDataFormat edataFormat) {
    Short dataFormat = dataFormatMap.get(edataFormat);
    if (dataFormat == null) dataFormatMap.put(edataFormat, dataFormat = edataFormat.createFormat(sheet.getWorkbook()));
    return dataFormat;
  }

  /**
   * Создание строки идет всегда с последующим увеличение индекса строки
   */
  @NotNull
  public WrapRow createRow() {
    return new WrapRow(sheet.createRow(rowCurrentIndex++), this);
  }

  public int getRowRememberIndex() {
    return rowRememberIndex;
  }

  public void setRowRememberIndex() {
    rowRememberIndex = rowCurrentIndex;
  }

  public int getRowCurrentIndex() {
    return rowCurrentIndex;
  }

  public void incRowCurrentIndex() {
    rowCurrentIndex++;
  }
}
