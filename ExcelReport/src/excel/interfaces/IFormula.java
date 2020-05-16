package excel.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.11.2017
 */
public interface IFormula {
  char getColumn();

  @NotNull
  String getFormula();
}
