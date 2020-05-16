/*
 * Created by JFormDesigner on Tue Jul 21 16:04:24 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import client.component.*;
import client.component.suggestion.SuggestionComboBox;
import client.editor.component.*;
import client.editor.component.listener.*;
import client.editor.component.renderer.GatewayListRenderer;
import client.net.*;
import client.utils.*;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.GatewayObj;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class AddActionEventDialog extends JDialog implements ResizeListener, NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<SeatingPlanObj> planIntComboBox;
  private SuggestionComboBox<ActionObj> actionIntComboBox;
  private JCheckBox quotaCheckBox;
  private JLabel ebsLabel;
  private SuggestionComboBox<GatewayObj> ebsComboBox;
  private JCheckBox eTicketsCheckBox;
  private JCheckBox sellEnabledCheckBox;
  private JButton listButton;
  private JScrollPane scrollPane;
  private JPanel verticalPanel;
  private JButton addButton2;
  private JButton okButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final long cityId;//диалог позволяет создать сеанс только в данном городе
  private final ChildlessControl childlessControl;
  private final OperationComboBox<ActionEventObj> actionEventComboBox;
  private final InfoButtonActionListener infoButtonActionListener = new InfoButtonActionListener();
  @Nullable
  private List<GatewayEventObj> gatewayEventList = null;
  private Runnable afterSuccessAdding;
  private WaitingDialog waitingDialog;

  public AddActionEventDialog(Window owner, long cityId, ChildlessControl childlessControl, OperationComboBox<ActionEventObj> actionEventComboBox,
                              OperationComboBox<SeatingPlanObj> planComboBox, OperationComboBox<ActionObj> actionComboBox) {
    super(owner);
    this.cityId = cityId;
    this.childlessControl = childlessControl;
    this.actionEventComboBox = actionEventComboBox;
    initComponents();

    for (int i = 0; i < planComboBox.getItemCount(); i++) {
      planIntComboBox.addItem(planComboBox.getItemAt(i));
    }
    planIntComboBox.setSelectedIndex(planComboBox.getSelectedIndex());

    for (int i = 0; i < actionComboBox.getItemCount(); i++) {
      actionIntComboBox.addItem(actionComboBox.getItemAt(i));
    }
    actionIntComboBox.setSelectedIndex(actionComboBox.getSelectedIndex());

    for (GatewayObj gateway : Env.gatewayList) {
      ebsComboBox.addItem(gateway);
    }
    GatewayListRenderer gatewayListRenderer = new GatewayListRenderer(Env.user.getUserType() == UserType.OPERATOR);
    ebsComboBox.setRenderer(gatewayListRenderer);
    ebsComboBox.setElementToStringConverter(gatewayListRenderer);

    addButtonActionPerformed();
    setLocationRelativeTo(getOwner());
  }

  private void ebsComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      GatewayObj gateway = ebsComboBox.getItemAt(ebsComboBox.getSelectedIndex());
      gatewayEventList = null;
      if (gateway.getId() == 0) {
        quotaCheckBox.setEnabled(true);
        okButton.setText("Далее...");
        updateActionEventItemPanelsByGatewayEventList();
      } else {
        quotaCheckBox.setEnabled(false);
        ebsComboBox.setEnabled(false);
        okButton.setEnabled(false);
        okButton.setText("OK");
        Env.net.create("GET_GATEWAY_EVENT_LIST", new Request(gateway.getId()), this, Env.GATEWAY_TIMEOUT).start();
      }
    }
  }

  private void quotaCheckBoxItemStateChanged(ItemEvent e) {
    ebsLabel.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
    ebsComboBox.setEnabled(e.getStateChange() != ItemEvent.SELECTED);
    if (e.getStateChange() == ItemEvent.SELECTED) okButton.setText("OK");
    else okButton.setText("Далее...");
  }

  private void listButtonActionPerformed() {
    if (gatewayEventList == null) return;
    StringBuilder str = new StringBuilder();
    for (GatewayEventObj gatewayEvent : gatewayEventList) {
      str.append(ActionEventItemPanel.stringValue(gatewayEvent)).append("\n");
    }
    TextDialog frame = new TextDialog(this, "Список сеансов ВБС", Dialog.ModalityType.DOCUMENT_MODAL);
    frame.getTextArea().setText(str.toString());
    frame.setVisible(true);
  }

  private void updateActionEventItemPanelsByGatewayEventList() {
    listButton.setVisible(gatewayEventList != null);
    listButton.setEnabled(gatewayEventList != null && !gatewayEventList.isEmpty());
    addButton2.setVisible(gatewayEventList != null);
    int items = verticalPanel.getComponentCount() - 1;
    for (int i = 0; i < items; i++) {
      ActionEventItemPanel itemPanel = (ActionEventItemPanel) verticalPanel.getComponent(i);
      itemPanel.setGatewayEventList(gatewayEventList);
    }
  }

  private void addButtonActionPerformed() {
    int lastItemIndex = verticalPanel.getComponentCount() - 2;
    ActionEventItemPanel lastItemPanel = null;
    if (lastItemIndex >= 0) {//уже есть item в панели
      lastItemPanel = (ActionEventItemPanel) verticalPanel.getComponent(lastItemIndex);
      SeatingPlanObj plan = planIntComboBox.getItemAt(planIntComboBox.getSelectedIndex());
      if (plan != null) {
        int totalSeats = (lastItemIndex + 2) * getSeatsNumber(plan);//общее кол-во мест во всех сеансах после добавления этого сеанса
        if (totalSeats > Env.MAX_ADD_EVENTS_NUMBER_SEATS) {
          JOptionPane.showMessageDialog(this, "Слишком много сеансов для добавления", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    }
    ActionEventItemPanel eventItemPanel = new ActionEventItemPanel();
    eventItemPanel.setGatewayEventList(gatewayEventList);
    eventItemPanel.setInfoButtonActionListener(infoButtonActionListener);
    eventItemPanel.addResizeListener(this);
    eventItemPanel.init(lastItemPanel);
    verticalPanel.add(eventItemPanel, lastItemIndex + 1);
    resizeScrollPane();
    pack();
  }

  private void addButton2ActionPerformed() {
    if (gatewayEventList == null) return;
    GatewayEventObj lastGatewayEvent = null;
    ActionEventItemPanel lastItemPanel = null;
    int lastItemIndex = verticalPanel.getComponentCount() - 2;
    if (lastItemIndex >= 0) {//уже есть item в панели
      lastItemPanel = (ActionEventItemPanel) verticalPanel.getComponent(lastItemIndex);
      if (lastItemPanel != null) lastGatewayEvent = lastItemPanel.getGatewayEvent();
    }
    if (lastGatewayEvent == null) {
      if (lastItemPanel != null) lastItemPanel.requestFocusGatewayEvent();
      JOptionPane.showMessageDialog(this, "Необходимо сначала указать связь", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    String action = lastGatewayEvent.getActionName();
    if (action == null) {
      JOptionPane.showMessageDialog(this, "Невозможно определить подобные сеансы", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    SeatingPlanObj plan = planIntComboBox.getItemAt(planIntComboBox.getSelectedIndex());
    if (plan == null) {
      planIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо сначала выбрать схему зала", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    AddActionEventLimitDialog limitDialog = new AddActionEventLimitDialog(this, action);
    limitDialog.setVisible(true);
    if (limitDialog.isCancelled()) return;
    Date limitDate = limitDialog.getResult();

    int items = verticalPanel.getComponentCount() - 1;
    int seats = getSeatsNumber(plan);//кол-во мест во одном сеансе
    int totalSeats = items * seats;//общее кол-во мест во всех сеансах
    Set<GatewayEventObj> gatewayEventSet = new HashSet<>(items);
    for (int i = 0; i < items; i++) {
      ActionEventItemPanel itemPanel = (ActionEventItemPanel) verticalPanel.getComponent(i);
      GatewayEventObj gatewayEvent = itemPanel.getGatewayEvent();
      if (gatewayEvent != null) gatewayEventSet.add(gatewayEvent);
    }

    List<GatewayEventObj> candidateList = new ArrayList<>();
    for (GatewayEventObj gatewayEvent : gatewayEventList) {
      if (!action.equals(gatewayEvent.getActionName())) continue;//другое название
      if (gatewayEventSet.contains(gatewayEvent)) continue;//уже введено выше
      if (ActionEventItemPanel.isExists(gatewayEvent)) continue;//уже есть на сервере (с галочкой)
      if (limitDate != null) {
        Date gatewayDate = GatewayEventObj.parseFormat(gatewayEvent.getDate());
        if (gatewayDate != null && gatewayDate.after(limitDate)) continue;//не входит в ограничение
      }
      candidateList.add(gatewayEvent);
    }
    int addedNumber = 0;
    boolean limitReached = false;
    for (GatewayEventObj gatewayEvent : candidateList) {
      totalSeats += seats;
      if (totalSeats > Env.MAX_ADD_EVENTS_NUMBER_SEATS) {
        limitReached = true;
        break;
      }
      ActionEventItemPanel eventItemPanel = new ActionEventItemPanel();
      eventItemPanel.setGatewayEventList(gatewayEventList);
      eventItemPanel.setInfoButtonActionListener(infoButtonActionListener);
      eventItemPanel.addResizeListener(this);
      eventItemPanel.init(lastItemPanel);
      eventItemPanel.setGatewayEvent(gatewayEvent);
      verticalPanel.add(eventItemPanel, ++lastItemIndex);
      addedNumber++;
    }
    if (addedNumber > 0) {
      resizeScrollPane();
      pack();
    }
    String res;
    if (addedNumber == 0) res = "Подобные сеансы не найдены";
    else {
      if (limitReached) res = "Достигнут предел сеансов для добавления.\n";
      else res = "";
      res += L10n.pluralForm(addedNumber, "Добавлен ", "Добавлено ", "Добавлено ");
      res += L10n.pluralVal(addedNumber, "подобный сеанс", "подобных сеанса", "подобных сеансов");
    }
    JOptionPane.showMessageDialog(this, res, "Результат", JOptionPane.INFORMATION_MESSAGE);
  }

  private void okButtonActionPerformed() {
    if (planIntComboBox.getSelectedIndex() == -1) {
      planIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать схему зала", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (actionIntComboBox.getSelectedIndex() == -1) {
      actionIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать представление", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    int items = verticalPanel.getComponentCount() - 1;
    if (items < 1) {
      JOptionPane.showMessageDialog(this, "Необходимо создать хотя бы один сеанс", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    final SeatingPlanObj plan = planIntComboBox.getItemAt(planIntComboBox.getSelectedIndex());
    final ActionObj action = actionIntComboBox.getItemAt(actionIntComboBox.getSelectedIndex());
    int totalSeats = items * getSeatsNumber(plan);//общее кол-во мест во всех сеансах
    if (totalSeats > Env.MAX_ADD_EVENTS_NUMBER_SEATS && items > 1) {
      JOptionPane.showMessageDialog(this, "Слишком много сеансов для добавления", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    GatewayObj gateway = ebsComboBox.getItemAt(ebsComboBox.getSelectedIndex());
    if (gateway.getOrganizerId() != null && gateway.getOrganizerId() != action.getOrganizerId()) {
      ebsComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "ВБС не пренадлежит организатору данного представления", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    afterSuccessAdding = new Runnable() {
      @Override
      public void run() {
        action.getCityIdSet().add(cityId);
        action.getVenueIdSet().add(plan.getVenueId());
      }
    };
    Set<GatewayEventObj> gatewayEventSet = new HashSet<>(items);
    GatewayEventObj gatewayEvent = null;
    ArrayList<ActionEventObj> actionEventList = new ArrayList<>(items);
    for (int i = 0; i < items; i++) {
      ActionEventItemPanel itemPanel = (ActionEventItemPanel) verticalPanel.getComponent(i);
      if (itemPanel.getShowDate() == null) {
        itemPanel.requestFocusShowDate();
        JOptionPane.showMessageDialog(this, "Начало сеанса не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (itemPanel.getSellStartDate() == null) {
        itemPanel.requestFocusSellStartDate();
        JOptionPane.showMessageDialog(this, "Начало продаж не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (itemPanel.getSellEndDate() == null) {
        itemPanel.requestFocusSellEndDate();
        JOptionPane.showMessageDialog(this, "Конец продаж не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (itemPanel.getSellStartDate().after(itemPanel.getShowDate())) {
        itemPanel.requestFocusSellStartDate();
        JOptionPane.showMessageDialog(this, "Начало продаж после начала сеанса", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (itemPanel.getSellEndDate().before(itemPanel.getSellStartDate())) {
        itemPanel.requestFocusSellEndDate();
        JOptionPane.showMessageDialog(this, "Конец продаж до начала продаж", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (gateway.getId() != 0) {
        gatewayEvent = itemPanel.getGatewayEvent();
        if (gatewayEvent == null) {
          itemPanel.requestFocusGatewayEvent();
          JOptionPane.showMessageDialog(this, "Связь с ВБС не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (!gatewayEventSet.add(gatewayEvent)) {
          itemPanel.requestFocusGatewayEvent();
          JOptionPane.showMessageDialog(this, "Дублирующая связь с ВБС", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        Date gatewayDate = GatewayEventObj.parseFormat(gatewayEvent.getDate());
        if (gatewayDate != null && !itemPanel.getShowDate().equals(gatewayDate)) {
          itemPanel.requestFocusGatewayEvent();
          JOptionPane.showMessageDialog(this, "Даты начала сеансов не совпадают", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      ActionEventObj actionEvent = new ActionEventObj(0);
      actionEvent.setPlacementPlan(plan.isPlacement());
      actionEvent.setPlanId(plan.getId());
      actionEvent.setActionId(action.getId());
      actionEvent.setETickets(eTicketsCheckBox.isSelected());
      actionEvent.setFullNameRequired(false);//по умолчанию
      actionEvent.setPhoneRequired(false);//по умолчанию
      actionEvent.setTicketRefundAllowed(true);//по умолчанию
      actionEvent.setTicketReissueAllowed(false);//по умолчанию
      actionEvent.setMaxReserveTime(null);//по умолчанию
      actionEvent.setVat(BigDecimal.ZERO);//по умолчанию
      actionEvent.setSellEnabled(sellEnabledCheckBox.isSelected());
      actionEvent.setQuota(quotaCheckBox.isSelected());
      //noinspection ConstantConditions
      actionEvent.setShowTime(itemPanel.getShowDateFormatted());
      //noinspection ConstantConditions
      actionEvent.setSellStartTime(itemPanel.getSellStartDateFormatted());
      //noinspection ConstantConditions
      actionEvent.setSellEndTime(itemPanel.getSellEndDateFormatted());
      if (gatewayEvent != null) actionEvent.setGatewayEvent(gatewayEvent);
      actionEventList.add(actionEvent);
    }
    List<CategoryObj> categoryList = plan.getCategoryList();
    if (quotaCheckBox.isSelected() || ebsComboBox.getItemAt(ebsComboBox.getSelectedIndex()).getId() != 0) {
      ArrayList<CategoryPriceObj> priceList = new ArrayList<>(categoryList.size());
      for (CategoryObj category : categoryList) {
        CategoryPriceObj categoryPrice = new CategoryPriceObj(category.getId());
        categoryPrice.setPrice(category.getInitPrice() != null ? category.getInitPrice() : BigDecimal.ZERO);
        priceList.add(categoryPrice);
      }
      for (ActionEventObj actionEvent : actionEventList) {
        actionEvent.setPriceList(priceList);
      }
      ArrayList<ActionEventElement> actionEventElementList = new ArrayList<>(actionEventList.size());
      for (ActionEventObj actionEvent : actionEventList) {
        actionEventElementList.add(new ActionEventElement(actionEvent));
      }
      Collections.sort(actionEventElementList);
      actionEventList = new ArrayList<>(actionEventElementList.size());
      for (ActionEventElement actionEventElement : actionEventElementList) {
        actionEventList.add(actionEventElement.getActionEvent());
      }
      waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
      Env.net.create("ADD_ACTION_EVENT", new Request(actionEventList), this, true, 30000).start();
    } else {
      new AddActionEventPriceDialog(this, childlessControl, actionEventComboBox, categoryList, actionEventList, afterSuccessAdding).setVisible(true);
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
    planIntComboBox = new SuggestionComboBox<>();
    JLabel label2 = new JLabel();
    actionIntComboBox = new SuggestionComboBox<>();
    JPanel check1Panel = new JPanel();
    quotaCheckBox = new JCheckBox();
    ebsLabel = new JLabel();
    ebsComboBox = new SuggestionComboBox<>();
    JPanel check2Panel = new JPanel();
    eTicketsCheckBox = new JCheckBox();
    sellEnabledCheckBox = new JCheckBox();
    listButton = new JButton();
    scrollPane = new JScrollPane();
    verticalPanel = new JPanel();
    JPanel plusPanel = new JPanel();
    JButton addButton = new JButton();
    addButton2 = new JButton();
    JPanel buttonBar = new JPanel();
    okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0435\u0430\u043d\u0441\u044b...");
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
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u0421\u0445\u0435\u043c\u0430 \u0437\u0430\u043b\u0430:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(planIntComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label2 ----
        label2.setText("\u041f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435:");
        contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(actionIntComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== check1Panel ========
        {
          check1Panel.setLayout(new GridBagLayout());
          ((GridBagLayout)check1Panel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
          ((GridBagLayout)check1Panel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)check1Panel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
          ((GridBagLayout)check1Panel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- quotaCheckBox ----
          quotaCheckBox.setText("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043a\u0432\u043e\u0442\u0443");
          quotaCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              quotaCheckBoxItemStateChanged(e);
            }
          });
          check1Panel.add(quotaCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- ebsLabel ----
          ebsLabel.setText("\u0412\u0411\u0421:");
          check1Panel.add(ebsLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- ebsComboBox ----
          ebsComboBox.setMaximumRowCount(12);
          ebsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              ebsComboBoxItemStateChanged(e);
            }
          });
          check1Panel.add(ebsComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(check1Panel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== check2Panel ========
        {
          check2Panel.setLayout(new GridBagLayout());
          ((GridBagLayout)check2Panel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
          ((GridBagLayout)check2Panel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)check2Panel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
          ((GridBagLayout)check2Panel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- eTicketsCheckBox ----
          eTicketsCheckBox.setText("\u041c\u043e\u0431\u0438\u043b\u044c\u043d\u044b\u0439 \u044d\u043b\u0435\u043a\u0442\u0440\u043e\u043d\u043d\u044b\u0439 \u0431\u0438\u043b\u0435\u0442 (\u041c\u042d\u0411)");
          eTicketsCheckBox.setSelected(true);
          check2Panel.add(eTicketsCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- sellEnabledCheckBox ----
          sellEnabledCheckBox.setText("\u041f\u0440\u043e\u0434\u0430\u0436\u0430 \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d\u0430");
          check2Panel.add(sellEnabledCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- listButton ----
          listButton.setIcon(new ImageIcon(getClass().getResource("/resources/list.png")));
          listButton.setMargin(new Insets(1, 1, 1, 1));
          listButton.setToolTipText("\u0421\u043f\u0438\u0441\u043e\u043a \u043e\u0442 \u0412\u0411\u0421");
          listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              listButtonActionPerformed();
            }
          });
          check2Panel.add(listButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(check2Panel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane ========
        {
          scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
          scrollPane.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.black),
            new EmptyBorder(5, 0, 0, 0)));

          //======== verticalPanel ========
          {
            verticalPanel.setLayout(new VerticalLayout());

            //======== plusPanel ========
            {
              plusPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
              plusPanel.setLayout(new GridBagLayout());
              ((GridBagLayout)plusPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
              ((GridBagLayout)plusPanel.getLayout()).rowHeights = new int[] {0, 0};
              ((GridBagLayout)plusPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
              ((GridBagLayout)plusPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

              //---- addButton ----
              addButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
              addButton.setMargin(new Insets(1, 1, 1, 1));
              addButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0435\u0430\u043d\u0441");
              addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  addButtonActionPerformed();
                }
              });
              plusPanel.add(addButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

              //---- addButton2 ----
              addButton2.setIcon(new ImageIcon(getClass().getResource("/resources/plus2.png")));
              addButton2.setMargin(new Insets(1, 1, 1, 1));
              addButton2.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043f\u043e\u0434\u043e\u0431\u043d\u044b\u0435 \u0441\u0435\u0430\u043d\u0441\u044b");
              addButton2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  addButton2ActionPerformed();
                }
              });
              plusPanel.add(addButton2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
            }
            verticalPanel.add(plusPanel);
          }
          scrollPane.setViewportView(verticalPanel);
        }
        contentPanel.add(scrollPane, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
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
        okButton.setText("\u0414\u0430\u043b\u0435\u0435...");
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

  private void resizeScrollPane() {
    int width = verticalPanel.getPreferredSize().width;
    int height = verticalPanel.getPreferredSize().height;
    if (scrollPane.getBorder() != null) {
      Insets insets = scrollPane.getBorder().getBorderInsets(scrollPane);
      width += insets.left + insets.right;
      height += insets.top + insets.bottom;
    }
    if (this.isVisible()) {
      Rectangle gcBounds = this.getGraphicsConfiguration().getBounds();
      int gcBottom = gcBounds.y + gcBounds.height;//низ текущего экрана
      int bottom = (int) (gcBottom * 0.9);//нижняя граница для включения прокрутки
      int scrollPaneBottom = scrollPane.getLocationOnScreen().y + height;//низ scrollPane
      if (scrollPaneBottom > bottom) {
        width += scrollPane.getVerticalScrollBar().getPreferredSize().width;
        height -= (scrollPaneBottom - bottom);
        height = Math.max(height, 50);
      }
    }
    scrollPane.setPreferredSize(new Dimension(width, height));
  }

  @Override
  public void needResize(ResizeEvent event) {
    resizeScrollPane();
    pack();
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (event.getCommand().equals("GET_GATEWAY_EVENT_LIST")) {
      if (state == Network.State.FINISHED) {
        ebsComboBox.setEnabled(true);
        okButton.setEnabled(true);
      }
    } else if (event.getCommand().equals("ADD_ACTION_EVENT")) {
      if (state == Network.State.STARTED) waitingDialog.setVisible(true);
      if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (result.getCommand().equals("GET_GATEWAY_EVENT_LIST")) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(AddActionEventDialog.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        gatewayEventList = Collections.emptyList();
      } else {
        List<GatewayEventObj> data = (List<GatewayEventObj>) result.getResponse().getData();
        gatewayEventList = new ArrayList<>(data.size());
        int index = 0;
        for (GatewayEventObj gatewayEventObj : data) {
          if (ActionEventItemPanel.isExists(gatewayEventObj)) {
            gatewayEventList.add(gatewayEventObj);
          } else {
            gatewayEventList.add(index, gatewayEventObj);
            index++;
          }
        }
      }
      updateActionEventItemPanelsByGatewayEventList();
    } else if (result.getCommand().equals("ADD_ACTION_EVENT")) {
      if (!result.getResponse().isSuccess()) {
        JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      afterSuccessAdding.run();
      List<ActionEventObj> actionEventList = (List<ActionEventObj>) result.getResponse().getData();
      boolean first = true;
      for (ActionEventObj actionEventObj : actionEventList) {
        actionEventComboBox.addElement(actionEventObj, first);
        Env.addGatewayEvent(actionEventObj);
        first = false;
      }
      childlessControl.updateChildless();
      this.dispose();
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.getCommand().equals("GET_GATEWAY_EVENT_LIST")) {
      gatewayEventList = Collections.emptyList();
      updateActionEventItemPanelsByGatewayEventList();
      JOptionPane.showMessageDialog(AddActionEventDialog.this, "Не удалось загрузить список сеансов ВБС", "Ошибка", JOptionPane.ERROR_MESSAGE);
    } else if (error.getCommand().equals("ADD_ACTION_EVENT")) {
      if (error.isDataSent())
        JOptionPane.showMessageDialog(this, "Команда отправлена на сервер, но подтверждение не было получено", "Ошибка", JOptionPane.ERROR_MESSAGE);
      else
        JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить сеансы", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static int getSeatsNumber(@NotNull SeatingPlanObj plan) {
    int res = 0;
    for (CategoryObj categoryObj : plan.getCategoryList()) {
      res += categoryObj.getSeatsNumber();
    }
    return res;
  }

  private class InfoButtonActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      final ActionEventItemPanel eventItemPanel = (ActionEventItemPanel) e.getSource();
      GatewayEventObj gatewayEvent = eventItemPanel.getGatewayEvent();
      if (gatewayEvent == null) return;
      eventItemPanel.setEnabledInfoButton(false);
      Env.net.create("GET_GATEWAY_EVENT_INFO", new Request(gatewayEvent), new NetListener<Request, Response>() {
        @Override
        public void netState(NetEvent<Request, Response> event, Network.State state) {
          if (state == Network.State.FINISHED) {
            eventItemPanel.setEnabledInfoButton(true);
          }
        }

        @Override
        public void netResult(NetResultEvent<Request, Response> result) {
          if (!result.getResponse().isSuccess()) {
            JOptionPane.showMessageDialog(AddActionEventDialog.this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String report = (String) result.getResponse().getData();
          ScrollOptionPane.showMessageDialog(AddActionEventDialog.this, report, "Доп. информация от ВБС", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.LEFT));
        }

        @Override
        public void netError(NetErrorEvent<Request, Response> error) {
          JOptionPane.showMessageDialog(AddActionEventDialog.this, "Не удалось загрузить доп. информацию по сеансу ВБС", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      }, Env.GATEWAY_TIMEOUT).start();
    }
  }
}
