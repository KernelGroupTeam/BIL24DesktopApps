/*
 * Created by JFormDesigner on Thu Jan 18 10:05:07 GMT 2018
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.WaitingDialog;
import client.net.*;
import client.reporter.component.renderer.ReportParamsListRenderer;
import org.jetbrains.annotations.NotNull;
import report.enums.*;
import report.exceptions.*;
import report.forms.AForm;
import report.models.*;
import report.reporter.enums.*;
import report.reporter.managers.BuildManager;
import report.reporter.models.BuildValues;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Inventor
 */
public class ReportParamsFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JList<ReportParamsObj> reportParamsList;
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton showButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  @NotNull
  private final Map<Long, AForm<?>> reportMap = new HashMap<>();
  @NotNull
  private final DefaultListModel<ReportParamsObj> reportParamsListModel = new DefaultListModel<>();
  @NotNull
  private WaitingDialog waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

  public ReportParamsFrame(Window owner) {
    initComponents();

    reportParamsList.setModel(reportParamsListModel);
    reportParamsList.setCellRenderer(new ReportParamsListRenderer());
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
      updateButtonActionPerformed();
    }
  }

  private void updateButtonActionPerformed() {
    Env.net.create("GET_REPORT_PARAMS", new Request(null), this).start();
  }

  private void addButtonActionPerformed() {
    AForm<?> report = BuildManager.getReport();
    if (report == null) {
      JOptionPane.showMessageDialog(this, "Необходимо сформировать отчет", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    new AddReportParamsDialog(this, report, reportMap, reportParamsListModel).setVisible(true);
  }

  private void editButtonActionPerformed() {
    ReportParamsObj reportParams = reportParamsList.getSelectedValue();
    if (reportParams == null) return;
    EForm form = EForm.getForm(reportParams.getFormId());
    new AddReportParamsDialog(this, form, reportParams, reportMap, reportParamsListModel).setVisible(true);
  }

  private void deleteButtonActionPerformed() {
    ReportParamsObj reportParams = reportParamsList.getSelectedValue();
    if (reportParams == null) return;
    Env.net.create("DEL_REPORT_PARAMS", new Request(reportParams.getId()), this).start();
  }

  private void showButtonActionPerformed() {
    ReportParamsObj reportParams = reportParamsList.getSelectedValue();
    if (reportParams == null) return;
    AForm<?> report = reportMap.get(reportParams.getId());
    if (report == null) {
      EForm form = EForm.getForm(reportParams.getFormId());
      switch (form.getType()) {
        case FILTER: {
          ReportParamsFilterObj reportParamsFilter = (ReportParamsFilterObj) reportParams;
          FilterObj filterObj = reportParamsFilter.getFilter();
          Filter filter;
          try {
            filter = new Filter(filterObj.getDateFrom(), filterObj.getDateTo(), filterObj.getPeriodTypeId(), filterObj.getAcquiring(),
                filterObj.getOrganizer(), filterObj.getCity(), filterObj.getVenue(), filterObj.getAction(), filterObj.getActionEvent(),
                filterObj.getAgent(), filterObj.getFrontend(), filterObj.getSystem(), filterObj.getGateway(), filterObj.isFullReport(), filterObj.isAllStatuses());
          } catch (ExcelReportException e) {
            JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (!filter.isFullReport()) {
            Period period = filter.getPeriod();
            if (period == null) return;//Не должно возникать, т.к. это уже проверено в конструкторе filter
            if (filter.getPeriodType() == null) return;//Не должно возникать, т.к. это уже проверено в конструкторе filter
            Object[] obj = new Object[14];
            obj[0] = false;
            obj[1] = period.getFromFormat();
            obj[2] = period.getToFormat();
            obj[3] = filter.isAllStatuses();
            if (!Filter.DEF_ACQUIRING.equals(filter.getAcquiring())) obj[4] = filter.getAcquiring().getId();
            if (!Filter.DEF_ORGANIZER.equals(filter.getOrganizer())) obj[5] = filter.getOrganizer().getId();
            if (!Filter.DEF_AGENT.equals(filter.getAgent())) obj[6] = filter.getAgent().getId();
            if (!Filter.DEF_FRONTEND.equals(filter.getFrontend())) obj[7] = filter.getFrontend().getId();
            if (!Filter.DEF_CITY.equals(filter.getCity())) obj[8] = filter.getCity().getId();
            if (!Filter.DEF_VENUE.equals(filter.getVenue())) obj[9] = filter.getVenue().getId();
            if (!Filter.DEF_ACTION.equals(filter.getAction())) obj[10] = filter.getAction().getId();
            if (!Filter.DEF_ACTION_EVENT.equals(filter.getActionEvent())) obj[11] = filter.getActionEvent().getId();
            if (!Filter.DEF_GATEWAY.equals(filter.getGateway())) obj[12] = filter.getGateway().getId();
            if (!Filter.DEF_SYSTEM.equals(filter.getSystem())) obj[13] = filter.getSystem().getId();
            if (filter.getPeriodType() == EPeriodType.SALES) {
              Env.net.create("GET_ORDER_LIST_1", new Request(obj), new ReportFilterNetListener(form, reportParamsFilter, filter), 60000).start();
            } else if (filter.getPeriodType() == EPeriodType.SHOWS) {
              Env.net.create("GET_ORDER_LIST_2", new Request(obj), new ReportFilterNetListener(form, reportParamsFilter, filter), 60000).start();
            }
          } else {
            Object[] obj = new Object[8];
            obj[0] = false;
            obj[1] = filter.getAction().getId();
            if (!Filter.DEF_ACTION_EVENT.equals(filter.getActionEvent())) obj[2] = filter.getActionEvent().getId();
            obj[3] = filter.isAllStatuses();//не используется
            if (!Filter.DEF_ACQUIRING.equals(filter.getAcquiring())) obj[4] = filter.getAcquiring().getId();
            if (!Filter.DEF_ORGANIZER.equals(filter.getOrganizer())) obj[5] = filter.getOrganizer().getId();
            if (!Filter.DEF_AGENT.equals(filter.getAgent())) obj[6] = filter.getAgent().getId();
            if (!Filter.DEF_FRONTEND.equals(filter.getFrontend())) obj[7] = filter.getFrontend().getId();
            Env.net.create("GET_ORDER_LIST_3", new Request(obj), new ReportFilterNetListener(form, reportParamsFilter, filter), 60000).start();
          }
          break;
        }
        case QUOTA_SALE: {
          ReportParamsQuotaSaleObj reportParamsQuotaSale = (ReportParamsQuotaSaleObj) reportParams;
          Object[] obj = new Object[3];
          obj[0] = reportParamsQuotaSale.getAction().getId();
          obj[1] = reportParamsQuotaSale.getActionEventQuotaMap(EInvoiceType.IN.getId());
          obj[2] = reportParamsQuotaSale.getActionEventQuotaMap(EInvoiceType.OUT.getId());
          Env.net.create("GET_REPORT_SALE_INFO", new Request(obj), new ReportQuotaSaleNetListener(form, reportParamsQuotaSale), 60000).start();
          break;
        }
        case QUOTA: {
          ReportParamsQuotaObj reportParamsQuota = (ReportParamsQuotaObj) reportParams;
          Object[] obj = new Object[8];
          obj[0] = false;
          obj[1] = reportParamsQuota.getAction().getId();
          Env.net.create("GET_ORDER_LIST_3", new Request(obj), new ReportQuotaNetListener(form, reportParamsQuota), 60000).start();
          break;
        }
        case INVOICE: {
          ReportParamsInvoiceObj reportParamsInvoice = (ReportParamsInvoiceObj) reportParams;
          EInvoiceType invoiceType = EInvoiceType.getInvoiceTypeById(form.getId());
          if (invoiceType == null) return;//Не должно возникать, т.к. идентификатор типа накладных равен идентификатору формы
          Env.net.create("GET_QUOTA_ACTION", new Request(reportParamsInvoice.getActionId()), new ReportInvoiceNetListener(form, reportParamsInvoice, invoiceType), 60000).start();
          break;
        }
        case CASHIER_WORK_SHIFT: {
          ReportParamsCashierWorkShiftObj reportParamsCashierWorkShift = (ReportParamsCashierWorkShiftObj) reportParams;
          cashierWorkShiftRequest(reportParamsCashierWorkShift.getCashierWorkShift(), new ReportCashierWorkShiftNetListener(form, reportParamsCashierWorkShift));
          break;
        }
      }
    } else {
      try {
        report.open();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void reportParamsListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      ReportParamsObj reportParams = reportParamsList.getSelectedValue();
      if (reportParams == null) {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        showButton.setEnabled(false);
      } else {
        editButton.setEnabled(!reportParams.isDeficient());
        deleteButton.setEnabled(true);
        showButton.setEnabled(!reportParams.isDeficient());
      }
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    JScrollPane scrollPane1 = new JScrollPane();
    reportParamsList = new JList<>();
    JPanel panel2 = new JPanel();
    JButton updateButton = new JButton();
    addButton = new JButton();
    editButton = new JButton();
    deleteButton = new JButton();
    showButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0420\u0430\u0441\u0441\u044b\u043b\u043a\u0430 \u043e\u0442\u0447\u0435\u0442\u043e\u0432");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u0440\u0430\u0441\u0441\u044b\u043b\u043e\u043a:");
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== scrollPane1 ========
      {

        //---- reportParamsList ----
        reportParamsList.setVisibleRowCount(30);
        reportParamsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportParamsList.addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            reportParamsListValueChanged(e);
          }
        });
        scrollPane1.setViewportView(reportParamsList);
      }
      panel1.add(scrollPane1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.CENTER);

    //======== panel2 ========
    {
      panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel2.setLayout(new GridBagLayout());
      ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
      ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
      ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
      ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

      //---- updateButton ----
      updateButton.setText("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c");
      updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateButtonActionPerformed();
        }
      });
      panel2.add(updateButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- addButton ----
      addButton.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c");
      addButton.setEnabled(false);
      addButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          addButtonActionPerformed();
        }
      });
      panel2.add(addButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- editButton ----
      editButton.setText("\u0420\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c");
      editButton.setEnabled(false);
      editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          editButtonActionPerformed();
        }
      });
      panel2.add(editButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- deleteButton ----
      deleteButton.setText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c");
      deleteButton.setEnabled(false);
      deleteButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          deleteButtonActionPerformed();
        }
      });
      panel2.add(deleteButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- showButton ----
      showButton.setText("\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c");
      showButton.setEnabled(false);
      showButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showButtonActionPerformed();
        }
      });
      panel2.add(showButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel2, BorderLayout.SOUTH);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) waitingDialog.setVisible(true);
    if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (result.getCommand().equals("GET_REPORT_PARAMS")) {
      List<ReportParamsObj> reportParamsList = (List<ReportParamsObj>) result.getResponse().getData();
      reportParamsListModel.removeAllElements();
      int pos = 0;
      for (int i = 0; i < reportParamsList.size(); i++) {
        ReportParamsObj reportParams = reportParamsList.get(i);
        if (reportParams.isExpired()) {
          reportParamsListModel.add(i, reportParams);
        } else {
          reportParamsListModel.add(pos, reportParams);
          pos++;
        }
      }
      addButton.setEnabled(true);
    } else if (result.getCommand().equals("DEL_REPORT_PARAMS")) {
      Long reportParamsId = (Long) result.getResponse().getData();
      reportMap.remove(reportParamsId);
      for (int i = 0; i < reportParamsListModel.size(); i++) {
        if (reportParamsListModel.get(i).getId() == reportParamsId) {
          reportParamsListModel.removeElementAt(i);
          break;
        }
      }
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private abstract class AbstractNetListener implements NetListener<Request, Response> {
    @NotNull
    private final EForm form;
    @NotNull
    private final ReportParamsObj reportParams;
    @NotNull
    private final BuildValues values;

    protected AbstractNetListener(@NotNull EForm form, @NotNull ReportParamsObj reportParams, @NotNull BuildValues values) {
      this.form = form;
      this.reportParams = reportParams;
      this.values = values.setForm(form).setSign(BuildManager.getSign());
    }

    @NotNull
    protected EForm getForm() {
      return form;
    }

    @NotNull
    protected ReportParamsObj getReportParams() {
      return reportParams;
    }

    @NotNull
    protected BuildValues getValues() {
      return values;
    }

    @Override
    public final void netState(NetEvent<Request, Response> event, Network.State state) {
      if (state == Network.State.STARTED) waitingDialog.setVisible(true);
      if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
    }

    @Override
    public final void netError(NetErrorEvent<Request, Response> error) {
      JOptionPane.showMessageDialog(ReportParamsFrame.this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    protected final void report() {
      try {
        AForm<?> report = BuildManager.build(values, EWriteType.FILE);
        reportMap.put(reportParams.getId(), report);
      } catch (IOException | ExcelReportException e) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private class ReportFilterNetListener extends AbstractNetListener {
    @NotNull
    private final Filter filter;

    private ReportFilterNetListener(@NotNull EForm form, @NotNull ReportParamsFilterObj reportParams, @NotNull Filter filter) {
      super(form, reportParams, new BuildValues()
          .setFilter(filter)
          .setCharge(reportParams.isCharge())
          .setDiscount(reportParams.isDiscount()));
      this.filter = filter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (result.getCommand().startsWith("GET_ORDER_LIST")) {
        List<OrderObj> orderList = (List<OrderObj>) result.getResponse().getData();
        getValues().setOrderList(orderList);
        if (getForm() == EForm.FORM_8) {
          if (orderList == null || orderList.isEmpty()) {
            getValues().setActionEventSeatMap(Collections.<Long, List<EventSeatObj>>emptyMap());
            report();
            return;
          }
          Env.net.create("GET_ACTION_EVENT_SEAT_MAP", new Request(getActionEventIdSet(orderList)), this, 100000).start();
        } else if (getForm() == EForm.FORM_17 || getForm() == EForm.FORM_22) {
          Period period;
          try {
            if (filter.isFullReport()) period = Period.create(orderList);
            else {
              period = filter.getPeriod();
              if (period == null) throw ValidationException.absent("Период");
            }
          } catch (ValidationException e) {
            JOptionPane.showMessageDialog(ReportParamsFrame.this, "Не удалось сформировать отчет: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
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

          Env.net.create("GET_REFUND_TICKET_LIST", new Request(obj), this, 60000).start();
        } else {
          report();
        }
      } else if (result.getCommand().equals("GET_ACTION_EVENT_SEAT_MAP")) {
        getValues().setActionEventSeatMap((Map<Long, List<EventSeatObj>>) result.getResponse().getData());
        report();
      } else if (result.getCommand().equals("GET_REFUND_TICKET_LIST")) {
        getValues().setTicketList((List<TicketObj>) result.getResponse().getData());
        report();
      }
    }

    @NotNull
    private Set<Long> getActionEventIdSet(@NotNull List<OrderObj> orderList) {
      //Формируем список сеансов по которым надо получить данные по местам
      Set<Long> actionEventIdSet = new HashSet<>();
      for (OrderObj order : orderList) {
        for (TicketObj ticket : order.getTicketList()) {
          Long actionEventId = ticket.getActionEvent().getId();
          actionEventIdSet.add(actionEventId);
        }
      }
      return actionEventIdSet;
    }
  }

  private class ReportQuotaSaleNetListener extends AbstractNetListener {
    private ReportQuotaSaleNetListener(@NotNull EForm form, @NotNull ReportParamsQuotaSaleObj reportParams) {
      super(form, reportParams, new BuildValues()
          .setAction(reportParams.getAction())
          .setActionEventQuotaMap(reportParams.getActionEventQuotaMap()));
    }

    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      getValues().setReportSaleInfo((ReportSaleInfo) result.getResponse().getData());
      report();
    }
  }

  private class ReportQuotaNetListener extends AbstractNetListener {
    private ReportQuotaNetListener(@NotNull EForm form, @NotNull ReportParamsQuotaObj reportParams) {
      super(form, reportParams, new BuildValues()
          .setAction(reportParams.getAction())
          .setAllowedSeatIdSet(reportParams.getAllowedSeatIdSet()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      getValues().setOrderList((List<OrderObj>) result.getResponse().getData());
      report();
    }
  }

  private class ReportInvoiceNetListener extends AbstractNetListener {
    @NotNull
    private final EInvoiceType invoiceType;

    private ReportInvoiceNetListener(@NotNull EForm form, @NotNull ReportParamsInvoiceObj reportParams, @NotNull EInvoiceType invoiceType) {
      super(form, reportParams, new BuildValues());
      this.invoiceType = invoiceType;
    }

    @NotNull
    @Override
    protected ReportParamsInvoiceObj getReportParams() {
      return (ReportParamsInvoiceObj) super.getReportParams();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      List<QuotaEvent> quotaEventList = (List<QuotaEvent>) result.getResponse().getData();
      QuotaEvent quotaEvent = null;
      for (QuotaEvent currentQuotaEvent : quotaEventList) {
        if (currentQuotaEvent.getId() == getReportParams().getActionEventId()) {
          quotaEvent = currentQuotaEvent;
          break;
        }
      }
      if (quotaEvent == null) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, "Не найден сеанс " + getReportParams().getActionEventId() + " квоты " + getReportParams().getQuotaNumber(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }

      QuotaDataObj quotaData = null;
      for (QuotaDataObj currentQuotaData : quotaEvent.getQuotaDataList(QuotaDataObj.Type.valueOf(invoiceType.name()))) {
        if (currentQuotaData.getNumber().equals(getReportParams().getQuotaNumber())) {
          quotaData = currentQuotaData;
          break;
        }
      }
      if (quotaData == null) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, "Не найдена квота " + getReportParams().getQuotaNumber() + " сеанса " + getReportParams().getActionEventId(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }

      getValues().setQuotaData(quotaData);
      report();
    }
  }

  private static void cashierWorkShiftRequest(@NotNull RCashierWorkShift cashierWorkShift, @NotNull ReportCashierWorkShiftNetListener listener) {
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

  private class ReportCashierWorkShiftNetListener extends AbstractNetListener {
    private int frontendIndex = 0;
    @NotNull
    private final List<OrderObj> orderList = new ArrayList<>();

    private ReportCashierWorkShiftNetListener(@NotNull EForm form, @NotNull ReportParamsCashierWorkShiftObj reportParams) {
      super(form, reportParams, new BuildValues()
          .setCashierWorkShift(reportParams.getCashierWorkShift())
          .setCharge(reportParams.isCharge())
          .setDiscount(reportParams.isDiscount()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void netResult(NetResultEvent<Request, Response> result) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(ReportParamsFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      orderList.addAll((List<OrderObj>) result.getResponse().getData());

      RCashierWorkShift cashierWorkShift = ((ReportParamsCashierWorkShiftObj) getReportParams()).getCashierWorkShift();
      List<RFrontend> frontendList = cashierWorkShift.getFrontendList();
      if (frontendIndex < frontendList.size() - 1) {
        frontendIndex++;
        cashierWorkShiftRequest(cashierWorkShift, this);
      } else {
        getValues().setOrderList(orderList);
        report();
      }
    }

    int getFrontendIndex() {
      return frontendIndex;
    }
  }
}
