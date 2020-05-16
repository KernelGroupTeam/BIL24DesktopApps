package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.common.AcquiringObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.08.2016
 */
public class AcquiringListRenderer implements ListCellRenderer<AcquiringObj>, ElementToStringConverter<AcquiringObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends AcquiringObj> list, AcquiringObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(AcquiringObj value) {
    if (value == null) return "";
    if (value.getId() < 0) return value.getName();
    if (value.getId() == 0) return "Без эквайринга";
    return "[" + value.getId() + "] " + value.getName();
  }
}
