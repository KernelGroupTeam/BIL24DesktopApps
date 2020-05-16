/*
 * Created by JFormDesigner on Sun Dec 06 16:39:36 MSK 2015
 */

package client.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * @author Maksim
 */
public class ConsoleFrame extends JFrame {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JTextPane consoleTextPane;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final Style regularStyle;
  private final Style errorStyle;

  public ConsoleFrame() {
    initComponents();

    Style defStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    regularStyle = consoleTextPane.addStyle("regular", defStyle);
    errorStyle = consoleTextPane.addStyle("error", defStyle);
    StyleConstants.setForeground(errorStyle, new Color(0x7F0000));
  }

  public JTextPane getConsoleTextPane() {
    return consoleTextPane;
  }

  public Style getRegularStyle() {
    return regularStyle;
  }

  public Style getErrorStyle() {
    return errorStyle;
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JScrollPane scrollPane = new JScrollPane();
    consoleTextPane = new JTextPane();

    //======== this ========
    setType(Window.Type.UTILITY);
    setTitle("\u041a\u043e\u043d\u0441\u043e\u043b\u044c");
    setAlwaysOnTop(true);
    setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== scrollPane ========
    {
      scrollPane.setPreferredSize(new Dimension(1024, 384));

      //---- consoleTextPane ----
      consoleTextPane.setEditable(false);
      consoleTextPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
      scrollPane.setViewportView(consoleTextPane);
    }
    contentPane.add(scrollPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
}
