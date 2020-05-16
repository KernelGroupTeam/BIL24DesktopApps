package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.common.GatewayObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class GatewayListRenderer implements ListCellRenderer<GatewayObj>, ElementToStringConverter<GatewayObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final boolean showOrganizer;

  public GatewayListRenderer(boolean showOrganizer) {
    this.showOrganizer = showOrganizer;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends GatewayObj> list, GatewayObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(GatewayObj value) {
    if (value == null) return "";
    String name = (showOrganizer ? value.getNameWithOrganizer() : value.getName());
    if (value.getId() == 0) return name;
    return "[" + value.getId() + "] " + name;
  }
}
