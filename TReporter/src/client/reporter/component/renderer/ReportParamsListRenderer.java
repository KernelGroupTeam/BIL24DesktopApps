package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import server.protocol2.reporter.ReportParamsObj;

/**
 * Created by Inventor on 23.01.2018
 */
public class ReportParamsListRenderer implements ListCellRenderer<ReportParamsObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends ReportParamsObj> list, ReportParamsObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = "";
    if (value != null) {
      str = (value.isExpired() ? "[Истекла] " : "") + value.getName() + (!value.isAllowed() ? " [Отключена]" : "") + (value.isDeficient() ? " [Неполная]" : "");
    }
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }
}
