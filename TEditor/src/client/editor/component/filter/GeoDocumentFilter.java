package client.editor.component.filter;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 03.08.15
 */
public class GeoDocumentFilter extends DocumentFilter {
  public static final String validCharacters = "0123456789,.-+";
  @Nullable
  private final Component errorFeedbackComponent;

  public GeoDocumentFilter(@Nullable Component errorFeedbackComponent) {
    this.errorFeedbackComponent = errorFeedbackComponent;
  }

  @Override
  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
    if (isValid(string)) {
      string = string.replace(',', '.');
      super.insertString(fb, offset, string, attr);
    } else {
      UIManager.getLookAndFeel().provideErrorFeedback(errorFeedbackComponent);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    if (isValid(text)) {
      text = (text == null ? null : text.replace(',', '.'));
      super.replace(fb, offset, length, text, attrs);
    } else {
      UIManager.getLookAndFeel().provideErrorFeedback(errorFeedbackComponent);
    }
  }

  private boolean isValid(String s) {
    if (s == null) return true;
    for (int i = 0; i < s.length(); i++) {
      if (validCharacters.indexOf(s.charAt(i)) == -1) return false;
    }
    return true;
  }
}
