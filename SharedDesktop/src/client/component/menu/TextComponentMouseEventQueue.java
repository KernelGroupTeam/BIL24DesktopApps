package client.component.menu;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import client.component.menu.action.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
public class TextComponentMouseEventQueue extends EventQueue {
  @Override
  protected void dispatchEvent(AWTEvent event) {
    super.dispatchEvent(event);

    // interested only in mouse events
    if (!(event instanceof MouseEvent)) return;
    MouseEvent mouseEvent = (MouseEvent) event;

    // interested only in popup triggers
    if (!mouseEvent.isPopupTrigger()) return;

    // mouseEvent.getComponent(...) returns the heavy weight component on which event occurred
    Component comp = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());

    // interested only in text components
    if (!(comp instanceof JTextComponent)) return;

    // no popup shown by user code
    if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) return;

    // create popup menu and show
    JTextComponent tc = (JTextComponent) comp;
    JPopupMenu menu = new JPopupMenu();
    menu.add(new CutAction(tc));
    menu.add(new CopyAction(tc));
    menu.add(new PasteAction(tc));
    menu.add(new DeleteAction(tc));
    menu.addSeparator();
    menu.add(new SelectAllAction(tc));

    Point pt = SwingUtilities.convertPoint(mouseEvent.getComponent(), mouseEvent.getPoint(), tc);
    menu.show(tc, pt.x, pt.y);
  }
}
