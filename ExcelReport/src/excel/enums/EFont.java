package excel.enums;

import excel.wraps.WrapFont;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.07.2017
 */
public enum EFont {
  CALIBRI_11_NORMAL() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)11)
          .get();
    }
  },
  CALIBRI_11_BOLD() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)11)
          .setBold()
          .get();
    }
  },
  CALIBRI_7_NORMAL() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)7)
          .get();
    }
  },
  CALIBRI_7_BOLD() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)7)
          .setBold()
          .get();
    }
  },
  CALIBRI_8_NORMAL() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)8)
          .get();
    }
  },
  CALIBRI_8_BOLD() {
    @NotNull
    @Override
    public XSSFFont createFont(@NotNull XSSFWorkbook book) {
      return new WrapFont(book)
          .setName("Calibri")
          .setHeight((short)8)
          .setBold()
          .get();
    }
  },
  ;

  @NotNull
  public abstract XSSFFont createFont(@NotNull XSSFWorkbook book);
}
