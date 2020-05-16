package excel.formulas;

import java.util.*;

import excel.interfaces.IFormula;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 18.08.2017
 */
public class FormulaSums implements Iterator<IFormula> {
  @NotNull
  private final FormulaSum[] formulaSums;
  private int next;

  //Пока пусть будет на всякий если вдруг окажется что колонки идут не одна за другой
  public FormulaSums(@NotNull char... columns) {
    formulaSums = new FormulaSum[columns.length];
    for (int i = 0; i < formulaSums.length; i++) formulaSums[i] = new FormulaSum(columns[i]);
  }

  public FormulaSums(char startColumn, int count) {
    formulaSums = new FormulaSum[count];
    for (int i = 0; i < formulaSums.length; i++) formulaSums[i] = new FormulaSum((char)(startColumn + i));
  }

  public void add(int row) {
    row++;
    for (FormulaSum formulaSum : formulaSums) formulaSum.add(row);
  }

  /**
   * Если сейчас будет обращение к первому элементу
   */
  public boolean isFirstBeforeNext() {
    return next == 0;
  }

  /**
   * Если сейчас будет обращение к последнему элементу
   */
  public boolean isLastBeforeNext() {
    return next + 1 == formulaSums.length;
  }

  @Override
  public boolean hasNext() {
    return next < formulaSums.length;
  }

  @Override
  public IFormula next() {
    return formulaSums[next++];
  }

  @Override
  public void remove() {

  }

  private final class FormulaSum implements IFormula {
    private static final int LIMIT = 255;
    @NotNull
    private final List<Integer> rowList = new ArrayList<>();
    private final char column;

    private FormulaSum(char column) {
      this.column = column;
    }

    private void add(int row) {
      rowList.add(row);
    }

    @Override
    public char getColumn() {
      return column;
    }

    @NotNull
    @Override
    public String getFormula() {
      StringBuilder formula = new StringBuilder();
      int countSum = (int)Math.ceil((float)rowList.size() / LIMIT);
      for (int i = 0; i < countSum; i++) {
        formula.append("SUM(");
        int startIndex = i * LIMIT;
        int endIndex = Math.min(startIndex + LIMIT, rowList.size());
        for (int j = startIndex; j < endIndex; j++) {
          formula.append(column);
          formula.append(rowList.get(j));
          formula.append(",");
        }
        formula.setLength(formula.length() - 1);//Отрезаем "," в конце
        formula.append(")+");
      }
      formula.setLength(formula.length() - 1);//Отрезаем "+" в конце
      return formula.toString();
    }
  }
}
