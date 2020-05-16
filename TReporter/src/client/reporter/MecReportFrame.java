/*
 * Created by JFormDesigner on Wed Mar 15 14:12:19 MSK 2017
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.WaitingDialog;
import client.component.suggestion.SuggestionComboBox;
import client.component.summary.JXSummaryTable;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.reporter.component.renderer.ActionListRenderer;
import client.reporter.model.MecTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Maksim
 */
public class MecReportFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<RAction> actionComboBox;
  private JButton getButton;
  private JXSummaryTable mecTable;
  private JLabel barLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final MecTableModel mecTableModel = new MecTableModel();
  private WaitingDialog waitingDialog;

  public MecReportFrame(Window owner, List<RAction> allActionList) {
    initComponents();

    for (RAction action : allActionList) {
      if (action.getKind().getId() == 1) {
        actionComboBox.addItem(action);//на первой итерации вызывается actionComboBoxItemStateChanged
      }
    }
    ActionListRenderer actionListRenderer = new ActionListRenderer(70);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);

    mecTable.setModel(mecTableModel);
    mecTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    mecTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) mecTableSelectionChanged();
      }
    });
    mecTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));
    if (Env.user.getUserType() == UserType.AGENT) {
      mecTable.getColumnExt(mecTableModel.getColumnName(2)).setVisible(false);
    }

    pack();
    setLocationRelativeTo(owner);
    mecTable.packAll();
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
      getButton.setEnabled(true);
    }
  }

  private void mecTableSelectionChanged() {
    int count = mecTable.getSelectedRowCount();
    if (mecTable.isRowSelected(mecTable.getRowCount() - 1)) count--;
    barLabel.setText("Выделено событий: " + count);
  }

  private void getButtonActionPerformed() {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    if (action == null) return;
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_PASS_LIST", new Request(new Object[]{action.getId(), null, Boolean.TRUE}), this).start();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    actionComboBox = new SuggestionComboBox<>();
    getButton = new JButton();
    JScrollPane scrollPane1 = new JScrollPane();
    mecTable = new JXSummaryTable();
    barLabel = new JLabel();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0423\u0441\u043f\u0435\u0448\u043d\u0430\u044f \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u044f \u043a\u0430\u0440\u0442");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 0, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u041c\u043e\u0431\u0438\u043b\u044c\u043d\u0430\u044f \u044d\u043b\u0435\u043a\u0442\u0440\u043e\u043d\u043d\u0430\u044f \u043a\u0430\u0440\u0442\u0430:");
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- actionComboBox ----
      actionComboBox.setMaximumRowCount(18);
      actionComboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          actionComboBoxItemStateChanged(e);
        }
      });
      panel1.add(actionComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- getButton ----
      getButton.setText("\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c");
      getButton.setEnabled(false);
      getButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          getButtonActionPerformed();
        }
      });
      panel1.add(getButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //======== scrollPane1 ========
      {

        //---- mecTable ----
        mecTable.setColumnControlVisible(true);
        mecTable.setHorizontalScrollEnabled(true);
        scrollPane1.setViewportView(mecTable);
      }
      panel1.add(scrollPane1, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.CENTER);

    //---- barLabel ----
    barLabel.setFont(barLabel.getFont().deriveFont(barLabel.getFont().getStyle() & ~Font.BOLD, barLabel.getFont().getSize() - 1f));
    barLabel.setText(" ");
    barLabel.setBorder(new EmptyBorder(0, 5, 1, 5));
    contentPane.add(barLabel, BorderLayout.SOUTH);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) waitingDialog.setVisible(true);
    if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    List<PassObj> passList = (List<PassObj>) result.getResponse().getData();
    mecTableModel.setData(passList);
    mecTable.packAll();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
