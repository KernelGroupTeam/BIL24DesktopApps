package client.component.menu.action;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.06.16
 */
public class CopyAction extends AbstractAction {
  private JTextComponent comp;

  public CopyAction(JTextComponent comp) {
    super("Копировать");
    this.comp = comp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    comp.copy();
  }

  @Override
  public boolean isEnabled() {
    return comp.isEnabled()
        && comp.getSelectedText() != null;
  }
}
