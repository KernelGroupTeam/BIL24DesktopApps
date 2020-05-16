/*
 * Created by JFormDesigner on Mon Jul 20 17:35:33 MSK 2015
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
import server.protocol2.common.KindObj;
import server.protocol2.editor.*;

import static client.editor.Env.user;

/**
 * @author Maksim
 */
public class AddActionDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<EOrganizer> orgComboBox;
  private JComboBox<KindObj> kindIntComboBox;
  private JTextField actionTextField;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static EOrganizer organizer;
  private final OperationComboBox<ActionObj> actionComboBox;
  private WaitingDialog waitingDialog;

  public AddActionDialog(Window owner, OperationComboBox<ActionObj> actionComboBox, OperationComboBox<KindObj> kindComboBox) {
    super(owner);
    this.actionComboBox = actionComboBox;
    initComponents();

    if (user.getUserType() == UserType.OPERATOR) {
      for (EOrganizer organizer : Env.organizerList) {
        orgComboBox.addItem(organizer);
      }
    } else {
      if (organizer == null) organizer = new EOrganizer(user.getAuthorityId(), user.getAuthorityName());
      orgComboBox.addItem(organizer);
    }

    for (int i = 0; i < kindComboBox.getItemCount(); i++) {
      kindIntComboBox.addItem(kindComboBox.getItemAt(i));
    }
    kindIntComboBox.setSelectedIndex(kindComboBox.getSelectedIndex());

    pack();
    setLocationRelativeTo(getOwner());
    if (user.getUserType() == UserType.ORGANIZER) actionTextField.requestFocus();
  }

  private void okButtonActionPerformed() {
    if (orgComboBox.getSelectedIndex() == -1) {
      orgComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать организатора", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (kindIntComboBox.getSelectedIndex() == -1) {
      kindIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать раздел", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (actionTextField.getText().trim().isEmpty()) {
      actionTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Название представления не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
    Object[] data = {orgComboBox.getItemAt(orgComboBox.getSelectedIndex()).getId(), kindIntComboBox.getItemAt(kindIntComboBox.getSelectedIndex()), actionTextField.getText().trim()};
    Env.net.create("ADD_ACTION", new Request(data), this).start();
  }

  private void cancelButtonActionPerformed() {
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    orgComboBox = new SuggestionComboBox<>();
    JLabel label2 = new JLabel();
    kindIntComboBox = new JComboBox<>();
    JLabel label3 = new JLabel();
    actionTextField = new JTextField();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435...");
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 200, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u041e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(orgComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label2 ----
        label2.setText("\u0420\u0430\u0437\u0434\u0435\u043b:");
        contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(kindIntComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label3 ----
        label3.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
        contentPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));
        contentPanel.add(actionTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
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
    ActionObj actionObj = (ActionObj) result.getResponse().getData();
    actionObj.setChildless(true);
    actionComboBox.addElement(actionObj, true);
    this.dispose();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.isDataSent())
      JOptionPane.showMessageDialog(this, "Команда отправлена на сервер, но подтверждение не было получено", "Ошибка", JOptionPane.ERROR_MESSAGE);
    else
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить представление", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
