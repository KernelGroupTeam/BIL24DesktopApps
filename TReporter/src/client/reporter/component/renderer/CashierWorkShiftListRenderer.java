package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import server.protocol2.reporter.RCashierWorkShift;

/**
 * Created by Inventor on 05.09.2018
 */
public class CashierWorkShiftListRenderer implements ListCellRenderer<RCashierWorkShift> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends RCashierWorkShift> list, RCashierWorkShift value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = "";
    if (value != null) str = "[" + value.getId() + "] " + value.getStartDate() + " - " + value.getEndDate();
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }
}
