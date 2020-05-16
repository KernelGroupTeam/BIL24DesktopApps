package client.editor.model;

import java.math.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import eventim.spl.models.EventimNplCategory;
import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class AddCatTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"п/п", "Категория", "Мест", "Начальная цена"};
  private static final Class<?>[] columnClasses = {Integer.class, String.class, Integer.class, BigDecimal.class};
  @NotNull
  private final List<CategoryObj> categoryList = new ArrayList<>();
  @NotNull
  private final List<CategoryLimitObj> categoryLimitList = new ArrayList<>();
  private int index = 1;
  private boolean readOnly = false;//после установки из файла, запрещаем менять что либо, кроме названия, начальной цены и лимитов

  public AddCatTableModel() {
    addCat();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return categoryList.size() + categoryLimitList.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < categoryList.size()) {
      CategoryObj category = categoryList.get(rowIndex);
      if (columnIndex == 0) return rowIndex + 1;
      if (columnIndex == 1) return category.getName();
      if (columnIndex == 2) return (category.getSeatsNumber() < 0 ? null : category.getSeatsNumber());
      if (columnIndex == 3) return category.getInitPrice();
    } else {
      int limitIndex = rowIndex - categoryList.size();
      CategoryLimitObj categoryLimit = categoryLimitList.get(limitIndex);
      if (columnIndex == 0) return null;
      if (columnIndex == 1) return getLimitName(categoryLimit);
      if (columnIndex == 2) return (categoryLimit.getLimit() <= 0 ? null : categoryLimit.getLimit());
      if (columnIndex == 3) return null;
    }
    return null;
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
    if (rowIndex < categoryList.size()) {
      if (readOnly) return columnIndex == 1 || columnIndex == 3;//только название и начальная цена
      else return columnIndex != 0;
    } else {
      return columnIndex == 2;
    }
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (rowIndex < categoryList.size()) {
      CategoryObj category = categoryList.get(rowIndex);
      if (columnIndex == 1) {
        if (value == null) return;
        if (!(value instanceof String)) return;
        String name = ((String) value).trim();
        if (name.isEmpty()) return;
        category.setName(name);
        fireTableCellUpdated(rowIndex, columnIndex);
        for (int row = rowIndex; row < getRowCount(); row++) {//обновить названия в ограничениях так, чтобы выделение таблицы осталось
          if (isRowLimit(row)) fireTableCellUpdated(row, columnIndex);
        }
      }
      if (columnIndex == 2) {
        if (readOnly) return;
        if (value == null) return;
        if (!(value instanceof Integer)) return;
        Integer num = (Integer) value;
        if (num < 0) return;
        category.setSeatsNumber(num);
        fireTableCellUpdated(rowIndex, columnIndex);
      }
      if (columnIndex == 3) {
        if (value == null) {
          category.setInitPrice(null);
          fireTableCellUpdated(rowIndex, columnIndex);
          return;
        }
        if (!(value instanceof BigDecimal)) return;
        BigDecimal initPrice = (BigDecimal) value;
        if (initPrice.scale() > 2) initPrice = initPrice.setScale(2, RoundingMode.HALF_UP);
        if (initPrice.compareTo(BigDecimal.ZERO) < 0) return;
        category.setInitPrice(initPrice);
        fireTableCellUpdated(rowIndex, columnIndex);
      }
    } else {
      int limitIndex = rowIndex - categoryList.size();
      CategoryLimitObj categoryLimit = categoryLimitList.get(limitIndex);
      if (columnIndex == 2) {
        if (value == null) return;
        if (!(value instanceof Integer)) return;
        int num = (Integer) value;
        if (num <= 0) return;
        categoryLimit.setLimit(num);
        fireTableCellUpdated(rowIndex, columnIndex);
      }
    }
  }

  public void setDataFromSpl(@NotNull List<EventimNplCategory> list) {
    readOnly = true;
    categoryList.clear();
    categoryLimitList.clear();
    for (EventimNplCategory nplCategory : list) {
      CategoryObj category = new CategoryObj(index++, false);
      category.setName(nplCategory.getSector().trim());
      category.setSeatsNumber(nplCategory.getSeatCount());
      categoryList.add(category);
    }
    fireTableDataChanged();
  }

  public boolean isCanAddCat() {
    return !readOnly;
  }

  public void addCat() {
    if (readOnly) return;
    CategoryObj category = new CategoryObj(index++, false);
    category.setSeatsNumber(-1);
    categoryList.add(category);
    fireTableDataChanged();
  }

  public boolean isCanRemove(int[] rows) {
    if (rows.length == 0) return false;
    if (readOnly) {
      for (int row : rows) {
        if (row < categoryList.size()) return false;
      }
    }
    return true;
  }

  public void remove(int[] rows) {
    int categorySize = categoryList.size();
    for (int i = categoryLimitList.size() - 1; i >= 0; i--) {
      for (int row : rows) {
        if (row - categorySize == i) {
          categoryLimitList.remove(i);
        }
      }
    }
    if (!readOnly) {
      for (int i = categoryList.size() - 1; i >= 0; i--) {
        for (int row : rows) {
          if (row == i) {
            CategoryObj category = categoryList.remove(i);
            for (Iterator<CategoryLimitObj> iterator = categoryLimitList.iterator(); iterator.hasNext(); ) {
              CategoryLimitObj categoryLimit = iterator.next();
              if (categoryLimit.getCategoryList().contains(category)) iterator.remove();
            }
            break;
          }
        }
      }
    }
    fireTableDataChanged();
  }

  public boolean isCanAddLimit(int[] rows) {
    int counter = 0;
    for (int row : rows) {
      if (row < categoryList.size()) counter++;
      if (counter >= 2) return true;
    }
    return false;
  }

  public void addLimit(int[] rows) {
    Arrays.sort(rows);
    LinkedHashSet<CategoryObj> categorySet = new LinkedHashSet<>();
    for (int row : rows) {
      if (row < categoryList.size()) {
        categorySet.add(categoryList.get(row));
      } else break;
    }
    if (categorySet.size() < 2) return;
    for (Iterator<CategoryLimitObj> iterator = categoryLimitList.iterator(); iterator.hasNext(); ) {
      CategoryLimitObj categoryLimit = iterator.next();
      for (CategoryObj category : categorySet) {
        if (categoryLimit.getCategoryList().contains(category)) {
          iterator.remove();
          break;
        }
      }
    }
    CategoryLimitObj categoryLimit = new CategoryLimitObj(index++);
    categoryLimit.setCategoryList(new ArrayList<CategoryObj>());
    categoryLimit.getCategoryList().addAll(categorySet);
    categoryLimit.setLimit(-1);
    categoryLimitList.add(categoryLimit);
    fireTableDataChanged();
  }

  @NotNull
  public List<CategoryObj> getCategoryList() {
    return Collections.unmodifiableList(categoryList);
  }

  @NotNull
  public List<CategoryLimitObj> getCategoryLimitList() {
    return Collections.unmodifiableList(categoryLimitList);
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

  boolean isRowLimit(int row) {
    return row >= categoryList.size();
  }
}
