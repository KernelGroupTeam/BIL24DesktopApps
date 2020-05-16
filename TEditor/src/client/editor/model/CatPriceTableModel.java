package client.editor.model;

import java.math.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.CategoryPriceObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class CatPriceTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"Категория", "Цена"};
  private Object[][] data = new Object[0][0];
  @NotNull
  private final List<CategoryPriceObj> categoryPriceList = new ArrayList<>();
  private boolean quota;

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
    return getValueAt(0, columnIndex).getClass();
  }

  @SuppressWarnings("RedundantIfStatement")
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (quota && columnIndex == 1) return false;
    return true;
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (value == null) return;
    if (columnIndex == 0) {
      if (!(value instanceof String)) return;
      String name = ((String) value).trim();
      if (name.isEmpty()) return;
      updateData(rowIndex, columnIndex, name);
    }
    if (columnIndex == 1) {
      if (!(value instanceof BigDecimal)) return;
      BigDecimal price = (BigDecimal) value;
      if (price.scale() > 2) price = price.setScale(2, RoundingMode.HALF_UP);
      if (price.compareTo(BigDecimal.ZERO) < 0) return;
      updateData(rowIndex, columnIndex, price);
    }
  }

  private void updateData(int row, int col, Object newValue) {
    Object oldValue = data[row][col];
    if (!Objects.equals(oldValue, newValue)) {
      data[row][col] = newValue;
      fireTableCellUpdated(row, col);
    }
  }

  public void setData(@NotNull List<CategoryPriceObj> categoryPriceList, boolean quota) {
    this.categoryPriceList.clear();
    this.categoryPriceList.addAll(categoryPriceList);
    this.quota = quota;
    Object[][] buffer = new Object[categoryPriceList.size()][columnNames.length];
    for (int i = 0; i < categoryPriceList.size(); i++) {
      CategoryPriceObj element = categoryPriceList.get(i);
      buffer[i][0] = element.getName();
      buffer[i][1] = element.getPrice();
    }
    if (categoryPriceList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }

  public void clear() {
    data = new Object[0][0];
    fireTableDataChanged();
  }

  public int getAvailability(int row) {
    return categoryPriceList.get(row).getAvailability();
  }
}
