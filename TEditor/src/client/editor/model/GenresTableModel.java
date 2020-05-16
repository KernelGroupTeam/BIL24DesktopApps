package client.editor.model;

import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.GenreObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class GenresTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"", "ID", "Жанр"};
  private static final Class<?>[] columnClasses = {Boolean.class, Integer.class, String.class};
  private Object[][] data = new Object[0][0];
  private List<GenreObj> genreList = Collections.emptyList();

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
    return columnIndex == 0;
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (value == null) return;
    if (columnIndex == 0) {
      if (!(value instanceof Boolean)) return;
      updateData(rowIndex, columnIndex, value);
    }
  }

  private void updateData(int row, int col, Object newValue) {
    Object oldValue = data[row][col];
    if (!Objects.equals(oldValue, newValue)) {
      data[row][col] = newValue;
      fireTableCellUpdated(row, col);
    }
  }

  public void setData(@NotNull List<GenreObj> genreList) {
    setData(genreList, Collections.<GenreObj>emptySet());
  }

  public void setData(@NotNull List<GenreObj> genreList, @NotNull Set<GenreObj> selectedGenreSet) {
    this.genreList = genreList;
    Object[][] buffer = new Object[genreList.size()][columnNames.length];
    for (int i = 0; i < genreList.size(); i++) {
      GenreObj element = genreList.get(i);
      buffer[i][0] = selectedGenreSet.contains(element);
      buffer[i][1] = element.getId();
      buffer[i][2] = element.getName();
    }
    boolean fullUpdate = (data.length != buffer.length);
    if (genreList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    if (fullUpdate) fireTableDataChanged();
    else fireTableRowsUpdated(0, data.length - 1);
  }

  public void invertSelection(int[] rows) {
    for (int row : rows) {
      data[row][0] = !(Boolean) data[row][0];
      fireTableCellUpdated(row, 0);
    }
  }

  @NotNull
  public Set<GenreObj> getSelectedGenreSet() {
    Set<GenreObj> result = new HashSet<>();
    for (int i = 0; i < data.length; i++) {
      if (data[i][0] == Boolean.TRUE) result.add(genreList.get(i));
    }
    return result;
  }
}
