package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.editor.SeatingPlanObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class SeatingPlanListRenderer2 implements ListCellRenderer<SeatingPlanObj>, ElementToStringConverter<SeatingPlanObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends SeatingPlanObj> list, SeatingPlanObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(SeatingPlanObj value) {
    if (value == null) return "";
    return "[" + value.getId() + "] " + value.getName();

  }
}
