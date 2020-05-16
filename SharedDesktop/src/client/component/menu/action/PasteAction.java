package client.component.menu.action;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.06.16
 */
public class PasteAction extends AbstractAction {
  private JTextComponent comp;

  public PasteAction(JTextComponent comp) {
    super("Вставить");
    this.comp = comp;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    comp.paste();
  }

  @Override
  public boolean isEnabled() {
    if (comp.isEditable() && comp.isEnabled()) {
      Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
      return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
    return false;
  }
}
