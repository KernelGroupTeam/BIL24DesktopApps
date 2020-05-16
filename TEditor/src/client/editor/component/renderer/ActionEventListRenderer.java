package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.editor.ActionEventObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ActionEventListRenderer implements ListCellRenderer<ActionEventObj>, ElementToStringConverter<ActionEventObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends ActionEventObj> list, ActionEventObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(ActionEventObj value) {
    if (value == null) return "";
    return "[" + value.getId() + "][" + value.getPlanName() + "] " + value.getShowTime() +
        (value.isArchival() ? " Архивный" : (value.isSellEnabled() ? "" : " Отключен"));
  }
}
