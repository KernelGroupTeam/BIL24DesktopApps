package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import org.jdesktop.swingx.renderer.*;
import server.protocol2.editor.ActionEventSync;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.17
 */
public class ActionEventSyncListRenderer implements ListCellRenderer<ActionEventSync>, ElementToStringConverter<ActionEventSync>, StringValue {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final boolean showOrganizer;

  public ActionEventSyncListRenderer(boolean showOrganizer) {
    this.showOrganizer = showOrganizer;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends ActionEventSync> list, ActionEventSync value, int index, boolean isSelected, boolean cellHasFocus) {
    return renderer.getListCellRendererComponent(list, stringValue(value), index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(ActionEventSync value) {
    StringBuilder str = new StringBuilder();
    if (value != null) {
      if (showOrganizer) str.append("[").append(value.getOrganizerName()).append("] ");
      if (value.getMismatchedPrice() == -1) str.append("[не синхронизирован] ");
      else {
        str.append("[по цене: ").append(value.getMismatchedPrice()).append("][прочее: ").append(value.getMismatchedSeat()).append("] ");
      }
      if (!value.isSellEnabled()) str.append("[Отключен] ");
      str.append(value.getShowTime()).append(" ").append(value.getActionName());
    }
    return str.toString();
  }

  @Override
  public String getString(Object value) {
    if (value instanceof ActionEventSync) {
      return stringValue((ActionEventSync) value);
    }
    return StringValues.TO_STRING.getString(value);
  }
}
