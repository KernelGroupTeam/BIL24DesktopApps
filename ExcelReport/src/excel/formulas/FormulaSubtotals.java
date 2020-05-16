package excel.formulas;

import excel.interfaces.IFormula;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 19.08.2017
 */
public final class FormulaSubtotals {
  @NotNull
  private final FormulaSubtotal[] formulaSubtotals;
  private int next;

  public FormulaSubtotals(@NotNull char... columns) {
    formulaSubtotals = new FormulaSubtotal[columns.length];
    for (int i = 0; i < formulaSubtotals.length; i++) formulaSubtotals[i] = new FormulaSubtotal(columns[i]);
  }

  public FormulaSubtotals(char startColumn, int count) {
    formulaSubtotals = new FormulaSubtotal[count];
    for (int i = 0; i < formulaSubtotals.length; i++) formulaSubtotals[i] = new FormulaSubtotal((char)(startColumn + i));
  }

  public void setRowStart(int rowIndex) {
    rowIndex++;//Смещаю значение номера строки, т.к. индекс строки на 1 меньше чем номер строки
    for (FormulaSubtotal formulaSubtotal : formulaSubtotals) formulaSubtotal.setRowStart(rowIndex);
  }

  public void setRowEnd(int rowIndex) {
    rowIndex++;//Смещаю значение номера строки, т.к. индекс строки на 1 меньше чем номер строки
    for (FormulaSubtotal formulaSubtotal : formulaSubtotals) formulaSubtotal.setRowEnd(rowIndex);
  }

  public void reset() {
    next = 0;
    setRowStart(0);
    setRowEnd(0);
  }

  /**
   * Если сейчас было обращение к первому элементу
   */
  public boolean isFirstAfterNext() {
    return next == 1;
  }

  /**
   * Если сейчас будет обращение к первому элементу
   */
  public boolean isFirstBeforeNext() {
    return next == 0;
  }

  /**
   * Если сейчас было обращение к последнему элементу
   */
  public boolean isLastAfterNext() {
    return next == formulaSubtotals.length;
  }

  /**
   * Если сейчас будет обращение к последнему элементу
   */
  public boolean isLastBeforeNext() {
    return next + 1 == formulaSubtotals.length;
  }

  public boolean hasNext() {
    return next < formulaSubtotals.length;
  }

  @NotNull
  public IFormula nextFormula() {
    return formulaSubtotals[next++];
  }

  private final class FormulaSubtotal implements IFormula {
    private final char column;//Столбец
    private int rowStart;//Номер начальной строки
    private int rowEnd;//Номер конечной строки

    private FormulaSubtotal(char column) {
      this.column = column;
    }

    private void setRowStart(int row) {
      this.rowStart = row;
    }

    private void setRowEnd(int row) {
      this.rowEnd = row;
    }

    @Override
    public char getColumn() {
      return column;
    }

    @NotNull
    @Override
    public String getFormula() {
      return "SUBTOTAL(9," + column + rowStart + ":" + column + rowEnd + ")";
    }
  }
}
