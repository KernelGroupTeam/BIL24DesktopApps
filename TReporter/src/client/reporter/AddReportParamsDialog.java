/*
 * Created by JFormDesigner on Thu Jan 18 10:50:35 GMT 2018
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.*;
import client.net.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.forms.*;
import report.models.*;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Inventor
 */
public class AddReportParamsDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel formLabel;
  private JTextField nameTextField;
  private JTextField emailsTextField;
  private JXDateTimePicker startDatePicker;
  private JXDateTimePicker endDatePicker;
  private JRadioButton reportPeriodRadioButton1;
  private JRadioButton reportPeriodRadioButton2;
  private JRadioButton reportPeriodRadioButton3;
  private JCheckBox allowedCheckBox;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  @NotNull
  private static final String EMAIL_DELIMITER = ",";
  @NotNull
  private static final Pattern PATTERN_EMAIL = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
  @Nullable
  private final ReportParamsObj reportParams;//Если null значит это режим добавления
  @Nullable
  private final AForm<?> report;//Если null значит это режим редактирования
  @NotNull
  private final Map<Long, AForm<?>> reportMap;
  @NotNull
  private final DefaultListModel<ReportParamsObj> reportParamsListModel;
  @NotNull
  private WaitingDialog waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

  //Конструктор для редактирования
  public AddReportParamsDialog(@NotNull Window owner, @NotNull EForm form, @NotNull ReportParamsObj reportParams, @NotNull Map<Long, AForm<?>> reportMap, @NotNull DefaultListModel<ReportParamsObj> reportParamsListModel) {
    this(owner, reportParams, form, null, reportMap, reportParamsListModel);

    setTitle("Редактировать рассылку");
    nameTextField.setText(reportParams.getName());
    emailsTextField.setText(emailListToString(reportParams.getEmailList()));
    try {
      startDatePicker.setDateFormatted(reportParams.getStartDate());
      endDatePicker.setDateFormatted(reportParams.getEndDate());
    } catch (ParseException e) {
      JOptionPane.showMessageDialog(this, "Ошибка формата даты", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    allowedCheckBox.setSelected(reportParams.isAllowed());
    if (reportParams.getReportPeriod() == 0) reportPeriodRadioButton1.setSelected(true);
    else if (reportParams.getReportPeriod() == 1) reportPeriodRadioButton2.setSelected(true);
    else if (reportParams.getReportPeriod() == 2) reportPeriodRadioButton3.setSelected(true);
  }

  //Конструктор для добавления
  public AddReportParamsDialog(@NotNull Window owner, @NotNull AForm<?> report, @NotNull Map<Long, AForm<?>> reportMap, @NotNull DefaultListModel<ReportParamsObj> reportParamsListModel) {
    this(owner, null, report.getForm(), report, reportMap, reportParamsListModel);
  }

  private AddReportParamsDialog(@NotNull Window owner, @Nullable ReportParamsObj reportParams, @NotNull EForm form, @Nullable AForm<?> report, @NotNull Map<Long, AForm<?>> reportMap, @NotNull DefaultListModel<ReportParamsObj> reportParamsListModel) {
    super(owner);
    initComponents();

    this.reportParams = reportParams;
    this.report = report;
    this.reportMap = reportMap;
    this.reportParamsListModel = reportParamsListModel;

    formLabel.setText(form.getName());

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void okButtonActionPerformed() {
    if (nameTextField.getText().trim().isEmpty()) {
      nameTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Название рассылки не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (emailsTextField.getText().trim().isEmpty()) {
      emailsTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Адресаты рассылки не заданы", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    List<String> emailList = emailStringToList();
    if (emailList == null) return;
    String startDate = startDatePicker.getDateFormatted();
    if (startDate == null) {
      startDatePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Дата начала рассылки не указана", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    String endDate = endDatePicker.getDateFormatted();
    if (endDate == null) {
      endDatePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Дата окончания рассылки не указана", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (startDatePicker.getDate().getTime() >= endDatePicker.getDate().getTime()) {
      endDatePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Дата окончания рассылки должна быть позже даты начала рассылки", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    int reportPeriod;
    if (reportPeriodRadioButton1.isSelected()) reportPeriod = 0;
    else if (reportPeriodRadioButton2.isSelected()) reportPeriod = 1;
    else if (reportPeriodRadioButton3.isSelected()) reportPeriod = 2;
    else {
      JOptionPane.showMessageDialog(this, "Период отчета не указан", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }

    ReportParamsObj obj = null;
    if (reportParams == null) {
      if (report == null) throw new IllegalStateException("report is null");
      switch (report.getForm().getType()) {
        case FILTER: {
          AFormFilter<?> reportFilter = (AFormFilter<?>) report;
          Filter filter = reportFilter.getFilter();
          Period period = filter.getPeriod();
          FilterObj filterObj = new FilterObj(
              period == null ? null : period.getFrom().getTime(),
              period == null ? null : period.getTo().getTime(),
              filter.getPeriodType() == null ? null : filter.getPeriodType().getId(),
              filter.getAcquiring(), filter.getOrganizer(), filter.getCity(), filter.getVenue(), filter.getAction(), filter.getActionEvent(),
              filter.getAgent(), filter.getFrontend(), filter.getSystem(), filter.getGateway(), filter.isFullReport(), filter.isAllStatuses());
          obj = new ReportParamsFilterObj(0, report.getForm().getId(), filterObj, reportFilter.isCharge(), reportFilter.isDiscount());
          break;
        }
        case QUOTA_SALE: {
          Form9 report9 = (Form9) report;
          obj = new ReportParamsQuotaSaleObj(0, report.getForm().getId(), report9.getAction(), report9.getActionEventQuotaMap());
          break;
        }
        case QUOTA: {
          AFormQuotaSeats<?> reportQuotaSeats = (AFormQuotaSeats<?>) report;
          obj = new ReportParamsQuotaObj(0, report.getForm().getId(), reportQuotaSeats.getAction(), reportQuotaSeats.getAllowedSeatIdSet());
          break;
        }
        case INVOICE: {
          QuotaDataObj quotaData = ((AFormInvoice) report).getQuotaData();
          obj = new ReportParamsInvoiceObj(0, report.getForm().getId(), quotaData.getQuotaEvent().getActionId(), quotaData.getQuotaEvent().getId(), quotaData.getNumber());
          break;
        }
        case CASHIER_WORK_SHIFT: {
          AFormCashierWorkShift<?> reportCashierWorkShift = (AFormCashierWorkShift<?>) report;
          RCashierWorkShift cashierWorkShift = reportCashierWorkShift.getCashierWorkShift();
          obj = new ReportParamsCashierWorkShiftObj(0, report.getForm().getId(), cashierWorkShift, reportCashierWorkShift.isCharge(), reportCashierWorkShift.isDiscount());
          break;
        }
      }
    } else {
      obj = reportParams;
    }
    obj.setName(nameTextField.getText().trim());
    obj.setEmailList(emailList);
    obj.setStartDate(startDate);
    obj.setEndDate(endDate);
    obj.setAllowed(allowedCheckBox.isSelected());
    obj.setReportPeriod(reportPeriod);
    if (reportParams == null) {
      Env.net.create("ADD_REPORT_PARAMS", new Request(obj), this).start();
    } else {
      Env.net.create("SAVE_REPORT_PARAMS", new Request(obj), this).start();
    }
  }

  private void cancelButtonActionPerformed() {
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    formLabel = new JLabel();
    JLabel label2 = new JLabel();
    nameTextField = new JTextField();
    JLabel label3 = new JLabel();
    emailsTextField = new JTextField();
    JLabel label4 = new JLabel();
    startDatePicker = new JXDateTimePicker();
    JLabel label5 = new JLabel();
    endDatePicker = new JXDateTimePicker();
    JLabel label6 = new JLabel();
    reportPeriodRadioButton1 = new JRadioButton();
    reportPeriodRadioButton2 = new JRadioButton();
    reportPeriodRadioButton3 = new JRadioButton();
    allowedCheckBox = new JCheckBox();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0440\u0430\u0441\u0441\u044b\u043b\u043a\u0443");
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 250, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u041e\u0442\u0447\u0435\u0442:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(formLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label2 ----
        label2.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
        contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(nameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label3 ----
        label3.setText("\u0410\u0434\u0440\u0435\u0441\u0430\u0442\u044b:");
        contentPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- emailsTextField ----
        emailsTextField.setToolTipText("\u0414\u043b\u044f \u0443\u043a\u0430\u0437\u0430\u043d\u0438\u044f \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u0438\u0445 \u0430\u0434\u0440\u0435\u0441\u0430\u0442\u043e\u0432 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \",\"");
        contentPanel.add(emailsTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label4 ----
        label4.setText("\u041d\u0430\u0447\u0430\u043b\u043e \u0440\u0430\u0441\u0441\u044b\u043b\u043a\u0438:");
        contentPanel.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(startDatePicker, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label5 ----
        label5.setText("\u041e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u0440\u0430\u0441\u0441\u044b\u043b\u043a\u0438:");
        contentPanel.add(label5, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(endDatePicker, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label6 ----
        label6.setText("\u041f\u0435\u0440\u0438\u043e\u0434 \u043e\u0442\u0447\u0435\u0442\u0430:");
        label6.setVerticalAlignment(SwingConstants.TOP);
        contentPanel.add(label6, new GridBagConstraints(0, 5, 1, 3, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- reportPeriodRadioButton1 ----
        reportPeriodRadioButton1.setText("\u041f\u043e \u0434\u0430\u0442\u0430\u043c \u0444\u0438\u043b\u044c\u0442\u0440\u0430");
        reportPeriodRadioButton1.setSelected(true);
        contentPanel.add(reportPeriodRadioButton1, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- reportPeriodRadioButton2 ----
        reportPeriodRadioButton2.setText("\u0422\u0435\u043a\u0443\u0449\u0438\u0435 \u0441\u0443\u0442\u043a\u0438");
        contentPanel.add(reportPeriodRadioButton2, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- reportPeriodRadioButton3 ----
        reportPeriodRadioButton3.setText("\u041f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u0435 \u0441\u0443\u0442\u043a\u0438");
        contentPanel.add(reportPeriodRadioButton3, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- allowedCheckBox ----
        allowedCheckBox.setText("\u0420\u0430\u0441\u0441\u044b\u043b\u043a\u0430 \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d\u0430");
        allowedCheckBox.setSelected(true);
        contentPanel.add(allowedCheckBox, new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0,
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

    //---- buttonGroup1 ----
    ButtonGroup buttonGroup1 = new ButtonGroup();
    buttonGroup1.add(reportPeriodRadioButton1);
    buttonGroup1.add(reportPeriodRadioButton2);
    buttonGroup1.add(reportPeriodRadioButton3);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Nullable
  private List<String> emailStringToList() {
    Set<String> emailSet = new LinkedHashSet<>();
    StringTokenizer st = new StringTokenizer(emailsTextField.getText().trim(), EMAIL_DELIMITER);
    while (st.hasMoreTokens()) {
      String email = st.nextToken().trim();
      if (!PATTERN_EMAIL.matcher(email).matches()) {
        emailsTextField.requestFocus();
        JOptionPane.showMessageDialog(this, "Формат email адресата \"" + email + "\" не верный", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return null;
      }
      emailSet.add(email);
    }
    return new ArrayList<>(emailSet);
  }

  @NotNull
  private static String emailListToString(@NotNull List<String> emailList) {
    StringBuilder result = new StringBuilder();
    for (String email : emailList) {
      result.append(email).append(EMAIL_DELIMITER);
    }
    if (result.length() > 0) result.setLength(result.length() - 1);
    return result.toString();
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
    if (result.getCommand().equals("ADD_REPORT_PARAMS")) {
      ReportParamsObj reportParams = (ReportParamsObj) result.getResponse().getData();
      reportMap.put(reportParams.getId(), report);
      reportParamsListModel.addElement(reportParams);
    } else if (result.getCommand().equals("SAVE_REPORT_PARAMS")) {
      if (reportParams == null) throw new IllegalStateException("reportParams is null");
      for (int i = 0; i < reportParamsListModel.size(); i++) {
        if (reportParamsListModel.get(i).getId() == reportParams.getId()) {
          reportParamsListModel.setElementAt(reportParams, i);
          break;
        }
      }
    }
    this.dispose();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось передать данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
