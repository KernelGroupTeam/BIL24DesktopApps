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
import javax.swing.event.*;

import client.component.WaitingDialog;
import client.component.suggestion.SuggestionComboBox;
import client.net.*;
import client.reporter.component.renderer.*;
import client.utils.ComponentClipboard;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ExcelReportException;
import report.reporter.enums.EInvoiceType;
import report.reporter.managers.BuildManager;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Maksim
 */
public class QuotaInvoiceFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<RAction> actionComboBox;
  private SuggestionComboBox<RActionEvent> actionEventComboBox;
  private JList<QuotaDataObj> quotaList;
  private JButton showButton;
  private JButton reportsButton;
  private JButton clearButton;
  private JPopupMenu reportsPopupMenu;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final List<RAction> actionList = new ArrayList<>();
  private final List<RActionEvent> actionEventList = new ArrayList<>();
  private final DefaultListModel<QuotaDataObj> quotaListModel = new DefaultListModel<>();
  private final Map<Long, QuotaEvent> quotaEventMap = new HashMap<>();
  private final Map<Long, Map<QuotaDataObj.Type, Set<String>>> quotaSelectedMap = new HashMap<>();
  private boolean ignoreQuotaSelection;//Флаг блокировки сохранения выбранных накладных. Его нужно активировать если происходит изменения quotaListModel(добавление/удаление элементов)
  private WaitingDialog waitingDialog;

  public QuotaInvoiceFrame(Window owner, List<RAction> allActionList, List<RActionEvent> allActionEventList) {
    initComponents();

    Set<Long> actionIdSet = new HashSet<>();
    for (RActionEvent actionEvent : allActionEventList) {
      if (actionEvent.isQuota()) {
        actionEventList.add(actionEvent);
        actionIdSet.add(actionEvent.getActionId());
      }
    }
    for (RAction action : allActionList) {
      if (actionIdSet.contains(action.getId())) {
        actionList.add(action);
        actionComboBox.addItem(action);//на первой итерации вызывается actionComboBoxItemStateChanged
      }
    }
    ActionListRenderer actionListRenderer = new ActionListRenderer(70);
    actionListRenderer.setShowOrganizer(Env.user.getUserType() != UserType.ORGANIZER);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);
    actionEventComboBox.setRenderer(new ActionEventListRenderer());
    QuotaDataListRenderer quotaDataListRenderer = new QuotaDataListRenderer();
    quotaList.setModel(quotaListModel);
    quotaList.setCellRenderer(quotaDataListRenderer);
    ComponentClipboard.setJListCopyAction(quotaList, quotaDataListRenderer);
    showButton.setIcon(Env.excelIcon);
    reportsButton.setIcon(Env.excelIcon);

    String text = reportsButton.getText();
    reportsButton.setText(text + " (88)");

    pack();
    setLocationRelativeTo(owner);

    reportsButton.setPreferredSize(reportsButton.getSize());
    reportsButton.setText(text);
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
      RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
      List<RActionEvent> filterList = new ArrayList<>();
      for (RActionEvent actionEvent : actionEventList) {
        if (actionEvent.pass(action)) filterList.add(actionEvent);
      }
      actionEventComboBox.removeAllItems();
      clearButtonActionPerformed();
      for (RActionEvent actionEvent : filterList) {
        actionEventComboBox.addItem(actionEvent);
      }
    }
  }

  private void actionEventComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      showButton.setEnabled(false);
      RActionEvent actionEvent = actionEventComboBox.getItemAt(actionEventComboBox.getSelectedIndex());
      QuotaEvent quotaEvent = quotaEventMap.get(actionEvent.getId());
      loadQuotaEvent(quotaEvent);
      selectQuotaEvent(quotaEvent);
      reportsButton.setEnabled(quotaEvent != null);
    }
  }

  private void quotaListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      //Получаем список выбранных накладных
      List<QuotaDataObj> quotaSelectedList = quotaList.getSelectedValuesList();
      if (quotaSelectedList == null) return;
      //Сохраняем выбранные накладные
      saveQuotaSelected(quotaSelectedList);
      //Если выбрана одна конкретная накладная, тогда активируем кнопку
      showButton.setEnabled(quotaSelectedList.size() == 1);
      clearButton.setEnabled(!quotaSelectedMap.isEmpty());
    }
  }

  private void getButtonActionPerformed() {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    if (action == null) return;
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_QUOTA_ACTION", new Request(action.getId()), this).start();
  }

  private void showButtonActionPerformed() {
    QuotaDataObj quotaData = quotaList.getSelectedValue();
    if (quotaData == null) return;
    BuildManager.setQuotaData(quotaData);
    try {
      BuildManager.build(quotaData.getType() == QuotaDataObj.Type.IN ? EForm.INVOICE_IN : EForm.INVOICE_OUT);
    } catch (IOException | ExcelReportException e) {
      JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void reportsButtonActionPerformed() {
    reportsPopupMenu.show(reportsButton, reportsButton.getWidth(), 0);
  }

  private void report9MenuItemActionPerformed() {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    if (action == null) return;
    Map<Long, Set<String>> actionEventInQuotaMap = getActionEventQuotaMap(QuotaDataObj.Type.IN);//actionEventId -> Set<QuotaNumber>
    Map<Long, Set<String>> actionEventOutQuotaMap = getActionEventQuotaMap(QuotaDataObj.Type.OUT);//actionEventId -> Set<QuotaNumber>

    Object[] obj = new Object[3];
    obj[0] = action.getId();
    obj[1] = actionEventInQuotaMap;
    obj[2] = actionEventOutQuotaMap;

    Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap;
    if (actionEventInQuotaMap == null && actionEventOutQuotaMap == null) {
      actionEventQuotaMap = null;
    } else {
      actionEventQuotaMap = new HashMap<>();
      if (actionEventInQuotaMap != null) actionEventQuotaMap.put(EInvoiceType.IN.getId(), actionEventInQuotaMap);
      if (actionEventOutQuotaMap != null) actionEventQuotaMap.put(EInvoiceType.OUT.getId(), actionEventOutQuotaMap);
    }

    BuildManager.setAction(action);
    BuildManager.setActionEventQuotaMap(actionEventQuotaMap);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_REPORT_SALE_INFO", new Request(obj), new ReportNetListener(EForm.FORM_9)).start();
  }

  private void report14MenuItemActionPerformed() {
    requestReport(EForm.FORM_14);
  }

  private void report15MenuItemActionPerformed() {
    requestReport(EForm.FORM_15);
  }

  //Очистка всего чего связано с запоминание выбранных накладных
  private void clearButtonActionPerformed() {
    quotaList.clearSelection();
    quotaSelectedMap.clear();
    changeReportsButtonText();
    clearButton.setEnabled(false);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    actionComboBox = new SuggestionComboBox<>();
    actionEventComboBox = new SuggestionComboBox<>();
    JPanel panel2 = new JPanel();
    JLabel label3 = new JLabel();
    JScrollPane scrollPane1 = new JScrollPane();
    quotaList = new JList<>();
    JPanel panel3 = new JPanel();
    JButton getButton = new JButton();
    showButton = new JButton();
    reportsButton = new JButton();
    clearButton = new JButton();
    reportsPopupMenu = new JPopupMenu();
    JMenuItem report9MenuItem = new JMenuItem();
    JMenuItem report14MenuItem = new JMenuItem();
    JMenuItem report15MenuItem = new JMenuItem();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041d\u0430\u043a\u043b\u0430\u0434\u043d\u044b\u0435 \u043f\u043e \u043a\u0432\u043e\u0442\u0430\u043c");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u041f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435:");
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- label2 ----
      label2.setText("\u0421\u0435\u0430\u043d\u0441:");
      panel1.add(label2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
        new Insets(0, 0, 0, 5), 0, 0));

      //---- actionEventComboBox ----
      actionEventComboBox.setMaximumRowCount(18);
      actionEventComboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          actionEventComboBoxItemStateChanged(e);
        }
      });
      actionEventComboBox.setPrototypeDisplayValue(new RActionEvent(0, 0, "88.88.8888 88:88"));
      panel1.add(actionEventComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
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
      label3.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u043d\u0430\u043a\u043b\u0430\u0434\u043d\u044b\u0445:");
      panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== scrollPane1 ========
      {

        //---- quotaList ----
        quotaList.setVisibleRowCount(10);
        quotaList.addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            quotaListValueChanged(e);
          }
        });
        scrollPane1.setViewportView(quotaList);
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
      ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
      ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
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

      //---- showButton ----
      showButton.setText("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043d\u0430\u043a\u043b\u0430\u0434\u043d\u0443\u044e");
      showButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showButtonActionPerformed();
        }
      });
      panel3.add(showButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
      panel3.add(reportsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- clearButton ----
      clearButton.setText("\u041e\u0442\u043c\u0435\u043d\u0438\u0442\u044c \u0432\u044b\u0434\u0435\u043b\u0435\u043d\u0438\u0435");
      clearButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          clearButtonActionPerformed();
        }
      });
      panel3.add(clearButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel3, BorderLayout.SOUTH);

    //======== reportsPopupMenu ========
    {

      //---- report9MenuItem ----
      report9MenuItem.setText("\u041e\u0442\u0447\u0435\u0442 \u21169");
      report9MenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/excel.png")));
      report9MenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          report9MenuItemActionPerformed();
        }
      });
      reportsPopupMenu.add(report9MenuItem);

      //---- report14MenuItem ----
      report14MenuItem.setText("\u041e\u0442\u0447\u0435\u0442 \u211614");
      report14MenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/excel.png")));
      report14MenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          report14MenuItemActionPerformed();
        }
      });
      reportsPopupMenu.add(report14MenuItem);

      //---- report15MenuItem ----
      report15MenuItem.setText("\u041e\u0442\u0447\u0435\u0442 \u211615");
      report15MenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/excel.png")));
      report15MenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          report15MenuItemActionPerformed();
        }
      });
      reportsPopupMenu.add(report15MenuItem);
    }
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

    if (result.getCommand().equals("GET_QUOTA_ACTION")) {
      List<QuotaEvent> quotaEventList = (List<QuotaEvent>) result.getResponse().getData();
      for (QuotaEvent quotaEvent : quotaEventList) {
        quotaEventMap.put(quotaEvent.getId(), quotaEvent);
      }
      RActionEvent actionEvent = actionEventComboBox.getItemAt(actionEventComboBox.getSelectedIndex());
      QuotaEvent quotaEvent = quotaEventMap.get(actionEvent.getId());
      loadQuotaEvent(quotaEvent);
      selectQuotaEvent(quotaEvent);
      reportsButton.setEnabled(quotaEvent != null);
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private void loadQuotaEvent(@Nullable QuotaEvent quotaEvent) {
    try {
      ignoreQuotaSelection = true;
      quotaListModel.removeAllElements();
      if (quotaEvent == null) return;
      for (QuotaDataObj quotaData : quotaEvent.getQuotaInDataList()) {
        quotaListModel.addElement(quotaData);
      }
      for (QuotaDataObj quotaData : quotaEvent.getQuotaOutDataList()) {
        quotaListModel.addElement(quotaData);
      }
    } finally {
      ignoreQuotaSelection = false;
    }
  }

  private void saveQuotaSelected(@NotNull List<QuotaDataObj> quotaSelectedList) {
    //Если включена блокировка тогда ничего не делаем
    if (ignoreQuotaSelection) return;
    RActionEvent actionEvent = actionEventComboBox.getItemAt(actionEventComboBox.getSelectedIndex());
    if (actionEvent == null) return;
    //Если список выбранных накладных пустой, тогда удаляем карту ранее выбранных накладных в выбранном сеансе
    if (quotaSelectedList.isEmpty()) quotaSelectedMap.remove(actionEvent.getId());
    else {//Если все же есть выбранные накладные
      //Получаем карту ранее выбранных накладных в выбранном сеансе
      Map<QuotaDataObj.Type, Set<String>> quotaNumberMap = quotaSelectedMap.get(actionEvent.getId());
      //Если не было ранее выбранных накладных, тогда создаем новый карту для выбранного сеанса
      if (quotaNumberMap == null) quotaSelectedMap.put(actionEvent.getId(), quotaNumberMap = new EnumMap<>(QuotaDataObj.Type.class));
        //Если были ранее выбранны накладные, тогда просто зачищаем карту, т.к. проще просто удалить и занести заново весь список выбранных накладных, чем сравнивать и искать какие выбраны а какие нет
      else quotaNumberMap.clear();
      //Запоминаем выбранные накладные
      for (QuotaDataObj quotaData : quotaSelectedList) {
        //Получаем набор накладных по типу
        Set<String> quotaNumberSet = quotaNumberMap.get(quotaData.getType());
        //Если не было ещё добавлено ни одной накладной в набор, тогда создаем его
        if (quotaNumberSet == null) quotaNumberMap.put(quotaData.getType(), quotaNumberSet = new HashSet<>());
        //Добавляем накладную в набор
        quotaNumberSet.add(quotaData.getNumber());
      }
    }
    changeReportsButtonText();
  }

  private void changeReportsButtonText() {
    if (quotaSelectedMap.isEmpty()) reportsButton.setText("Отчеты");
    else {
      int count = 0;
      for (Map.Entry<Long, Map<QuotaDataObj.Type, Set<String>>> actionEvent : quotaSelectedMap.entrySet()) {
        for (Map.Entry<QuotaDataObj.Type, Set<String>> entry : actionEvent.getValue().entrySet()) {
          count += entry.getValue().size();
        }
      }
      reportsButton.setText("Отчеты (" + count + ")");
    }
  }

  private void selectQuotaEvent(@Nullable QuotaEvent quotaEvent) {
    if (quotaEvent == null) return;
    //Если список выбранных накладных пустой
    if (quotaSelectedMap.isEmpty()) return;

    //Получаем карту ранее выбранных накладных в выбранном сеансе
    Map<QuotaDataObj.Type, Set<String>> quotaNumberMap = quotaSelectedMap.get(quotaEvent.getId());
    //Если нет ранее выбранные накладных
    if (quotaNumberMap == null) return;

    //Проходимся по всем только что полученным накладным
    for (int i = 0; i < quotaListModel.getSize(); i++) {
      //Получаем накладную из списка только что полученных
      QuotaDataObj quotaData = quotaListModel.getElementAt(i);
      //Получаем набор ранее выбранных накладных в выбранном сеансе
      Set<String> quotaNumberSet = quotaNumberMap.get(quotaData.getType());
      //Если в ранее выбранных накладных имеется накладная с таким же номером, тогда выбираем накладную в списке
      if (quotaNumberSet != null && quotaNumberSet.contains(quotaData.getNumber())) {
        quotaList.addSelectionInterval(i, i);
      }
    }
  }

  @Nullable
  private Map<Long, Set<String>> getActionEventQuotaMap(@NotNull QuotaDataObj.Type type) {
    //Если карта пустая, значит сразу возвращаем null
    if (quotaSelectedMap.isEmpty()) return null;
    Map<Long, Set<String>> actionEventQuotaMap = new HashMap<>();
    for (Map.Entry<Long, Map<QuotaDataObj.Type, Set<String>>> entry : quotaSelectedMap.entrySet()) {
      Set<String> quotaNumberSet = entry.getValue().get(type);
      if (quotaNumberSet != null) actionEventQuotaMap.put(entry.getKey(), quotaNumberSet);
    }
    //Если карта выбранных накладных необходимого типа пустой, тогда возвращаем null. Это связано с тем, что когда будет
    //происходить генерация данных, то мы проверяем набор на null, а если отправим пустой список тогда мы все накладные проигнорируем
    if (actionEventQuotaMap.isEmpty()) return null;
    return actionEventQuotaMap;
  }

  private void requestReport(@NotNull EForm form) {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    if (action == null) return;
    Object[] obj = new Object[8];
    obj[0] = false;
    obj[1] = action.getId();

    Map<Long, Set<String>> actionEventInQuotaMap = getActionEventQuotaMap(QuotaDataObj.Type.IN);
    Map<Long, Set<String>> actionEventOutQuotaMap = getActionEventQuotaMap(QuotaDataObj.Type.OUT);
    Set<QuotaEvent> quotaEventSet = new HashSet<>();
    for (QuotaEvent quotaEvent : quotaEventMap.values()) if (quotaEvent.getActionId() == action.getId()) quotaEventSet.add(quotaEvent);
    boolean ignoreQuotaAllowed = actionEventInQuotaMap != null || actionEventOutQuotaMap != null;//Разрешение на игнорирование накладных
    Set<Long> allowedSeatIdSet = new HashSet<>();//Запоминаем какие места из накладных нам нужны
    for (QuotaEvent quotaEvent : quotaEventSet) {
      Set<String> quotaInNumberSet = actionEventInQuotaMap == null ? null : actionEventInQuotaMap.get(quotaEvent.getId());
      Set<String> quotaOutNumberSet = actionEventOutQuotaMap == null ? null : actionEventOutQuotaMap.get(quotaEvent.getId());

      List<QuotaDataObj> quotaInDataList = quotaEvent.getQuotaInDataList();
      for (QuotaDataObj quotaInData : quotaInDataList) {
        if (ignoreQuotaAllowed) {
          if (quotaInNumberSet == null || !quotaInNumberSet.contains(quotaInData.getNumber())) continue;
        }
        for (QuotaSeat quotaSeat : quotaInData.getQuotaSeatList()) allowedSeatIdSet.add(quotaSeat.getId());
      }

      List<QuotaDataObj> quotaOutDataList = quotaEvent.getQuotaOutDataList();
      for (QuotaDataObj quotaOutData : quotaOutDataList) {
        if (ignoreQuotaAllowed) {
          if (quotaOutNumberSet == null || !quotaOutNumberSet.contains(quotaOutData.getNumber())) continue;
        }
        for (QuotaSeat quotaSeat : quotaOutData.getQuotaSeatList()) allowedSeatIdSet.remove(quotaSeat.getId());
      }
    }

    BuildManager.setAction(action);
    BuildManager.setAllowedSeatIdSet(allowedSeatIdSet);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_ORDER_LIST_3", new Request(obj), new ReportNetListener(form)).start();
  }

  private class ReportNetListener implements NetListener<Request, Response> {
    @NotNull
    private final EForm form;

    private ReportNetListener(@NotNull EForm form) {
      this.form = form;
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
        JOptionPane.showMessageDialog(QuotaInvoiceFrame.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (result.getCommand().equals("GET_REPORT_SALE_INFO")) {
        ReportSaleInfo reportSaleInfo = (ReportSaleInfo) result.getResponse().getData();
        BuildManager.setReportSaleInfo(reportSaleInfo);
      } else if (result.getCommand().equals("GET_ORDER_LIST_3")) {
        List<OrderObj> orderList = (List<OrderObj>) result.getResponse().getData();
        BuildManager.setOrderListQuota(orderList);
      }

      try {
        BuildManager.build(form);
      } catch (IOException | ExcelReportException e) {
        JOptionPane.showMessageDialog(QuotaInvoiceFrame.this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }

    @Override
    public void netError(NetErrorEvent<Request, Response> error) {
      JOptionPane.showMessageDialog(QuotaInvoiceFrame.this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }
}
