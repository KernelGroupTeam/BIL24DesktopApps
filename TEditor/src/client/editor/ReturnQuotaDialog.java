/*
 * Created by JFormDesigner on Tue Nov 22 16:59:26 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.utils.L10n;
import org.jdesktop.swingx.JXDatePicker;
import org.jetbrains.annotations.*;
import server.protocol2.editor.QuotaOutObj;

/**
 * @author Maksim
 */
public class ReturnQuotaDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JTextField numberTextField;
  private JXDatePicker datePicker;
  private JLabel quantityLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final long actionEventId;
  @NotNull
  private final Set<Long> seatIdSet;
  private final boolean allSeats;
  @Nullable
  private QuotaOutObj quota;

  public ReturnQuotaDialog(Window owner, long actionEventId, @NotNull Set<Long> seatIdSet) {//возврат выделенных мест
    super(owner);
    this.actionEventId = actionEventId;
    this.seatIdSet = seatIdSet;
    this.allSeats = false;
    initComponents();

    quantityLabel.setText("Вернуть " + L10n.pluralVal(seatIdSet.size(), "место", "места", "мест"));
    pack();
    setLocationRelativeTo(getOwner());
  }

  public ReturnQuotaDialog(Window owner, long actionEventId) {//возврат всех мест
    super(owner);
    this.actionEventId = actionEventId;
    this.seatIdSet = Collections.emptySet();
    this.allSeats = true;
    initComponents();

    quantityLabel.setText("Вернуть все места");
    pack();
    setLocationRelativeTo(getOwner());
  }

  private void okButtonActionPerformed() {
    String number = numberTextField.getText().trim();
    if (number.isEmpty()) {
      numberTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Номер накладной не указан", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Date date = datePicker.getDate();
    if (date == null) {
      datePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Дата накладной не указана", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (allSeats) quota = new QuotaOutObj(actionEventId, number, date);
    else {
      long[] eventSeatIds = new long[seatIdSet.size()];
      int i = 0;
      for (Long seatId : seatIdSet) {
        eventSeatIds[i] = seatId;
        i++;
      }
      quota = new QuotaOutObj(actionEventId, number, date, eventSeatIds);
    }
    this.dispose();
  }

  private void cancelButtonActionPerformed() {
    quota = null;
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    numberTextField = new JTextField();
    JLabel label2 = new JLabel();
    datePicker = new JXDatePicker(new Date());
    quantityLabel = new JLabel();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u043a\u0432\u043e\u0442\u0443...");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        //======== panel1 ========
        {
          panel1.setLayout(new GridBagLayout());
          ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
          ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label1 ----
          label1.setText("\u041d\u0430\u043a\u043b\u0430\u0434\u043d\u0430\u044f \u043d\u0430 \u0432\u043e\u0437\u0432\u0440\u0430\u0442 \u2116");
          panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- numberTextField ----
          numberTextField.setColumns(8);
          panel1.add(numberTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label2 ----
          label2.setText("\u043e\u0442");
          panel1.add(label2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- datePicker ----
          datePicker.setFormats("dd.MM.yyyy");
          panel1.add(datePicker, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));
        contentPanel.add(quantityLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new GridBagLayout());
        ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
        ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

        //---- okButton ----
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            okButtonActionPerformed();
          }
        });
        buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- cancelButton ----
        cancelButton.setText("\u041e\u0442\u043c\u0435\u043d\u0430");
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cancelButtonActionPerformed();
          }
        });
        buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Nullable
  public QuotaOutObj getQuota() {
    return quota;
  }
}
