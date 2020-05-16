package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import org.jdesktop.swingx.renderer.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.common.SubsActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class SubsActionListRenderer implements ListCellRenderer<SubsActionObj>, ElementToStringConverter<SubsActionObj>, StringValue {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private boolean showOrganizer;

  public boolean isShowOrganizer() {
    return showOrganizer;
  }

  public void setShowOrganizer(boolean showOrganizer) {
    this.showOrganizer = showOrganizer;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends SubsActionObj> list, SubsActionObj value, int index, boolean isSelected, boolean cellHasFocus) {
    return renderer.getListCellRendererComponent(list, stringValue(value), index, isSelected, cellHasFocus);
  }

  @Override
  @NotNull
  public String stringValue(SubsActionObj value) {
    String str = "";
    if (value != null) {
      if (showOrganizer) str = "[" + value.getOrganizerName() + "]";
      str += "[" + value.getId() + "] " + value.getName();
    }
    return str;
  }

  @Override
  public String getString(Object value) {
    if (value instanceof SubsActionObj) {
      return stringValue((SubsActionObj) value);
    }
    return StringValues.TO_STRING.getString(value);
  }
}
