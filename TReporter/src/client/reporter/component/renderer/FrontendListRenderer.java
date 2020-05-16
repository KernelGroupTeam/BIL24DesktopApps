package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.reporter.RFrontend;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.08.2016
 */
public class FrontendListRenderer implements ListCellRenderer<RFrontend>, ElementToStringConverter<RFrontend> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final int charLimit;

  public FrontendListRenderer(int charLimit) {
    this.charLimit = charLimit;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends RFrontend> list, RFrontend value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    if (str.length() > charLimit) str = str.substring(0, charLimit) + "…";
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(RFrontend value) {
    if (value == null) return "";
    if (value.getId() == 0) return value.getName();
    return "[" + value.getId() + "] " + value.getName() + " [" + value.getType().getName() + ']';
  }
}
