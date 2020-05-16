/*
 * Created by JFormDesigner on Sun Dec 06 16:39:36 MSK 2015
 */

package client.component;

import java.awt.*;
import javax.swing.*;

/**
 * @author Maksim
 */
public class TextDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JTextArea textArea;
  // JFormDesigner - End of variables declaration  //GEN-END:variables


  public TextDialog(Window owner, String title, ModalityType modalityType) {
    super(owner, title, modalityType);
    initComponents();
  }

  public JTextArea getTextArea() {
    return textArea;
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JScrollPane scrollPane = new JScrollPane();
    textArea = new JTextArea();

    //======== this ========
    setType(Window.Type.UTILITY);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== scrollPane ========
    {
      scrollPane.setPreferredSize(new Dimension(1024, 384));

      //---- textArea ----
      textArea.setEditable(false);
      textArea.setMargin(new Insets(1, 3, 1, 3));
      textArea.setFont(UIManager.getFont("TextField.font"));
      scrollPane.setViewportView(textArea);
    }
    contentPane.add(scrollPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
}
