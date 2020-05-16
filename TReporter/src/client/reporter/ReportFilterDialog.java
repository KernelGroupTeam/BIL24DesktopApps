/*
 * Created by JFormDesigner on Mon Dec 25 11:18:39 GMT 2017
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
import client.net.*;
import org.jetbrains.annotations.NotNull;
import report.enums.EForm;
import report.exceptions.*;
import report.models.*;
import report.reporter.managers.*;
import server.protocol2.*;
import server.protocol2.reporter.*;

import static client.reporter.Env.net;

/**
 * @author Inventor
 */
public class ReportFilterDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JCheckBox chargeCheckBox;
  private JCheckBox discountCheckBox;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  @NotNull
  private final EForm form;
  @NotNull
  private final WaitingDialog waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

  public ReportFilterDialog(@NotNull Frame owner, @NotNull EForm form) {
    initComponents();

    this.form = form;

    pack();
    setLocationRelativeTo(owner);
  }

  private void showButtonActionPerformed() {
    BuildManager.setCharge(chargeCheckBox.isSelected());
    BuildManager.setDiscount(discountCheckBox.isSelected());

    if (form == EForm.FORM_8) {
      List<OrderObj> orderList = BuildManager.getOrderListFilter();

      if (orderList == null || orderList.isEmpty()) {
        //Если список заказов пустой то сразу формируем отчет
        BuildManager.setActionEventSeatMap(Collections.<Long, List<EventSeatObj>>emptyMap());
        report();
        return;
      }

      //Формируем список сеансов по которым надо получить данные по местам
      Set<Long> actionEventIdSet = new HashSet<>();
      for (OrderObj order : orderList) {
        for (TicketObj ticket : order.getTicketList()) {
          Long actionEventId = ticket.getActionEvent().getId();
          actionEventIdSet.add(actionEventId);
        }
      }

      net.create("GET_ACTION_EVENT_SEAT_MAP", new Request(actionEventIdSet), this, 100000).start();
    } else if (form == EForm.FORM_17 || form == EForm.FORM_22) {
      Filter filter;
      Period period;
      try {
        filter = FilterManager.getFilter();
        if (filter.isFullReport()) period = Period.create(BuildManager.getOrderListFilter());
        else {
          period = filter.getPeriod();
          if (period == null) throw ValidationException.absent("Период");
        }
      } catch (ValidationException e) {
        JOptionPane.showMessageDialog(this, "Не удалось сформировать отчет: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }

      Object[] obj = new Object[12];
      obj[0] = period.getFromFormat();
      obj[1] = period.getToFormat();
      if (!Filter.DEF_ACQUIRING.equals(filter.getAcquiring())) obj[2] = filter.getAcquiring().getId();
      if (!Filter.DEF_ORGANIZER.equals(filter.getOrganizer())) obj[3] = filter.getOrganizer().getId();
      if (!Filter.DEF_AGENT.equals(filter.getAgent())) obj[4] = filter.getAgent().getId();
      if (!Filter.DEF_FRONTEND.equals(filter.getFrontend())) obj[5] = filter.getFrontend().getId();
      if (!Filter.DEF_CITY.equals(filter.getCity())) obj[6] = filter.getCity().getId();
      if (!Filter.DEF_VENUE.equals(filter.getVenue())) obj[7] = filter.getVenue().getId();
      if (!Filter.DEF_ACTION.equals(filter.getAction())) obj[8] = filter.getAction().getId();
      if (!Filter.DEF_ACTION_EVENT.equals(filter.getActionEvent())) obj[9] = filter.getActionEvent().getId();
      if (!Filter.DEF_GATEWAY.equals(filter.getGateway())) obj[10] = filter.getGateway().getId();
      if (!Filter.DEF_SYSTEM.equals(filter.getSystem())) obj[11] = filter.getSystem().getId();

      net.create("GET_REFUND_TICKET_LIST", new Request(obj), this, 60000).start();
    } else {
      report();
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    chargeCheckBox = new JCheckBox();
    discountCheckBox = new JCheckBox();
    JPanel panel3 = new JPanel();
    JButton showButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043e\u0442\u0447\u0435\u0442\u0430");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {250, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

      //---- chargeCheckBox ----
      chargeCheckBox.setText("\u0441 \u0443\u0447\u0435\u0442\u043e\u043c \u0441\u0435\u0440\u0432\u0438\u0441\u043d\u043e\u0433\u043e \u0441\u0431\u043e\u0440\u0430");
      panel1.add(chargeCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- discountCheckBox ----
      discountCheckBox.setText("\u0441 \u0443\u0447\u0435\u0442\u043e\u043c \u0441\u043a\u0438\u0434\u043a\u0438");
      panel1.add(discountCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
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

  @SuppressWarnings("unchecked")
  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (result.getCommand().equals("GET_ACTION_EVENT_SEAT_MAP")) {
      Map<Long, List<EventSeatObj>> actionEventSeatMap = (Map<Long, List<EventSeatObj>>) result.getResponse().getData();
      BuildManager.setActionEventSeatMap(actionEventSeatMap);
    } else if (result.getCommand().equals("GET_REFUND_TICKET_LIST")) {
      List<TicketObj> refundTicketList = (List<TicketObj>) result.getResponse().getData();
      BuildManager.setRefundTicketListFilter(refundTicketList);
    }

    report();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private void report() {
    try {
      BuildManager.build(form);
      dispose();
    } catch (IOException | ExcelReportException e) {
      JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }
}
