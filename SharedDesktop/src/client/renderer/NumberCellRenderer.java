package client.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.renderer.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.10.18
 * <p/>
 * Переопределяет метод getString() в DefaultTableRenderer для чисел, чтобы работал поиск по этим значениям
 */
public class NumberCellRenderer implements TableCellRenderer, StringValue {
  private final TableCellRenderer defaultTableCellRenderer = new DefaultTableRenderer(StringValues.NUMBER_TO_STRING, JLabel.RIGHT);

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return defaultTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }

  @Override
  public String getString(Object value) {
    if (value == null) return "";
    return value.toString();
  }
}
