package client.component.menu.action;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.06.16
 */
public class SelectAllAction extends AbstractAction {
  private JTextComponent comp;

  public SelectAllAction(JTextComponent comp) {
    super("Выделить все");
    this.comp = comp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    comp.selectAll();
  }

  @Override
  public boolean isEnabled() {
    return comp.isEnabled()
        && !comp.getText().isEmpty();
  }
}
