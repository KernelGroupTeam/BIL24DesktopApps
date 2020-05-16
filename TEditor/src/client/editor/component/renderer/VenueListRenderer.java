package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.editor.VenueObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class VenueListRenderer implements ListCellRenderer<VenueObj>, ElementToStringConverter<VenueObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends VenueObj> list, VenueObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(VenueObj value) {
    if (value == null) return "";
    return "[" + value.getId() + "] " + value.getName();
  }
}
