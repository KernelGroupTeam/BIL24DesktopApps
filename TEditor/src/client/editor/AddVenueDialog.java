/*
 * Created by JFormDesigner on Sun Jul 19 18:52:20 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.*;
import client.component.suggestion.SuggestionComboBox;
import client.net.*;
import server.protocol2.*;
import server.protocol2.common.CityObj;
import server.protocol2.editor.VenueObj;

/**
 * @author Maksim
 */
public class AddVenueDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<CityObj> cityIntComboBox;
  private JTextField venueTextField;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final ChildlessControl childlessControl;
  private final OperationComboBox<VenueObj> venueComboBox;
  private WaitingDialog waitingDialog;

  public AddVenueDialog(Window owner, ChildlessControl childlessControl, OperationComboBox<VenueObj> venueComboBox, OperationComboBox<CityObj> cityComboBox) {
    super(owner);
    this.childlessControl = childlessControl;
    this.venueComboBox = venueComboBox;
    initComponents();

    for (int i = 0; i < cityComboBox.getItemCount(); i++) {
      cityIntComboBox.addItem(cityComboBox.getItemAt(i));
    }
    cityIntComboBox.setSelectedIndex(cityComboBox.getSelectedIndex());

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void okButtonActionPerformed() {
    if (cityIntComboBox.getSelectedIndex() == -1) {
      cityIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать город", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (venueTextField.getText().trim().isEmpty()) {
      venueTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Название места проведения не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
    Object[] data = {cityIntComboBox.getItemAt(cityIntComboBox.getSelectedIndex()).getId(), venueTextField.getText().trim()};
    Env.net.create("ADD_VENUE", new Request(data), this).start();
  }

  private void cancelButtonActionPerformed() {
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    cityIntComboBox = new SuggestionComboBox<>();
    JLabel label2 = new JLabel();
    venueTextField = new JTextField();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043c\u0435\u0441\u0442\u043e \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u044f...");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 300, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u0413\u043e\u0440\u043e\u0434:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(cityIntComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label2 ----
        label2.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
        contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));
        contentPanel.add(venueTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
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

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) waitingDialog.setVisible(true);
    if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    VenueObj venueObj = (VenueObj) result.getResponse().getData();
    venueObj.setChildless(true);
    venueComboBox.addElement(venueObj, true);
    childlessControl.updateChildless();
    this.dispose();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.isDataSent())
      JOptionPane.showMessageDialog(this, "Команда отправлена на сервер, но подтверждение не было получено", "Ошибка", JOptionPane.ERROR_MESSAGE);
    else
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить место проведения", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
