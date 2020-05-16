package client.component.changes;

import java.lang.reflect.Field;
import java.util.Objects;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 20.11.17
 */
class FTFWrapper {
  private final JFormattedTextField formattedTextField;

  public FTFWrapper(JFormattedTextField formattedTextField) {
    this.formattedTextField = formattedTextField;
  }

  FTFValue getValue() {
    return new FTFValue(formattedTextField.getValue(), formattedTextField.getText(), isEdited());
  }

  private boolean isEdited() {
    try {//рискованное место, если в будущих версиях java поле изменится, будет неправильно работать
      Field field = JFormattedTextField.class.getDeclaredField("edited");
      field.setAccessible(true);
      return field.getBoolean(formattedTextField);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      return false;
    }
  }

  static class FTFValue {
    private final Object value;
    private final String text;
    private final boolean edited;

    public FTFValue(Object value, String text, boolean edited) {
      this.value = value;
      this.text = text;
      this.edited = edited;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof FTFValue)) return false;
      FTFValue ftfValue = (FTFValue) o;
      if (edited || ftfValue.edited) {
        return Objects.equals(value, ftfValue.value) && Objects.equals(text, ftfValue.text);
      } else {
        return Objects.equals(value, ftfValue.value);
      }
    }

    @Override
    public int hashCode() {
      return value != null ? value.hashCode() : 0;
    }
  }
}
