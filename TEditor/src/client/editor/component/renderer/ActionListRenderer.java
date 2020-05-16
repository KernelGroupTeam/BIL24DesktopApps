package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import org.jdesktop.swingx.renderer.*;
import server.protocol2.editor.ActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class ActionListRenderer implements ListCellRenderer<ActionObj>, ElementToStringConverter<ActionObj>, StringValue {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private boolean showOrganizer;

  public boolean isShowOrganizer() {
    return showOrganizer;
  }

  public void setShowOrganizer(boolean showOrganizer) {
    this.showOrganizer = showOrganizer;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends ActionObj> list, ActionObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(ActionObj value) {
    String str = "";
    if (value != null) {
      if (showOrganizer) str = "[" + value.getOrganizerName() + "]";
      str += "[" + value.getId() + "] " + value.getName();
    }
    return str;
  }

  @Override
  public String getString(Object value) {
    if (value instanceof ActionObj) {
      return stringValue((ActionObj) value);
    }
    return StringValues.TO_STRING.getString(value);
  }
}
