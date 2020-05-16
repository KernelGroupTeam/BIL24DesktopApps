package excel.enums;

import java.awt.*;

import org.apache.poi.xssf.usermodel.XSSFColor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.07.2017
 */
public enum EColor {
  GREY_191(new XSSFColor(new Color(191, 191, 191))),
  GREY_216(new XSSFColor(new Color(216, 216, 216))),
  GREY_242(new XSSFColor(new Color(242, 242, 242))),
  WHITE(new XSSFColor(new Color(255, 255, 255))),
  BLUE(new XSSFColor(new Color(141, 180, 227))),
  ;

  @NotNull
  private final XSSFColor color;

  EColor(@NotNull XSSFColor color) {
    this.color = color;
  }

  @NotNull
  public XSSFColor getColor() {
    return color;
  }
}
