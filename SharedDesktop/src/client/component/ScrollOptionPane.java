package client.component;

import java.awt.*;
import javax.swing.*;

import client.utils.Position;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.09.15
 */
public class ScrollOptionPane {

  private ScrollOptionPane() {
  }

  public static void showMessageDialog(Component parentComponent, String message, String title, int messageType) {
    showMessageDialog(parentComponent, message, title, messageType, false);
  }

  public static void showMessageDialog(Component parentComponent, String message, String title, int messageType, boolean modal) {
    showMessageDialog(parentComponent, message, title, messageType, modal, Position.DEFAULT);
  }

  public static void showMessageDialog(Component parentComponent, String message, String title, int messageType, boolean modal, Position position) {
    showMessageDialog(parentComponent, message, title, messageType, modal, null, position);
  }

  public static void showMessageDialog(Component parentComponent, String message, String title, int messageType,
                                       boolean modal, Dialog.ModalExclusionType modalExclusionType, Position position) {
    JScrollPane scrollPane = createScrollPane(parentComponent, message);
    JOptionPane pane = new JOptionPane(scrollPane, messageType);
    JDialog dialog = pane.createDialog(parentComponent, title);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setModal(modal);
    dialog.setModalExclusionType(modalExclusionType);
    dialog.setResizable(true);
    dialog.pack();
    Position.setLocationRelativeTo(dialog, parentComponent, position);
    dialog.setVisible(true);
  }

  public static int showConfirmDialog(Component parentComponent, String message, String title, int optionType) {
    return showConfirmDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE);
  }

  public static int showConfirmDialog(Component parentComponent, String message, String title, int optionType, int messageType) {
    return showConfirmDialog(parentComponent, message, title, optionType, messageType, Position.DEFAULT);
  }

  public static int showConfirmDialog(Component parentComponent, String message, String title, int optionType, int messageType, Position position) {
    JScrollPane scrollPane = createScrollPane(parentComponent, message);
    JOptionPane pane = new JOptionPane(scrollPane, messageType, optionType);
    JDialog dialog = pane.createDialog(parentComponent, title);
    dialog.setResizable(true);
    dialog.pack();
    Position.setLocationRelativeTo(dialog, parentComponent, position);
    dialog.setVisible(true);
    dialog.dispose();
    Object selectedValue = pane.getValue();
    if (selectedValue == null) return JOptionPane.CLOSED_OPTION;
    if (selectedValue instanceof Integer) return (Integer) selectedValue;
    return JOptionPane.CLOSED_OPTION;
  }

  private static JScrollPane createScrollPane(Component parentComponent, String message) {
    if (parentComponent == null) parentComponent = JOptionPane.getRootFrame();
    Rectangle gcBounds = parentComponent.getGraphicsConfiguration().getBounds();
    int maxHeight = (int) (gcBounds.height * 0.8);
    JTextArea textArea = new JTextArea();
    textArea.setFont(UIManager.getFont("TextField.font"));
    textArea.setMargin(new Insets(4, 4, 4, 4));
    textArea.setEditable(false);
    textArea.setText(message);
    JScrollPane scrollPane = new JScrollPane(textArea);
    Dimension scrollPreferredSize = scrollPane.getPreferredSize();
    if (scrollPreferredSize.height > maxHeight) {
      scrollPreferredSize.height = maxHeight;
      scrollPreferredSize.width = scrollPreferredSize.width + scrollPane.getVerticalScrollBar().getPreferredSize().width;
      scrollPane.setPreferredSize(scrollPreferredSize);
    }
    return scrollPane;
  }
}
