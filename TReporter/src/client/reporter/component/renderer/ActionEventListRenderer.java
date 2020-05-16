package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.reporter.RActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ActionEventListRenderer implements ListCellRenderer<RActionEvent>, ElementToStringConverter<RActionEvent> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends RActionEvent> list, RActionEvent value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    Component component = renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
    if (value != null && value.isSellEnd()) component.setFont(component.getFont().deriveFont(Font.PLAIN));
    return component;
  }

  @Override
  public String stringValue(RActionEvent value) {
    if (value == null) return "";
    if (value.getId() == 0) return value.getShowTime();
    return "[" + value.getId() + "] " + value.getShowTime();
  }
}
