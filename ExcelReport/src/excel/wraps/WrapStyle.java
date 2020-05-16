package excel.wraps;

import java.awt.Color;

import excel.enums.EColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 08.07.2016.
 */
public final class WrapStyle {
  @NotNull
  private final XSSFCellStyle cellStyle;

  public WrapStyle(@NotNull XSSFWorkbook book) {
    this.cellStyle = book.createCellStyle();
  }

  @NotNull
  public XSSFCellStyle get() {
    return cellStyle;
  }

  @NotNull
  public WrapStyle setColor(@NotNull Color color) {
    return setColor(new XSSFColor(color));
  }

  @NotNull
  private WrapStyle setColor(@NotNull XSSFColor color) {
    cellStyle.setFillForegroundColor(color);
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return this;
  }

  @NotNull
  public WrapStyle setColor(@NotNull EColor color) {
    return setColor(color.getColor());
  }

  @NotNull
  public WrapStyle setFont(@NotNull XSSFFont font) {
    cellStyle.setFont(font);
    return this;
  }

  @NotNull
  private WrapStyle setBorderLeft(@NotNull BorderStyle border) {
    cellStyle.setBorderLeft(border);
    return this;
  }

  @NotNull
  private WrapStyle setBorderTop(@NotNull BorderStyle border) {
    cellStyle.setBorderTop(border);
    return this;
  }

  @NotNull
  private WrapStyle setBorderRight(@NotNull BorderStyle border) {
    cellStyle.setBorderRight(border);
    return this;
  }

  @NotNull
  private WrapStyle setBorderBottom(@NotNull BorderStyle border) {
    cellStyle.setBorderBottom(border);
    return this;
  }

  @NotNull
  public WrapStyle setBorder(@NotNull BorderStyle border) {
    return setBorderLeft(border).setBorderTop(border).setBorderRight(border).setBorderBottom(border);
  }

  @NotNull
  public WrapStyle setHorizontalAlignment(@NotNull HorizontalAlignment alignment) {
    cellStyle.setAlignment(alignment);
    return this;
  }

  @NotNull
  public WrapStyle setVerticalAlignment(@NotNull VerticalAlignment alignment) {
    cellStyle.setVerticalAlignment(alignment);
    return this;
  }

  @NotNull
  public WrapStyle setDataFormat(short format) {
    cellStyle.setDataFormat(format);
    return this;
  }

  @NotNull
  public WrapStyle setWrapText(boolean wrapped) {
    cellStyle.setWrapText(wrapped);
    return this;
  }
}
