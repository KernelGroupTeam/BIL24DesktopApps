package client.editor.component.listener;

import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.02.16
 */
public class ReorderListListener<E> extends MouseInputAdapter {
  @NotNull
  private final JList<E> list;
  @NotNull
  private final DefaultListModel<E> listModel;
  private boolean mouseDragging = false;
  private int dragSourceIndex;

  public ReorderListListener(@NotNull JList<E> list) {
    this.list = list;
    ListModel<E> model = list.getModel();
    if (!(model instanceof DefaultListModel)) {
      throw new IllegalArgumentException("List must has a DefaultListModel");
    }
    this.listModel = (DefaultListModel<E>) model;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      dragSourceIndex = list.getSelectedIndex();
      mouseDragging = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseDragging = false;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (mouseDragging) {
      int currentIndex = list.locationToIndex(e.getPoint());
      if (currentIndex != dragSourceIndex) {
        int dragTargetIndex = list.getSelectedIndex();
        E dragElement = listModel.get(dragSourceIndex);
        listModel.remove(dragSourceIndex);
        listModel.add(dragTargetIndex, dragElement);
        dragSourceIndex = currentIndex;
      }
    }
  }
}
