package client.reporter.model;

import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.RFrontend;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.17
 */
public class QueryStatsTableModel extends AbstractTableModel {
  private static final String unknownFrontend = "Неизвестный";
  private static final String[] initColumnNames = {"Интерфейс", "Запросов"};
  private static final Class<?>[] initColumnClasses = {String.class, Integer.class};
  private static String[] columnNames = initColumnNames;
  private static Class<?>[] columnClasses = initColumnClasses;
  private Object[][] data = new Object[0][0];

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
    return true;
  }

  public void setData(Map<Long, RFrontend> frontendMap, Map<Long, Map<String, Integer>> statsMap) {
    TreeMap<String, Integer> commandMap = new TreeMap<>();
    Integer zero = 0;
    for (Map.Entry<Long, Map<String, Integer>> entry : statsMap.entrySet()) {
      for (Map.Entry<String, Integer> frontendEntry : entry.getValue().entrySet()) {
        commandMap.put(frontendEntry.getKey(), zero);
      }
    }
    columnNames = new String[initColumnNames.length + commandMap.size()];
    System.arraycopy(initColumnNames, 0, columnNames, 0, initColumnNames.length);
    int index = initColumnNames.length;
    for (Map.Entry<String, Integer> entry : commandMap.entrySet()) {
      columnNames[index] = "<html><font size=2>" + entry.getKey() + "</font></html>";
      index++;
    }
    columnClasses = new Class[initColumnClasses.length + commandMap.size()];
    System.arraycopy(initColumnClasses, 0, columnClasses, 0, initColumnClasses.length);
    index = initColumnClasses.length;
    for (Map.Entry<String, Integer> entry : commandMap.entrySet()) {
      columnClasses[index] = Integer.class;
      index++;
    }

    Map<Long, Map<String, Integer>> sortedStats = new TreeMap<>(statsMap);

    Object[][] buffer = new Object[sortedStats.size() + 1][columnNames.length];
    int total = 0;
    int i = 0;
    for (Map.Entry<Long, Map<String, Integer>> entry : sortedStats.entrySet()) {
      RFrontend frontend = frontendMap.get(entry.getKey());
      if (frontend == null) buffer[i][0] = unknownFrontend;
      else buffer[i][0] = frontendToString(frontend);
      int frontendTotal = 0;
      int j = initColumnNames.length;
      for (Map.Entry<String, Integer> commandEntry : commandMap.entrySet()) {
        Integer number = entry.getValue().get(commandEntry.getKey());
        if (number != null) {
          buffer[i][j] = number;
          commandEntry.setValue(commandEntry.getValue() + number);
          frontendTotal += number;
        }
        j++;
      }
      buffer[i][1] = frontendTotal;
      total += frontendTotal;
      i++;
    }

    int lastRow = buffer.length - 1;
    buffer[lastRow][0] = "Итого:";
    buffer[lastRow][1] = total;
    int j = initColumnNames.length;
    for (Map.Entry<String, Integer> commandEntry : commandMap.entrySet()) {
      Integer count = commandEntry.getValue();
      buffer[lastRow][j] = count;
      j++;
    }

    if (statsMap.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableStructureChanged();
  }

  @NotNull
  private String frontendToString(@NotNull RFrontend frontend) {
    return "[" + frontend.getId() + "] " + frontend.getName() + " [" + frontend.getType().getName() + ']';
  }
}
