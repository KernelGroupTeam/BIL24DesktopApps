package client.reporter.model;

import java.text.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.*;
import server.protocol2.reporter.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.07.17
 */
public class PassTableModel extends AbstractTableModel {
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static final String[] columnNames = {"ID", "Время", "Агент", "Интерфейс", "FID",
      "Название представления", "Начало сеанса", "Результат", "ID билета", "Сектор", "Ряд", "Место", "Категория",
      "ID клиента", "e-mail доставки", "Комментарий"};
  private static final Class<?>[] columnClasses = {Long.class, String.class, String.class, String.class, String.class,
      String.class, String.class, PassObj.Status.class, Long.class, String.class, String.class, String.class, String.class,
      Long.class, String.class, String.class};
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
      try {
        buffer[i][6] = dateFormat2.format(element.getActionEvent().getShowTimeDate());
      } catch (ParseException e) {
        buffer[i][6] = element.getActionEvent().getShowTime();
      }
      buffer[i][7] = element.getStatus();
      if (element.getTicket() != null) {
        buffer[i][8] = element.getTicket().getId();
        SeatLocationObj seatLocation = element.getTicket().getSeatLocation();
        if (seatLocation != null) {
          buffer[i][9] = seatLocation.getSector();
          buffer[i][10] = seatLocation.getRow();
          buffer[i][11] = seatLocation.getNumber();
        } else {
          buffer[i][9] = "";
          buffer[i][10] = "";
          buffer[i][11] = "";
        }
        buffer[i][12] = element.getTicket().getCategory();
        buffer[i][13] = element.getTicket().getUser().getId();
        buffer[i][14] = element.getTicket().getEmail();
      } else {
        buffer[i][8] = "";
        buffer[i][9] = "";
        buffer[i][10] = "";
        buffer[i][11] = "";
        buffer[i][12] = "";
        buffer[i][13] = "";
        buffer[i][14] = "";
      }
      buffer[i][15] = element.getDescription();
    }
    int lastRow = buffer.length - 1;
    buffer[lastRow][1] = "Итого: " + passList.size();
    if (passList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    fireTableDataChanged();
  }

  @Nullable
  public PassObj.Status getStatus(int row) {
    if (row >= passList.size()) return null;
    return passList.get(row).getStatus();
  }
}
