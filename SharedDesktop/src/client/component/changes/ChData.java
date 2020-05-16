package client.component.changes;

import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 11.01.17
 */
class ChData {
  private final Class<?> objectClass;
  private final Object object;
  private Object value = null;
  private boolean valueSet = false;
  private Comparator<?> comparator = null;

  public ChData(JTextComponent textComponent) {
    this(JTextComponent.class, textComponent);
  }

  public ChData(JFormattedTextField formattedTextField) {
    this(JFormattedTextField.class, formattedTextField);
  }

  public ChData(FTFWrapper formattedTextField) {
    this(FTFWrapper.class, formattedTextField);
  }

  public ChData(AbstractButton abstractButton) {
    this(AbstractButton.class, abstractButton);
  }

  public ChData(AbstractTableModel abstractTableModel) {
    this(AbstractTableModel.class, abstractTableModel);
  }

  public <T> ChData(AbstractListModel<T> abstractListModel, Comparator<T> comparator) {
    this(AbstractListModel.class, abstractListModel);
    this.comparator = comparator;
  }

  public ChData(JXDatePicker datePicker) {
    this(JXDatePicker.class, datePicker);
  }

  public ChData(JComboBox<?> comboBox) {
    this(JComboBox.class, comboBox);
  }

  private ChData(Class<?> objectClass, Object object) {
    this.objectClass = objectClass;
    this.object = object;
  }

  public void clearValue() {
    value = null;
    valueSet = false;
  }

  public void setValue() {
    value = retrieveValue();
    valueSet = true;
  }

  public boolean isValueChanged() {
    if (!valueSet) return false;
    Object currentValue = retrieveValue();
    return !Objects.equals(value, currentValue);
  }

  private Object retrieveValue() {
    if (objectClass == JTextComponent.class) {
      return ((JTextComponent) object).getText();
    } else if (objectClass == JFormattedTextField.class) {
      return ((JFormattedTextField) object).getValue();
    } else if (objectClass == FTFWrapper.class) {
      return ((FTFWrapper) object).getValue();
    } else if (objectClass == AbstractButton.class) {
      return ((AbstractButton) object).isSelected();
    } else if (objectClass == AbstractTableModel.class) {
      AbstractTableModel model = (AbstractTableModel) object;
      Object[][] elements = new Object[model.getRowCount()][model.getColumnCount()];
      for (int row = 0; row < model.getRowCount(); row++) {
        for (int col = 0; col < model.getColumnCount(); col++) {
          elements[row][col] = model.getValueAt(row, col);
        }
      }
      return new Array2Value(elements);
    } else if (objectClass == AbstractListModel.class) {
      AbstractListModel<?> model = (AbstractListModel<?>) object;
      Object[] elements = new Object[model.getSize()];
      for (int i = 0; i < elements.length; i++) {
        elements[i] = model.getElementAt(i);
      }
      return new ArrayValue(elements, comparator);
    } else if (objectClass == JXDatePicker.class) {
      return ((JXDatePicker) object).getDate();
    } else if (objectClass == JComboBox.class) {
      return ((JComboBox<?>) object).getSelectedIndex();
    }
    return null;//не должно быть
  }

  private static class ArrayValue {
    private final Object[] value;//NotNull

    @SuppressWarnings("unchecked")
    public ArrayValue(Object[] value, Comparator<?> comparator) {
      if (comparator != null) Arrays.sort(value, (Comparator<? super Object>) comparator);
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ArrayValue)) return false;
      ArrayValue that = (ArrayValue) o;
      return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
      return Arrays.toString(value);
    }
  }

  private static class Array2Value {
    private final Object[][] value;//NotNull

    public Array2Value(Object[][] value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Array2Value)) return false;
      Array2Value that = (Array2Value) o;
      return Arrays.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Arrays.deepHashCode(value);
    }

    @Override
    public String toString() {
      StringBuilder str = new StringBuilder();
      for (Object[] objects : value) {
        str.append(Arrays.toString(objects)).append("\n");
      }
      return str.toString();
    }
  }
}
