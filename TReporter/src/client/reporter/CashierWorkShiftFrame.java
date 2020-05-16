/*
 * Created by JFormDesigner on Tue Sep 04 14:27:12 GMT 2018
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.WaitingDialog;
import client.net.*;
import client.reporter.component.renderer.CashierWorkShiftListRenderer;
import client.utils.Utils;
import org.jdesktop.swingx.JXDatePicker;
import org.jetbrains.annotations.NotNull;
import report.enums.EForm;
import report.reporter.managers.BuildManager;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Inventor
 */
public class CashierWorkShiftFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXDatePicker fromDatePicker;
  private JXDatePicker toDatePicker;
  private JList<RCashierWorkShift> cashierWorkShiftList;
  private JButton reportsButton;
  private JPopupMenu reportsPopupMenu;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final DateFormat requestFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  @NotNull
  private final Map<Long, List<OrderObj>> cashierWorkShiftOrderListMap = new HashMap<>();//key - идентификатор смены
  @NotNull
  private final DefaultListModel<RCashierWorkShift> cashierWorkShiftListModel = new DefaultListModel<>();
  @NotNull
  private final WaitingDialog waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

  public CashierWorkShiftFrame(Window owner) {
    initComponents();

    cashierWorkShiftList.setModel(cashierWorkShiftListModel);
    cashierWorkShiftList.setCellRenderer(new CashierWorkShiftListRenderer());
    reportsButton.setIcon(Env.excelIcon);

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

  private void getButtonActionPerformed() {
    Date date1 = fromDatePicker.getDate();
    if (date1 == null) {
      fromDatePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Не указан период", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Date date2 = toDatePicker.getDate();
    if (date2 == null) {
      toDatePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Не указан период", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    date2 = Utils.endOfDay(date2);
    if (date1.after(date2)) {
      date1 = toDatePicker.getDate();
      date2 = Utils.endOfDay(fromDatePicker.getDate());
    }
    Env.net.create("GET_CASHIER_WORK_SHIFT_LIST", new Request(new Object[]{requestFormat.format(date1), requestFormat.format(date2)}), this).start();
  }

  private void reportsButtonActionPerformed() {
    reportsPopupMenu.show(reportsButton, reportsButton.getWidth(), 0);
  }

  private void report12MenuItemActionPerformed() {
    RCashierWorkShift cashierWorkShift = cashierWorkShiftList.getSelectedValue();
    if (cashierWorkShift == null) return;
    List<OrderObj> orderList = cashierWorkShiftOrderListMap.get(cashierWorkShift.getId());
    if (orderList == null) {
      cashierWorkShiftRequest(new CashierWorkShiftNetListener(EForm.FORM_12, cashierWorkShift));
    } else {
      report(EForm.FORM_12, cashierWorkShift, orderList);
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label4 = new JLabel();
    JLabel label5 = new JLabel();
    fromDatePicker = new JXDatePicker(new Date());
    JLabel label6 = new JLabel();
    toDatePicker = new JXDatePicker(new Date());
    JPanel panel2 = new JPanel();
    JLabel label3 = new JLabel();
    JScrollPane scrollPane1 = new JScrollPane();
    cashierWorkShiftList = new JList<>();
    JPanel panel3 = new JPanel();
    JButton getButton = new JButton();
    reportsButton = new JButton();
    reportsPopupMenu = new JPopupMenu();
    JMenuItem report12MenuItem = new JMenuItem();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0421\u043c\u0435\u043d\u044b");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 200, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

      //---- label4 ----
      label4.setText("\u041f\u0435\u0440\u0438\u043e\u0434:");
      panel1.add(label4, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- label5 ----
      label5.setText("\u0441");
      panel1.add(label5, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- fromDatePicker ----
      fromDatePicker.setFormats("EEE dd.MM.yyyy");
      panel1.add(fromDatePicker, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- label6 ----
      label6.setText("\u043f\u043e");
      panel1.add(label6, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- toDatePicker ----
      toDatePicker.setFormats("EEE dd.MM.yyyy");
      panel1.add(toDatePicker, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.NORTH);

    //======== panel2 ========
    {
      panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel2.setLayout(new GridBagLayout());
      ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //---- label3 ----
      label3.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u0441\u043c\u0435\u043d:");
      panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== scrollPane1 ========
      {

        //---- cashierWorkShiftList ----
        cashierWorkShiftList.setVisibleRowCount(10);
        cashierWorkShiftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane1.setViewportView(cashierWorkShiftList);
      }
      panel2.add(scrollPane1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel2, BorderLayout.CENTER);

    //======== panel3 ========
    {
      panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel3.setLayout(new GridBagLayout());
      ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
      ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
      ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
      ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

      //---- getButton ----
      getButton.setText("\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c");
      getButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          getButtonActionPerformed();
        }
      });
      panel3.add(getButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- reportsButton ----
      reportsButton.setText("\u041e\u0442\u0447\u0435\u0442\u044b");
      reportsButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          reportsButtonActionPerformed();
        }
      });
      panel3.add(reportsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel3, BorderLayout.SOUTH);

    //======== reportsPopupMenu ========
    {

      //---- report12MenuItem ----
      report12MenuItem.setText("\u041e\u0442\u0447\u0435\u0442 \u211612");
      report12MenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/excel.png")));
      report12MenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          report12MenuItemActionPerformed();
        }
      });
      reportsPopupMenu.add(report12MenuItem);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void report(@NotNull EForm form, @NotNull RCashierWorkShift cashierWorkShift, @NotNull List<OrderObj> orderList) {
    BuildManager.setCashierWorkShift(cashierWorkShift);
    BuildManager.setOrderListCashierWorkShift(orderList);
    new ReportFilterDialog(CashierWorkShiftFrame.this, form).setVisible(true);
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

    if (result.getCommand().equals("GET_CASHIER_WORK_SHIFT_LIST")) {
      List<RCashierWorkShift> cashierWorkShiftList = (List<RCashierWorkShift>) result.getResponse().getData();
      cashierWorkShiftListModel.removeAllElements();
      for (RCashierWorkShift cashierWorkShift : cashierWorkShiftList) {
        cashierWorkShiftListModel.addElement(cashierWorkShift);
      }
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private static void cashierWorkShiftRequest(@NotNull CashierWorkShiftNetListener listener) {
    RCashierWorkShift cashierWorkShift = listener.getCashierWorkShift();
    RFrontend frontend = cashierWorkShift.getFrontendList().get(listener.getFrontendIndex());
    Object[] obj = new Object[14];
    obj[0] = false;
    obj[1] = cashierWorkShift.getStartDate();
    obj[2] = cashierWorkShift.getEndDate();
    obj[3] = true;
    obj[6] = cashierWorkShift.getAgent().getId();
    obj[7] = frontend.getId();
    Env.net.create("GET_ORDER_LIST_1", new Request(obj), listener, 60000).start();
  }

  private class CashierWorkShiftNetListener implements NetListener<Request, Response> {
    @NotNull
    private final EForm form;
    @NotNull
    private final RCashierWorkShift cashierWorkShift;
    @NotNull
    private final List<OrderObj> orderList = new ArrayList<>();
    private int frontendIndex = 0;

    private CashierWorkShiftNetListener(@NotNull EForm form, @NotNull RCashierWorkShift cashierWorkShift) {
      this.form = form;
      this.cashierWorkShift = cashierWorkShift;
    }

    @Override
    public final void netState(NetEvent<Request, Response> event, Network.State state) {
      if (state == Network.State.STARTED) waitingDialog.setVisible(true);
      if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(CashierWorkShiftFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      orderList.addAll((List<OrderObj>) result.getResponse().getData());

      List<RFrontend> frontendList = cashierWorkShift.getFrontendList();
      if (frontendIndex < frontendList.size() - 1) {
        frontendIndex++;
        cashierWorkShiftRequest(this);
      } else {
        cashierWorkShiftOrderListMap.put(cashierWorkShift.getId(), orderList);
        report(form, cashierWorkShift, orderList);
      }
    }

    @Override
    public final void netError(NetErrorEvent<Request, Response> error) {
      JOptionPane.showMessageDialog(CashierWorkShiftFrame.this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    @NotNull
    RCashierWorkShift getCashierWorkShift() {
      return cashierWorkShift;
    }

    int getFrontendIndex() {
      return frontendIndex;
    }
  }
}
