package client.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXBusyLabel;

public class WaitingDialog extends JDialog {

  public WaitingDialog(Window owner, ModalityType modalityType) {
    super(owner, modalityType);
    initComponents();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JXBusyLabel busyLabel = new JXBusyLabel();

    //======== this ========
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setResizable(false);
    setTitle("\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435...");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //---- busyLabel ----
    busyLabel.setText("\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u0435 \u0437\u0430\u043f\u0440\u043e\u0441\u0430...");
    busyLabel.setBusy(true);
    busyLabel.setBorder(new EmptyBorder(15, 12, 15, 12));
    contentPane.add(busyLabel, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void setVisible(boolean b) {
    if (b) setLocationRelativeTo(getOwner());
    super.setVisible(b);
  }
}
