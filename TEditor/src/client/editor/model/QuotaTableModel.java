package client.editor.model;

import java.math.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.*;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class QuotaTableModel extends AbstractTableModel {
  public static final CategoryPriceObj defCategoryPrice = new CategoryPriceObj(0);
  static {
    defCategoryPrice.setName("Автоматически");
  }
  private static final CategoryPriceObj emptyCategoryPrice = new CategoryPriceObj(0);
  private static final String[] columnNames = {"Кол-во мест", "Цена", "Сумма", "Категория"};
  private static final Class<?>[] columnClasses = {String.class, BigDecimal.class, BigDecimal.class, CategoryPriceObj.class};
  private Object[][] data = new Object[0][0];
  @NotNull
  private List<QuotaInManualObj.Data> dataList = Collections.emptyList();
  @Nullable
  private Integer quantity;//quantity не null если добавляем новую строку
  private int totalQty;//без учета добавляемой строки
  @NotNull
  private BigDecimal totalSum = BigDecimal.ZERO;//без учета добавляемой строки

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

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (quantity == null) return false;
    if (columnIndex != 1 && columnIndex != 3) return false;
    return rowIndex != (data.length - 1);
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (quantity == null) return;
    if (columnIndex != 1 && columnIndex != 3) return;
    int addingRow = data.length - 2;
    if (columnIndex == 1) {
      if (rowIndex == addingRow) {
        if (value == null) {
          setData(dataList, quantity, null, getPrefCategory());
        } else {
          if (!(value instanceof BigDecimal)) return;
          BigDecimal price = (BigDecimal) value;
          if (price.scale() > 2) price = price.setScale(2, RoundingMode.HALF_UP);
          if (price.compareTo(BigDecimal.ZERO) < 0) return;
          setData(dataList, quantity, price, getPrefCategory());
        }
      } else if (rowIndex < addingRow) {
        if (value == null) return;
        if (!(value instanceof BigDecimal)) return;
        BigDecimal price = (BigDecimal) value;
        if (price.scale() > 2) price = price.setScale(2, RoundingMode.HALF_UP);
        if (price.compareTo(BigDecimal.ZERO) < 0) return;
        dataList.get(rowIndex).setPrice(price);
        setData(dataList, quantity, getPrice(), getPrefCategory());
      }
    }
    if (columnIndex == 3) {
      if (rowIndex == addingRow) {
        if (value == defCategoryPrice) value = null;
        if (value == null) {
          setData(dataList, quantity, getPrice(), null);
        } else {
          if (!(value instanceof CategoryPriceObj)) return;
          CategoryPriceObj prefCategory = (CategoryPriceObj) value;
          setData(dataList, quantity, getPrice(), prefCategory);
        }
      } else if (rowIndex < addingRow) {
        if (value != null && !(value instanceof CategoryPriceObj)) return;
        CategoryPriceObj prefCategory = (CategoryPriceObj) value;
        dataList.get(rowIndex).setPrefCategory(prefCategory);
        setData(dataList, quantity, getPrice(), getPrefCategory());
      }
    }
  }

  //quota == null значит новая квота, еще нет ни одной строки с введенными данными
  //quantity == null значит квота вся введена, строку добавления не показываем
  public void setData(@Nullable QuotaInManualObj quota, @Nullable Integer quantity) {
    setData(quota, quantity, null, null);
  }

  public void setData(@Nullable QuotaInManualObj quota, @Nullable Integer quantity, @Nullable BigDecimal price, @Nullable CategoryPriceObj prefCategory) {
    List<QuotaInManualObj.Data> dataList;
    if (quota == null) dataList = Collections.emptyList();
    else dataList = quota.getDataList();
    setData(dataList, quantity, price, prefCategory);
  }

  private void setData(@NotNull List<QuotaInManualObj.Data> dataList, @Nullable Integer quantity, @Nullable BigDecimal price, @Nullable CategoryPriceObj prefCategory) {
    this.dataList = dataList;
    this.quantity = quantity;

    Object[][] buffer;
    if (quantity == null) buffer = new Object[dataList.size() + 1][columnNames.length];
    else buffer = new Object[dataList.size() + 2][columnNames.length];
    totalQty = 0;
    totalSum = BigDecimal.ZERO;
    for (int i = 0; i < dataList.size(); i++) {
      QuotaInManualObj.Data quotaData = dataList.get(i);
      totalQty += quotaData.getQuantity();
      totalSum = totalSum.add(quotaData.getSum());
      buffer[i][0] = String.valueOf(quotaData.getQuantity());
      buffer[i][1] = quotaData.getPrice();
      buffer[i][2] = quotaData.getSum();
      buffer[i][3] = quotaData.getPrefCategory();
    }

    if (quantity != null) {
      int addingRow = buffer.length - 2;
      buffer[addingRow][0] = String.valueOf(quantity);
      buffer[addingRow][1] = price;
      if (price != null) {
        buffer[addingRow][2] = price.multiply(new BigDecimal(quantity));
      } else {
        buffer[addingRow][2] = null;
      }
      buffer[addingRow][3] = prefCategory;
    }

    int lastRow = buffer.length - 1;
    if (quantity != null && price != null) {
      BigDecimal sum = price.multiply(new BigDecimal(quantity));
      buffer[lastRow][0] = "Итого: " + (totalQty + quantity);
      buffer[lastRow][2] = totalSum.add(sum);
    } else {
      buffer[lastRow][0] = "Итого: " + totalQty;
      buffer[lastRow][2] = totalSum;
    }
    buffer[lastRow][3] = emptyCategoryPrice;

    boolean fullUpdate = (data.length != buffer.length);
    data = buffer;
    if (fullUpdate) fireTableDataChanged();
    else fireTableRowsUpdated(0, data.length - 1);
  }

  public int getAddingRow() {
    return data.length - 2;
  }

  @Nullable
  public QuotaInManualObj.Data getData(int row) {
    if (row < dataList.size()) return dataList.get(row);
    return null;
  }

  public boolean isInputComplete() {//ввод завершен или не требуется
    if (quantity == null) return true;
    int addingRow = data.length - 2;
    return data[addingRow][1] != null;
  }

  @Nullable
  public BigDecimal getPrice() {
    if (quantity == null) return null;
    return (BigDecimal) data[getAddingRow()][1];
  }

  @Nullable
  public CategoryPriceObj getPrefCategory() {
    if (quantity == null) return null;
    return (CategoryPriceObj) data[getAddingRow()][3];
  }

  public int getCompleteTotalQty() {
    if (quantity == null) return totalQty;
    else return totalQty + quantity;
  }

  @Nullable
  public BigDecimal getCompleteTotalSum() {
    if (quantity == null) return totalSum;
    BigDecimal price = getPrice();
    if (price == null) return null;
    BigDecimal sum = price.multiply(new BigDecimal(quantity));
    return totalSum.add(sum);
  }
}
