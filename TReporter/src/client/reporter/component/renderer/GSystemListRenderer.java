package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.common.GSystemObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.08.2016
 */
public class GSystemListRenderer implements ListCellRenderer<GSystemObj>, ElementToStringConverter<GSystemObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends GSystemObj> list, GSystemObj value, int index, boolean isSelected, boolean cellHasFocus) {
    return renderer.getListCellRendererComponent(list, stringValue(value), index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(GSystemObj value) {
    if (value == null) return "";
    if (value.getId() < 0) return value.getName();
    if (value.getId() == 0) return "Без шлюза в ВБС";
    return "[" + value.getId() + "] " + value.getName();
  }
}
