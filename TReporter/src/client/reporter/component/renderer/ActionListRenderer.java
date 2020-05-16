package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.reporter.RAction;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ActionListRenderer implements ListCellRenderer<RAction>, ElementToStringConverter<RAction> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final int charLimit;
  private final int orgLimit;
  private boolean showOrganizer;

  public ActionListRenderer(int charLimit) {
    this.charLimit = charLimit;
    this.orgLimit = charLimit / 3;
  }

  public boolean isShowOrganizer() {
    return showOrganizer;
  }

  public void setShowOrganizer(boolean showOrganizer) {
    this.showOrganizer = showOrganizer;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends RAction> list, RAction value, int index, boolean isSelected, boolean cellHasFocus) {
    Component component = renderer.getListCellRendererComponent(list, limitStringValue(value), index, isSelected, cellHasFocus);
    if (value != null && value.isSellEnd()) component.setFont(component.getFont().deriveFont(Font.PLAIN));
    return component;
  }

  private String limitStringValue(RAction value) {
    if (value == null) return "";
    if (value.getId() == 0) {
      String str = value.getName();
      if (str.length() > charLimit) str = str.substring(0, charLimit) + "…";
      return str;
    }
    String str = "";
    if (showOrganizer) {
      str = value.getOrganizerName();
      if (str.length() > orgLimit) str = str.substring(0, orgLimit) + "…";
      str = "[" + str + "]";
    }
    str += "[" + value.getId() + "] " + value.getName();
    if (str.length() > charLimit) str = str.substring(0, charLimit) + "…";
    return str;
  }

  @Override
  public String stringValue(RAction value) {
    if (value == null) return "";
    if (value.getId() == 0) return value.getName();
    String str = "";
    if (showOrganizer) str = "[" + value.getOrganizerName() + "]";
    str += "[" + value.getId() + "] " + value.getName();
    return str;
  }
}
