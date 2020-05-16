package client.utils;

import java.awt.*;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.07.16
 */
public class TableUtils {

  private TableUtils() {
  }

  public static void editCellAtModel(JTable table, int modelRow, int modelColumn) {
    int row = table.convertRowIndexToView(modelRow);
    int column = table.convertColumnIndexToView(modelColumn);
    editCellAtView(table, row, column);
  }

  public static void editCellAtView(final JTable table, final int row, final int column) {
    table.requestFocus();
    table.changeSelection(row, column, false, false);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        table.editCellAt(row, column);
      }
    });
  }

  public static void updateRowHeights(JTable table) {
    for (int row = 0; row < table.getRowCount(); row++) {
      int rowHeight = table.getRowHeight();
      for (int column = 0; column < table.getColumnCount(); column++) {
        Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
        rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
      }
      table.setRowHeight(row, rowHeight);
    }
  }
}
