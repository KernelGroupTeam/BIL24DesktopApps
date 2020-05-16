package client.component.changes;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 19.11.17
 */
public class ChComponentsDispatcher {
  private final ChComponentsListener chComponentsListener;
  private final ArrayList<ChData> chDataList = new ArrayList<>();
  private ChDocumentListener chDocumentListener;
  private ChFTFChangeListener chFTFChangeListener;
  private ChPropertyChangeListener chPropertyChangeListener;
  private ChItemListener chItemListener;
  private ChTableModelListener chTableModelListener;
  private ChListDataListener chListDataListener;
  private ChDatePicker chDatePicker;
  private ArrayList<Component> enableList;

  private boolean edited = false;//были ли изменения, установленные вручную
  private boolean componentEdited = false;//были ли изменения в слушаемых компонентах

  public ChComponentsDispatcher() {
    this.chComponentsListener = null;
  }

  public ChComponentsDispatcher(ChComponentsListener chComponentsListener) {
    this.chComponentsListener = chComponentsListener;
  }

  public void listenChanges(JTextComponent textComponent) {
    if (chDocumentListener == null) chDocumentListener = new ChDocumentListener();
    textComponent.getDocument().addDocumentListener(chDocumentListener);
    chDataList.add(new ChData(textComponent));
  }

  public void listenChanges(JFormattedTextField formattedTextField) {
    if (chFTFChangeListener == null) chFTFChangeListener = new ChFTFChangeListener();
    formattedTextField.addPropertyChangeListener("value", chFTFChangeListener);
    formattedTextField.getDocument().addDocumentListener(chFTFChangeListener);
    chDataList.add(new ChData(new FTFWrapper(formattedTextField)));
  }

  public void listenChanges(AbstractButton abstractButton) {
    if (chItemListener == null) chItemListener = new ChItemListener();
    abstractButton.addItemListener(chItemListener);
    chDataList.add(new ChData(abstractButton));
  }

  public void listenChanges(AbstractTableModel abstractTableModel) {
    if (chTableModelListener == null) chTableModelListener = new ChTableModelListener();
    abstractTableModel.addTableModelListener(chTableModelListener);
    chDataList.add(new ChData(abstractTableModel));
  }

  public void listenChanges(AbstractListModel<?> abstractListModel) {
    listenChangesIgnoringOrder(abstractListModel, null);
  }

  public <T> void listenChangesIgnoringOrder(AbstractListModel<T> abstractListModel, Comparator<T> comparator) {
    if (chListDataListener == null) chListDataListener = new ChListDataListener();
    abstractListModel.addListDataListener(chListDataListener);
    chDataList.add(new ChData(abstractListModel, comparator));
  }

  public void listenChanges(JXDatePicker datePicker) {
    if (chDatePicker == null) chDatePicker = new ChDatePicker();
    datePicker.getEditor().addPropertyChangeListener("value", chDatePicker);
    chDataList.add(new ChData(datePicker));
  }

  public void listenChanges(JComboBox<?> comboBox) {
    if (chItemListener == null) chItemListener = new ChItemListener();
    comboBox.addItemListener(chItemListener);
    chDataList.add(new ChData(comboBox));
  }

  public void enableComponentChanges(Component component) {
    if (enableList == null) enableList = new ArrayList<>();
    component.setEnabled(false);
    enableList.add(component);
  }

  /**
   * @return Состояние изменено вручную или связанные компоненты
   */
  public boolean isEdited() {
    return edited || componentEdited;
  }

  /**
   * Включить состояние изменено вручную
   */
  public void markEdited() {
    boolean oldValue = isEdited();
    edited = true;
    chStatusChanged(oldValue);
  }

  /**
   * Выключить состояние изменено вручную
   */
  public void resetEdited() {
    boolean oldValue = isEdited();
    edited = false;
    chStatusChanged(oldValue);
  }

  /**
   * Очистить состояние связанных компонентов
   */
  public void clearValue() {
    boolean oldValue = isEdited();
    for (ChData chData : chDataList) {
      chData.clearValue();
    }
    componentEdited = false;
    chStatusChanged(oldValue);
  }

  /**
   * Принять состояние связанных компонентов за начальное
   */
  public void setValue() {
    boolean oldValue = isEdited();
    for (ChData chData : chDataList) {
      chData.setValue();
    }
    componentEdited = false;
    chStatusChanged(oldValue);
  }

  private void changed() {
    boolean oldValue = isEdited();
    componentEdited = false;
    for (ChData chData : chDataList) {
      componentEdited = chData.isValueChanged();
      if (componentEdited) break;
    }
    chStatusChanged(oldValue);
  }

  private void chStatusChanged(boolean oldEditedValue) {
    boolean newEditedValue = isEdited();
    if (oldEditedValue != newEditedValue) {
      if (enableList != null) {
        for (Component component : enableList) {
          component.setEnabled(newEditedValue);
        }
      }
      if (chComponentsListener != null) chComponentsListener.chStatusChanged(newEditedValue);
    }
  }

  private class ChDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      changed();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      changed();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      changed();
    }
  }

  private class ChPropertyChangeListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) changed();
    }
  }

  private class ChFTFChangeListener implements PropertyChangeListener, DocumentListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      changed();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          changed();//выполнить после того, как в JFormattedTextField отработает processFocusEvent и внутренний DocumentListener и поставит верный edited
        }
      });
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          changed();//выполнить после того, как в JFormattedTextField отработает processFocusEvent и внутренний DocumentListener и поставит верный edited
        }
      });
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
  }

  private class ChItemListener implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      changed();
    }
  }

  private class ChTableModelListener implements TableModelListener {
    @Override
    public void tableChanged(TableModelEvent e) {
      changed();
    }
  }

  private class ChListDataListener implements ListDataListener {
    @Override
    public void intervalAdded(ListDataEvent e) {
      changed();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
      changed();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
      changed();
    }
  }

  private class ChDatePicker implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      changed();
    }
  }
}
