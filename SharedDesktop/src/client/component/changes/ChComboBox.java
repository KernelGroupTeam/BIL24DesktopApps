package client.component.changes;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;

import client.component.suggestion.SuggestionComboBox;
import org.jdesktop.swingx.JXDatePicker;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 18.07.15
 */
public class ChComboBox<E> extends SuggestionComboBox<E> {
  private final ChComponentsDispatcher chComponentsDispatcher = new ChComponentsDispatcher(
      new ChComponentsListener() {
        @Override
        public void chStatusChanged(boolean edited) {
          ChComboBox.this.chStatusChanged(edited);
        }
      });
  private Border normalBorder;
  private Border editedBorder = new LineBorder(Color.RED);

  public ChComboBox(E[] items) {
    super(items);
    normalBorder = getBorder();
  }

  public ChComboBox(Vector<E> items) {
    super(items);
    normalBorder = getBorder();
  }

  public ChComboBox() {
    normalBorder = getBorder();
  }

  @Override
  public void setBorder(Border border) {
    super.setBorder(border);
    normalBorder = border;
  }

  public void setEditedBorder(Border border) {
    editedBorder = border;
  }

  public void listenChanges(JTextComponent textComponent) {
    chComponentsDispatcher.listenChanges(textComponent);
  }

  public void listenChanges(JFormattedTextField formattedTextField) {
    chComponentsDispatcher.listenChanges(formattedTextField);
  }

  public void listenChanges(AbstractButton abstractButton) {
    chComponentsDispatcher.listenChanges(abstractButton);
  }

  public void listenChanges(AbstractTableModel abstractTableModel) {
    chComponentsDispatcher.listenChanges(abstractTableModel);
  }

  public void listenChanges(AbstractListModel<?> abstractListModel) {
    chComponentsDispatcher.listenChanges(abstractListModel);
  }

  public <T> void listenChangesIgnoringOrder(AbstractListModel<T> abstractListModel, Comparator<T> comparator) {
    chComponentsDispatcher.listenChangesIgnoringOrder(abstractListModel, comparator);
  }

  public void listenChanges(JXDatePicker datePicker) {
    chComponentsDispatcher.listenChanges(datePicker);
  }

  public void listenChanges(JComboBox<?> comboBox) {
    chComponentsDispatcher.listenChanges(comboBox);
  }

  public void enableComponentChanges(Component component) {
    chComponentsDispatcher.enableComponentChanges(component);
  }

  public boolean isEdited() {
    return chComponentsDispatcher.isEdited();
  }

  public void markEdited() {
    if (getSelectedIndex() < 0) return;
    chComponentsDispatcher.markEdited();
  }

  protected void resetEdited() {
    chComponentsDispatcher.resetEdited();
  }

  protected void chComponentsClearValue() {
    chComponentsDispatcher.clearValue();
  }

  protected void chComponentsSetValue() {
    chComponentsDispatcher.setValue();
  }

  private void chStatusChanged(boolean edited) {
    if (edited) {
      if (getSelectedIndex() < 0) return;
      if (editedBorder != getBorder()) super.setBorder(editedBorder);
    } else {
      if (normalBorder != getBorder()) super.setBorder(normalBorder);
    }
  }
}
