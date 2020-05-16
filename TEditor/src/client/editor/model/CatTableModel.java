package client.editor.model;

import java.math.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class CatTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"Категория", "Мест", "Начальная цена"};
  private static final Class<?>[] columnClasses = {String.class, Integer.class, BigDecimal.class};
  private Object[][] data = new Object[0][0];
  @NotNull
  private final List<CategoryObj> categoryList = new ArrayList<>();
  @NotNull
  private final List<CategoryLimitObj> categoryLimitList = new ArrayList<>();
  private boolean readOnly;

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnClasses[columnIndex];
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (readOnly) return false;
    if (rowIndex < categoryList.size()) {
      if (columnIndex == 0 || columnIndex == 2) return true;
      if (columnIndex == 1) return !categoryList.get(rowIndex).isPlacement();
    } else {
      return columnIndex == 1;
    }
    return false;
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (rowIndex < categoryList.size()) {
      if (columnIndex == 0) {
        if (value == null) return;
        if (!(value instanceof String)) return;
        String name = ((String) value).trim();
        if (name.isEmpty()) return;
        updateData(rowIndex, columnIndex, name);
      }
      if (columnIndex == 1) {
        if (value == null) return;
        if (!(value instanceof Integer)) return;
        Integer num = (Integer) value;
        if (num < 0) return;
        updateData(rowIndex, columnIndex, num);
      }
      if (columnIndex == 2) {
        if (value == null) {
          updateData(rowIndex, columnIndex, null);
          return;
        }
        if (!(value instanceof BigDecimal)) return;
        BigDecimal initPrice = (BigDecimal) value;
        if (initPrice.scale() > 2) initPrice = initPrice.setScale(2, RoundingMode.HALF_UP);
        if (initPrice.compareTo(BigDecimal.ZERO) < 0) return;
        updateData(rowIndex, columnIndex, initPrice);
      }
    } else {
      if (columnIndex == 1) {
        if (value == null) return;
        if (!(value instanceof Integer)) return;
        Integer num = (Integer) value;
        if (num <= 0) return;
        updateData(rowIndex, columnIndex, num);
      }
    }
  }

  private void updateData(int row, int col, Object newValue) {
    Object oldValue = data[row][col];
    if (!Objects.equals(oldValue, newValue)) {
      data[row][col] = newValue;
      fireTableCellUpdated(row, col);
    }
  }

  public void setData(@NotNull List<CategoryObj> categoryList, @NotNull List<CategoryLimitObj> categoryLimitList) {
    this.categoryList.clear();
    this.categoryList.addAll(categoryList);
    this.categoryLimitList.clear();
    this.categoryLimitList.addAll(categoryLimitList);
    Object[][] buffer = new Object[categoryList.size() + categoryLimitList.size()][columnNames.length];
    for (int i = 0; i < categoryList.size(); i++) {
      CategoryObj element = categoryList.get(i);
      buffer[i][0] = element.getName();
      buffer[i][1] = element.getSeatsNumber();
      buffer[i][2] = element.getInitPrice();
    }
    int dy = categoryList.size();
    for (int i = 0; i < categoryLimitList.size(); i++) {
      CategoryLimitObj element = categoryLimitList.get(i);
      buffer[dy + i][0] = getLimitName(element);
      buffer[dy + i][1] = element.getLimit();
    }
    if (categoryList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public void clear() {
    data = new Object[0][0];
    fireTableDataChanged();
  }

  @NotNull
  private static String getLimitName(CategoryLimitObj categoryLimit) {
    StringBuilder result = new StringBuilder("<HTML>");
    for (CategoryObj category : categoryLimit.getCategoryList()) {
      result.append(category.getName()).append("<BR>");
    }
    result.append("</HTML>");
    return result.toString();
  }

  public boolean isRowLimit(int row) {
    return row >= categoryList.size();
  }
}
