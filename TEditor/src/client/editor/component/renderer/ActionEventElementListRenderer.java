package client.editor.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.editor.component.ActionEventElement;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public class ActionEventElementListRenderer implements ListCellRenderer<ActionEventElement> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends ActionEventElement> list, ActionEventElement value, int index, boolean isSelected, boolean cellHasFocus) {
    Component component = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    ((JLabel) component).setHorizontalAlignment(JLabel.RIGHT);
    return component;
  }
}
