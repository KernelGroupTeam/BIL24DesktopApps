package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.common.CityObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class CityListRenderer implements ListCellRenderer<CityObj>, ElementToStringConverter<CityObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends CityObj> list, CityObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(CityObj value) {
    if (value == null) return "";
    if (value.getId() == 0) return value.getName();
    return "[" + value.getId() + "] " + value.getName();
  }
}
