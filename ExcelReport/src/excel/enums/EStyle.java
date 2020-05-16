package excel.enums;

import excel.interfaces.*;
import excel.wraps.WrapStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.07.2017
 */
public enum EStyle {
  EMPTY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  NORMAL() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .get();
    }
  },
  BOLD() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .get();
    }
  },
  NORMAL_JUSTIFY_TOP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.JUSTIFY)
          .setVerticalAlignment(VerticalAlignment.TOP)
          .get();
    }
  },
  NORMAL_JUSTIFY_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.JUSTIFY)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_CENTER_CENTER_WRAP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setWrapText(true)
          .get();
    }
  },
  BOLD_242_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .get();
    }
  },
  BOLD_242_LEFT_TOP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.LEFT)
          .setVerticalAlignment(VerticalAlignment.TOP)
          .get();
    }
  },
  BOLD_242_LEFT_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.LEFT)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  NORMAL_242() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  NORMAL_242_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  NORMAL_242_COUNT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.COUNT))
          .get();
    }
  },
  NORMAL_242_LEFT_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.LEFT)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_242_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_242_JUSTIFY_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.JUSTIFY)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_242_RIGHT_CENTER_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  NORMAL_216() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  NORMAL_216_COUNT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.COUNT))
          .get();
    }
  },
  NORMAL_216_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  NORMAL_216_RIGHT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .get();
    }
  },
  NORMAL_216_RIGHT_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  BOLD_216() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  BOLD_216_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  BOLD_216_COUNT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.COUNT))
          .get();
    }
  },
  BOLD_216_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .get();
    }
  },
  BOLD_216_RIGHT_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  NORMAL_191_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  NORMAL_191_CENTER_CENTER_WRAP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setWrapText(true)
          .get();
    }
  },
  BOLD_191() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  BOLD_191_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .get();
    }
  },
  BOLD_191_LEFT_CENTER_WRAP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.LEFT)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setWrapText(true)
          .get();
    }
  },
  BOLD_191_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  BOLD_191_COUNT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.GREY_191)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.COUNT))
          .get();
    }
  },
  NORMAL_WHITE_RIGHT() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.WHITE)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .get();
    }
  },
  NORMAL_WHITE_RIGHT_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_NORMAL))
          .setColor(EColor.WHITE)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.RIGHT)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  BOLD_BLUE() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.BLUE)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  BOLD_BLUE_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_11_BOLD))
          .setColor(EColor.BLUE)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  SMALL_NORMAL_CENTER_CENTER_WRAP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setWrapText(true)
          .get();
    }
  },
  SMALL_NORMAL_242_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  SMALL_NORMAL_242_CENTER_JUSTIFY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.JUSTIFY)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  SMALL_NORMAL_242_CENTER_CENTER_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  SMALL_BOLD_216() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .get();
    }
  },
  SMALL_BOLD_216_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_7_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  CALIBRI_8_NORMAL_THIN_CENTER_CENTER_WRAP() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_NORMAL))
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setWrapText(true)
          .get();
    }
  },
  CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  CALIBRI_8_NORMAL_GREY_242_THIN_JUSTIFY_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.JUSTIFY)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  CALIBRI_8_NORMAL_GREY_242_THIN_CENTER_CENTER_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_NORMAL))
          .setColor(EColor.GREY_242)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .get();
    }
  },
  CALIBRI_8_BOLD_GREY_216_THIN_CENTER_CENTER_MONEY() {
    @NotNull
    @Override
    public XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat) {
      return new WrapStyle(book)
          .setFont(font.getFont(EFont.CALIBRI_8_BOLD))
          .setColor(EColor.GREY_216)
          .setBorder(BorderStyle.THIN)
          .setHorizontalAlignment(HorizontalAlignment.CENTER)
          .setVerticalAlignment(VerticalAlignment.CENTER)
          .setDataFormat(dataFormat.getDataFormat(EDataFormat.MONEY))
          .get();
    }
  },
  ;

  @NotNull
  public abstract XSSFCellStyle createStyle(@NotNull XSSFWorkbook book, @NotNull IFont font, @NotNull IDataFormat dataFormat);
}
