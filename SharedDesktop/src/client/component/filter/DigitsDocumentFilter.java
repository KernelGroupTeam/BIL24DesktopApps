package client.component.filter;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.04.16
 */
public class DigitsDocumentFilter extends DocumentFilter {
  @Nullable
  private final Component errorFeedbackComponent;

  public DigitsDocumentFilter(@Nullable Component errorFeedbackComponent) {
    this.errorFeedbackComponent = errorFeedbackComponent;
  }

  @Override
  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
    string = string.replace(" ", "").replace("\r", "").replace("\n", "");
    if (isValid(string)) {
      super.insertString(fb, offset, string, attr);
    } else {
      UIManager.getLookAndFeel().provideErrorFeedback(errorFeedbackComponent);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    text = (text == null ? null : text.replace(" ", "").replace("\r", "").replace("\n", ""));
    if (isValid(text)) {
      super.replace(fb, offset, length, text, attrs);
    } else {
      UIManager.getLookAndFeel().provideErrorFeedback(errorFeedbackComponent);
    }
  }

  private boolean isValid(String s) {
    if (s == null) return true;
    for (int i = 0; i < s.length(); i++) {
      if (!Character.isDigit(s.charAt(i))) return false;
    }
    return true;
  }
}
