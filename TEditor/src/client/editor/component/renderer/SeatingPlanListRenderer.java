package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import server.protocol2.editor.SeatingPlanObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class SeatingPlanListRenderer implements ListCellRenderer<SeatingPlanObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final int charLimit;

  public SeatingPlanListRenderer(int charLimit) {
    this.charLimit = charLimit;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends SeatingPlanObj> list, SeatingPlanObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = (value == null) ? "" : value.getName();
    if (value != null && value.isSplExists()) str = "[+spl] " + str;
    if (str.length() > charLimit) str = str.substring(0, charLimit) + "…";
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }
}
