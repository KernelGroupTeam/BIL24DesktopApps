package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.editor.SubsAgentObj;


/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 26.02.17
 */
public class AgentListRenderer implements ListCellRenderer<SubsAgentObj>, ElementToStringConverter<SubsAgentObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends SubsAgentObj> list, SubsAgentObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(SubsAgentObj value) {
    if (value == null) return "";
    return "[" + value.getId() + "] " + value.getName();
  }
}
