/*
 * Created by JFormDesigner on Fri Apr 12 13:27:53 MSK 2019
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.utils.Utils;
import org.jdesktop.swingx.JXDatePicker;
import org.jetbrains.annotations.*;

/**
 * @author Maksim
 */
public class AddActionEventLimitDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel actionLabel;
  private JRadioButton radioButton1;
  private JRadioButton radioButton2;
  private JXDatePicker limitDatePicker;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private boolean cancelled = true;
  @Nullable
  private Date result = null;

  public AddActionEventLimitDialog(Window owner, @NotNull String action) {
    super(owner);
    initComponents();

    if (action.length() > 80) action = action.substring(0, 80) + "…";
    actionLabel.setText(action);

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void radioButton1ItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      limitDatePicker.setEnabled(false);
    }
  }

  private void radioButton2ItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      limitDatePicker.setEnabled(true);
    }
  }

  private void okButtonActionPerformed() {
    if (radioButton1.isSelected()) {
      result = null;
    } else if (radioButton2.isSelected()) {
      result = limitDatePicker.getDate();
      if (result == null) {
        limitDatePicker.requestFocus();
        JOptionPane.showMessageDialog(this, "Необходимо указать дату", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      result = Utils.endOfDay(result);
    } else throw new IllegalStateException();
    cancelled = false;
    this.dispose();
  }

  private void cancelButtonActionPerformed() {
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    actionLabel = new JLabel();
    radioButton1 = new JRadioButton();
    radioButton2 = new JRadioButton();
    limitDatePicker = new JXDatePicker(new Date());
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043f\u043e\u0434\u043e\u0431\u043d\u044b\u0435 \u0441\u0435\u0430\u043d\u0441\u044b...");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
        contentPanel.add(actionLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- radioButton1 ----
        radioButton1.setText("\u0412\u0441\u0435 \u0441\u0435\u0430\u043d\u0441\u044b");
        radioButton1.setSelected(true);
        radioButton1.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            radioButton1ItemStateChanged(e);
          }
        });
        contentPanel.add(radioButton1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- radioButton2 ----
        radioButton2.setText("\u041d\u0435 \u043f\u043e\u0437\u0436\u0435");
        radioButton2.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            radioButton2ItemStateChanged(e);
          }
        });
        contentPanel.add(radioButton2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- limitDatePicker ----
        limitDatePicker.setEnabled(false);
        limitDatePicker.setFormats("EEE dd.MM.yyyy");
        contentPanel.add(limitDatePicker, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));
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

    //---- buttonGroup1 ----
    ButtonGroup buttonGroup1 = new ButtonGroup();
    buttonGroup1.add(radioButton1);
    buttonGroup1.add(radioButton2);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  public boolean isCancelled() {
    return cancelled;
  }

  @Nullable
  public Date getResult() {
    return result;
  }
}
