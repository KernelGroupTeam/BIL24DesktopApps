package client.utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.02.18
 */
public class ComponentClipboard {

  private ComponentClipboard() {
  }

  public static <T> void setJListCopyAction(@NotNull final JList<T> list, @NotNull final ElementToStringConverter<? super T> converter) {
    InputMap inputMap = list.getInputMap(JComponent.WHEN_FOCUSED);
    ActionMap actionMap = list.getActionMap();
    Action copyAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        StringBuilder str = new StringBuilder();
        for (T value : list.getSelectedValuesList()) {
          str.append(converter.stringValue(value)).append(System.lineSeparator());
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str.toString()), null);
      }
    };
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "copy");
    actionMap.put("copy", copyAction);
  }
}
