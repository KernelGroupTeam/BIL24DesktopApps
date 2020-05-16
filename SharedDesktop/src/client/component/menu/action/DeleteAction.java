package client.component.menu.action;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.06.16
 */
public class DeleteAction extends AbstractAction {
  private JTextComponent comp;

  public DeleteAction(JTextComponent comp) {
    super("Удалить");
    this.comp = comp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    comp.replaceSelection(null);
  }

  @Override
  public boolean isEnabled() {
    return comp.isEditable()
        && comp.isEnabled()
        && comp.getSelectedText() != null;
  }
}
