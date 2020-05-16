package client.reporter.model;

import java.text.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.PassObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.07.17
 */
public class MecTableModel extends AbstractTableModel {
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String[] columnNames = {"ID", "Время", "Агент", "Интерфейс", "FID",
      "Название карты", "ID карты", "Категория", "ID клиента", "e-mail клиента"};
  private static final Class<?>[] columnClasses = {Long.class, String.class, String.class, String.class, String.class,
      String.class, Long.class, String.class, Long.class, String.class};
  private Object[][] data = new Object[0][0];
  @NotNull
  private List<PassObj> passList = Collections.emptyList();

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

  public void setData(List<PassObj> passList) {
    this.passList = passList;
    Object[][] buffer = new Object[passList.size() + 1][columnNames.length];
    for (int i = 0; i < passList.size(); i++) {
      PassObj element = passList.get(i);
      buffer[i][0] = element.getId();
      try {
        buffer[i][1] = dateFormat.format(PassObj.parseFormat(element.getTime()));
      } catch (ParseException e) {
        buffer[i][1] = element.getTime();
      }
      buffer[i][2] = element.getAgent().getName();
      buffer[i][3] = element.getFrontend().getName();
      buffer[i][4] = element.getFrontend().getIdStr();
      buffer[i][5] = element.getActionEvent().getActionName();
      if (element.getTicket() != null) {
        buffer[i][6] = element.getTicket().getId();
        buffer[i][7] = element.getTicket().getCategory();
        buffer[i][8] = element.getTicket().getUser().getId();
        buffer[i][9] = element.getTicket().getUser().getEmail();
      } else {
        buffer[i][6] = "";
        buffer[i][7] = "";
        buffer[i][8] = "";
        buffer[i][9] = "";
      }
    }
    int lastRow = buffer.length - 1;
    buffer[lastRow][1] = "Итого: " + passList.size();
    if (passList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }
}
