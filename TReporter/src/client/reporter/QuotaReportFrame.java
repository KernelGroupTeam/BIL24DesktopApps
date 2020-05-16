/*
 * Created by JFormDesigner on Wed Mar 15 14:12:19 MSK 2017
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.WaitingDialog;
import client.component.suggestion.SuggestionComboBox;
import client.net.*;
import client.reporter.component.renderer.ActionListRenderer;
import report.enums.EForm;
import report.exceptions.ExcelReportException;
import report.reporter.managers.BuildManager;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Maksim
 */
public class QuotaReportFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<RAction> actionComboBox;
  private JButton showButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private WaitingDialog waitingDialog;

  public QuotaReportFrame(Window owner, List<RAction> allActionList, List<RActionEvent> allActionEventList) {
    initComponents();

    Set<Long> actionIdSet = new HashSet<>();
    for (RActionEvent actionEvent : allActionEventList) {
      if (actionEvent.isQuota()) {
        actionIdSet.add(actionEvent.getActionId());
      }
    }
    for (RAction action : allActionList) {
      if (actionIdSet.contains(action.getId())) {
        actionComboBox.addItem(action);//на первой итерации вызывается actionComboBoxItemStateChanged
      }
    }
    ActionListRenderer actionListRenderer = new ActionListRenderer(70);
    actionListRenderer.setShowOrganizer(Env.user.getUserType() != UserType.ORGANIZER);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);
    showButton.setIcon(Env.excelIcon);

    pack();
    setLocationRelativeTo(owner);
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) {
      int state = getExtendedState();
      if ((state & ICONIFIED) != 0) setExtendedState(state & ~ICONIFIED);
    }
  }

  private void actionComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      showButton.setEnabled(true);
    }
  }

  private void showButtonActionPerformed() {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    if (action == null) return;
    BuildManager.setAction(action);
    BuildManager.setActionEventQuotaMap(null);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_REPORT_SALE_INFO", new Request(new Object[]{action.getId(), null, null}), this).start();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    actionComboBox = new SuggestionComboBox<>();
    JPanel panel3 = new JPanel();
    showButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041e\u0442\u0447\u0435\u0442 \u043f\u043e \u043f\u0440\u043e\u0434\u0430\u0436\u0430\u043c");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u041f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435:");
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- actionComboBox ----
      actionComboBox.setMaximumRowCount(18);
      actionComboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          actionComboBoxItemStateChanged(e);
        }
      });
      panel1.add(actionComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.NORTH);

    //======== panel3 ========
    {
      panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel3.setLayout(new GridBagLayout());
      ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
      ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
      ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

      //---- showButton ----
      showButton.setText("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043e\u0442\u0447\u0435\u0442");
      showButton.setEnabled(false);
      showButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showButtonActionPerformed();
        }
      });
      panel3.add(showButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel3, BorderLayout.SOUTH);
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

    ReportSaleInfo reportSaleInfo = (ReportSaleInfo) result.getResponse().getData();
    BuildManager.setReportSaleInfo(reportSaleInfo);
    try {
      BuildManager.build(EForm.FORM_9);
    } catch (IOException | ExcelReportException e) {
      JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
