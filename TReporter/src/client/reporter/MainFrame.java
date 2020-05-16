/*
 * Created by JFormDesigner on Wed Oct 21 14:14:19 MSK 2015
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.component.*;
import client.component.suggestion.SuggestionComboBox;
import client.component.summary.JXSummaryTable;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.reporter.component.StatusBarPanel;
import client.reporter.component.renderer.*;
import client.reporter.model.*;
import client.reporter.svn.SvnRevision;
import client.utils.*;
import org.apache.commons.csv.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ExcelReportException;
import report.reporter.managers.*;
import report.utils.StaticMethods;
import server.protocol2.*;
import server.protocol2.common.*;
import server.protocol2.reporter.*;

import static client.reporter.Env.net;
import static client.reporter.Env.user;

/**
 * @author Maksim
 */
public class MainFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<AcquiringObj> acquiringComboBox;
  private SuggestionComboBox<ROrganizer> organizerComboBox;
  private SuggestionComboBox<RAgent> agentComboBox;
  private SuggestionComboBox<RFrontend> frontendComboBox;
  private SuggestionComboBox<RCity> cityComboBox;
  private SuggestionComboBox<RVenue> venueComboBox;
  private JCheckBox actualCheckBox;
  private SuggestionComboBox<RAction> actionComboBox;
  private SuggestionComboBox<RActionEvent> actionEventComboBox;
  private JCheckBox fullCheckBox;
  private SuggestionComboBox<GSystemObj> gSystemComboBox;
  private SuggestionComboBox<GatewayObj> gatewayComboBox;
  private JLabel periodLabel;
  private JComboBox<PeriodType> periodTypeComboBox;
  private JLabel fromLabel;
  private JXDatePicker fromDatePicker;
  private JLabel toLabel;
  private JXDatePicker toDatePicker;
  private JCheckBox allStatusesCheckBox;
  private JCheckBox lightRequestCheckBox;
  private JButton getButton;
  private JSplitPane splitPane;
  private JXSummaryTable orderTable;
  private JXSummaryTable ticketTable;
  private StatusBarPanel statusBarPanel;
  private JMenu reportsMenu;
  private JMenu quotaMenu;
  private JMenu cashierWorkShiftMenu;
  private JMenu statsMenu;
  private JPopupMenu orderPopupMenu;
  private JMenuItem orderMenuItem1;
  private JMenuItem orderMenuItem2;
  private JPopupMenu ticketPopupMenu;
  private JMenuItem ticketMenuItem1;
  private JMenuItem ticketMenuItem2;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  private final Rectangle gcBounds;
  private final WaitingDialog waitingDialog;
  private final DateFormat requestFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private final AcquiringObj defAcquiring = AcquiringObj.getInstance(-11, "Эквайринг: любое значение");
  private final AcquiringObj anyAcquiring = AcquiringObj.getInstance(-1, "Любой эквайринг");
  private final ROrganizer defOrganizer = new ROrganizer(0, "Организатор: любой");
  private final RAgent defAgent = new RAgent(0, "Агент: любой");
  private final RFrontend defFrontend = new RFrontend(0, 0, "Интерфейс: любой", FrontendType.getInstance(0, ""));
  private final RCity defCity = new RCity(0, "Город: любой");
  private final RVenue defVenue = new RVenue(0, 0, "Место: любое");
  private final RAction defAction = new RAction(0, 0, "", KindObj.getInstance(-1, ""), "Представление: любое");
  private final RActionEvent defActionEvent = new RActionEvent(0, 0, "Сеанс: любой");
  private final GSystemObj defSystem = GSystemObj.getInstance(-11, "Шлюз в ВБС: любое значение");
  private final GSystemObj anySystem = GSystemObj.getInstance(-1, "Любой шлюз в ВБС");
  private final GatewayObj defGateway = GatewayObj.getInstance(-11, -11, "Подключение к ВБС: любое значение", null, null);
  private final GatewayObj anyGateway = GatewayObj.getInstance(-1, -1, "Любое подключение к ВБС", null, null);
  private final OrderTableModel orderTableModel = new OrderTableModel();
  private final TicketTableModel ticketTableModel = new TicketTableModel();
  private boolean firstLoad = true;
  private List<AcquiringObj> acquiringList = Collections.emptyList();
  private List<ROrganizer> organizerList = Collections.emptyList();
  private List<RAgent> agentList = Collections.emptyList();
  private List<RFrontend> frontendList = Collections.emptyList();
  private List<RCity> cityList = Collections.emptyList();
  private List<RVenue> venueList = Collections.emptyList();
  private List<RAction> actionList = Collections.emptyList();
  private List<RActionEvent> actionEventList = Collections.emptyList();
  private List<GSystemObj> gSystemList = Collections.emptyList();
  private List<GatewayObj> gatewayList = Collections.emptyList();
  private double dividerLocation = 0.7d;
  private ReportParamsFrame reportParamsFrame = null;
  private QuotaInvoiceFrame quotaInvoiceFrame = null;
  private QuotaReportFrame quotaReportFrame = null;
  private PassReportFrame passReportFrame = null;
  private MecReportFrame mecReportFrame = null;
  private CashierWorkShiftFrame cashierWorkShiftFrame = null;
  private QueryStatsFrame queryStatsFrame = null;
  @Nullable
  private FileChooser exportTicketsDialog;

  public MainFrame() {
    initComponents();
    setTitle(getTitle() + Env.ver + " build " + SvnRevision.SVN_REV + " от " + SvnRevision.SVN_REV_DATE);
    if (Env.testZone) setTitle(getTitle() + " [Тестовая зона]");
    BuildManager.setSign(StaticMethods.generateSign(user.getUserType().getDesc(), user.getEmail()));

    for (final EForm form : EForm.values()) {
      JMenuItem menuItem = new JMenuItem(form.getName());
      menuItem.setIcon(Env.excelIcon);
      menuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          reportMenuItemActionPerformed(form);
        }
      });
      reportsMenu.add(menuItem);
    }

    if (user.getUserType() == UserType.AGENT) quotaMenu.setVisible(false);
    if (user.getUserType() != UserType.OPERATOR && user.getUserType() != UserType.AGENT) {
      statsMenu.setVisible(false);
      cashierWorkShiftMenu.setVisible(false);
    }
    if (user.getUserType() != UserType.OPERATOR) orderMenuItem2.setVisible(false);

    gcBounds = this.getGraphicsConfiguration().getBounds();
//    gcBounds = new Rectangle(1600, 900);//проверить
//    gcBounds = new Rectangle(1366, 768);//проверить
//    gcBounds = new Rectangle(1024, 600); //Dimension frameSize = new Dimension(1024, 600);//проверить
    Dimension frameSize = new Dimension((int) (gcBounds.width * 0.9), (int) (gcBounds.height * 0.8));

    acquiringComboBox.addItem(defAcquiring);
    organizerComboBox.addItem(defOrganizer);//вызывается organizerComboBoxItemStateChanged
    agentComboBox.addItem(defAgent);//вызывается agentComboBoxItemStateChanged
    cityComboBox.addItem(defCity);//вызывается cityComboBoxItemStateChanged
    gSystemComboBox.addItem(defSystem);//вызывается gSystemComboBoxItemStateChanged
    gatewayComboBox.addItem(defGateway);

    AcquiringListRenderer acquiringListRenderer = new AcquiringListRenderer();
    OrganizerListRenderer organizerListRenderer;
    AgentListRenderer agentListRenderer;
    FrontendListRenderer frontendListRenderer;
    VenueListRenderer venueListRenderer;
    ActionListRenderer actionListRenderer;
    GSystemListRenderer systemListRenderer = new GSystemListRenderer();
    GatewayListRenderer gatewayListRenderer;
    if (gcBounds.height < 768) {
      organizerListRenderer = new OrganizerListRenderer(28);
      agentListRenderer = new AgentListRenderer(28);
      frontendListRenderer = new FrontendListRenderer(28);
      venueListRenderer = new VenueListRenderer(28);
      actionListRenderer = new ActionListRenderer(28);
      gatewayListRenderer = new GatewayListRenderer(28);
    } else if (gcBounds.height < 900) {
      organizerListRenderer = new OrganizerListRenderer(40);
      agentListRenderer = new AgentListRenderer(40);
      frontendListRenderer = new FrontendListRenderer(40);
      venueListRenderer = new VenueListRenderer(40);
      actionListRenderer = new ActionListRenderer(45);
      gatewayListRenderer = new GatewayListRenderer(40);
    } else {
      organizerListRenderer = new OrganizerListRenderer(50);
      agentListRenderer = new AgentListRenderer(50);
      frontendListRenderer = new FrontendListRenderer(50);
      venueListRenderer = new VenueListRenderer(50);
      actionListRenderer = new ActionListRenderer(70);
      gatewayListRenderer = new GatewayListRenderer(50);
    }
    actionListRenderer.setShowOrganizer(user.getUserType() != UserType.ORGANIZER);
    acquiringComboBox.setRenderer(acquiringListRenderer);
    acquiringComboBox.setElementToStringConverter(acquiringListRenderer);
    organizerComboBox.setRenderer(organizerListRenderer);
    organizerComboBox.setElementToStringConverter(organizerListRenderer);
    agentComboBox.setRenderer(agentListRenderer);
    agentComboBox.setElementToStringConverter(agentListRenderer);
    frontendComboBox.setRenderer(frontendListRenderer);
    frontendComboBox.setElementToStringConverter(frontendListRenderer);
    venueComboBox.setRenderer(venueListRenderer);
    venueComboBox.setElementToStringConverter(venueListRenderer);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);
    actionEventComboBox.setRenderer(new ActionEventListRenderer());
    gSystemComboBox.setRenderer(systemListRenderer);
    gSystemComboBox.setElementToStringConverter(systemListRenderer);
    gatewayComboBox.setRenderer(gatewayListRenderer);
    gatewayComboBox.setElementToStringConverter(gatewayListRenderer);

    statusBarPanel.setUserType(user.getUserType().getDesc());
    statusBarPanel.setAuthorityName(user.getAuthorityName());
    statusBarPanel.addReloadButtonActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        net.create("GET_INIT_REPORTER", new Request(null), MainFrame.this).start();
      }
    });
    net.addPoolListener(statusBarPanel, Network.EventMode.EDT_INVOKE_LATER);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

    orderTable.setModel(orderTableModel);
    orderTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    orderTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) orderTableSelectionChanged();
      }
    });
    orderTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));
    orderTable.addHighlighter(new ColorHighlighter(new OrderStatusPredicate(orderTableModel, OrderObj.Status.PROCESSING), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    orderTable.addHighlighter(new ColorHighlighter(new OrderStatusPredicate(orderTableModel, OrderObj.Status.PROCESSING_GATEWAY), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    orderTable.addHighlighter(new ColorHighlighter(new OrderStatusPredicate(orderTableModel, OrderObj.Status.CANCELLING_GATEWAY), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    orderTable.addHighlighter(new ColorHighlighter(new OrderStatusPredicate(orderTableModel, OrderObj.Status.CANCELLED), new Color(255, 209, 155), Color.BLACK, new Color(210, 200, 179), Color.BLACK));
    orderTable.addHighlighter(new ColorHighlighter(new NotSentOrderPredicate(orderTableModel), new Color(255, 255, 105), Color.BLACK, new Color(195, 195, 92), Color.BLACK));
    showDatesColumns(false);
    showFilteredColumns(false);

    ticketTable.setModel(ticketTableModel);
    ticketTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    ticketTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));
    ticketTable.addHighlighter(new ColorHighlighter(new TicketHolderStatusPredicate(ticketTableModel, TicketObj.HolderStatus.REFUND_GATEWAY), new Color(255, 255, 105), Color.BLACK, new Color(195, 195, 92), Color.BLACK));
    ticketTable.addHighlighter(new ColorHighlighter(new TicketHolderStatusPredicate(ticketTableModel, TicketObj.HolderStatus.REFUND), new Color(255, 209, 155), Color.BLACK, new Color(210, 200, 179), Color.BLACK));
    ticketTable.addHighlighter(new ColorHighlighter(new TicketHolderStatusPredicate(ticketTableModel, TicketObj.HolderStatus.CHECK_IN), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    ticketTable.addHighlighter(new ColorHighlighter(new TicketHolderStatusPredicate(ticketTableModel, TicketObj.HolderStatus.CHECK_IN_BY_CONTROLLER), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    ticketTable.addHighlighter(new ColorHighlighter(new TicketHolderStatusPredicate(ticketTableModel, TicketObj.HolderStatus.CHECK_OUT), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    showDiscountReasonColumns(false);

    setPreferredSize(frameSize);
    pack();
    setLocationRelativeTo(null);
    orderTable.packAll();
    ticketTable.packAll();
    getButton.requestFocus();
  }

  public void startWork() {
    this.setVisible(true);
    net.create("GET_INIT_REPORTER", new Request(null), this).start();
  }

  @SuppressWarnings("unchecked")
  private void loadData(@NotNull Object[] data) {
    user = (LoginUser) data[0];
    Object[] reportData = (Object[]) data[1];
    acquiringList = Collections.unmodifiableList((List<AcquiringObj>) reportData[0]);
    organizerList = Collections.unmodifiableList((List<ROrganizer>) reportData[1]);
    agentList = Collections.unmodifiableList((List<RAgent>) reportData[2]);
    frontendList = Collections.unmodifiableList((List<RFrontend>) reportData[3]);
    cityList = Collections.unmodifiableList((List<RCity>) reportData[4]);
    venueList = Collections.unmodifiableList((List<RVenue>) reportData[5]);
    actionList = Collections.unmodifiableList((List<RAction>) reportData[6]);
    actionEventList = Collections.unmodifiableList((List<RActionEvent>) reportData[7]);
    gSystemList = Collections.unmodifiableList((List<GSystemObj>) reportData[10]);
    gatewayList = Collections.unmodifiableList((List<GatewayObj>) reportData[8]);
    Env.agentAcquiring = (AcquiringObj) reportData[9];

    acquiringComboBox.removeAllItems();
    acquiringComboBox.addItem(defAcquiring);
    acquiringComboBox.addItem(anyAcquiring);
    for (AcquiringObj acquiring : acquiringList) {
      acquiringComboBox.addItem(acquiring);
    }
    organizerComboBox.removeAllItems();
    organizerComboBox.addItem(defOrganizer);//вызывается organizerComboBoxItemStateChanged
    for (ROrganizer organizer : organizerList) {
      organizerComboBox.addItem(organizer);
    }
    agentComboBox.removeAllItems();
    agentComboBox.addItem(defAgent);//вызывается agentComboBoxItemStateChanged
    for (RAgent agent : agentList) {
      agentComboBox.addItem(agent);
    }
    cityComboBox.removeAllItems();
    cityComboBox.addItem(defCity);//вызывается cityComboBoxItemStateChanged
    for (RCity city : cityList) {
      cityComboBox.addItem(city);
    }
    gSystemComboBox.removeAllItems();
    gSystemComboBox.addItem(defSystem);//вызывается gSystemComboBoxItemStateChanged
    gSystemComboBox.addItem(anySystem);
    for (GSystemObj system : gSystemList) {
      gSystemComboBox.addItem(system);
    }

    orderTableModel.setData(Collections.<OrderObj>emptyList());
    if (quotaInvoiceFrame != null) {
      quotaInvoiceFrame.dispose();
      quotaInvoiceFrame = null;
    }
    if (quotaReportFrame != null) {
      quotaReportFrame.dispose();
      quotaReportFrame = null;
    }
    if (passReportFrame != null) {
      passReportFrame.dispose();
      passReportFrame = null;
    }
    if (mecReportFrame != null) {
      mecReportFrame.dispose();
      mecReportFrame = null;
    }
    if (queryStatsFrame != null) {
      queryStatsFrame.dispose();
      queryStatsFrame = null;
    }
    reportsMenu.setEnabled(false);

    if (user.getUserType() == UserType.ORGANIZER) {
      for (ROrganizer organizer : organizerList) {
        if (organizer.getId() == user.getAuthorityId()) {
          organizerComboBox.setSelectedItem(organizer);
          organizerComboBox.setEnabled(false);
          break;
        }
      }
    }
    if (user.getUserType() == UserType.AGENT) {
      for (RAgent agent : agentList) {
        if (agent.getId() == user.getAuthorityId()) {
          agentComboBox.setSelectedItem(agent);
          if (Env.agentAcquiring == null) agentComboBox.setEnabled(false);
          break;
        }
      }
    }
    if (firstLoad) {
      pack();
      //при уменьшении ширины окна
      Dimension preferredSize;
      preferredSize = organizerComboBox.getPreferredSize();
      organizerComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));
      preferredSize = agentComboBox.getPreferredSize();
      agentComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));
      preferredSize = frontendComboBox.getPreferredSize();
      frontendComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));
      preferredSize = venueComboBox.getPreferredSize();
      venueComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));
      preferredSize = actionComboBox.getPreferredSize();
      actionComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));
      preferredSize = gatewayComboBox.getPreferredSize();
      gatewayComboBox.setMinimumSize(new Dimension(preferredSize.width * 2 / 3, preferredSize.height));

      //фиксируем размеры для фильтрации
      frontendComboBox.setPreferredSize(frontendComboBox.getSize());
      venueComboBox.setPreferredSize(venueComboBox.getSize());
      actionComboBox.setPreferredSize(actionComboBox.getSize());
      gatewayComboBox.setPreferredSize(gatewayComboBox.getSize());
      actualCheckBox.setSelected(true);

      if (gcBounds.height < 768) setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
      firstLoad = false;
    }
  }

  private void reportMenuItemActionPerformed(@NotNull EForm form) {
    if (form.getType() == EForm.Type.FILTER) {
      if (form == EForm.FORM_5 || form == EForm.FORM_11 || form == EForm.FORM_13 || form == EForm.FORM_16 || form == EForm.FORM_21 || form == EForm.FORM_23) {
        try {
          BuildManager.build(form);
        } catch (IOException | ExcelReportException e) {
          JOptionPane.showMessageDialog(this, "Не удалось открыть файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      } else {
        new ReportFilterDialog(this, form).setVisible(true);
      }
    } else if (form.getType() == EForm.Type.QUOTA_SALE || form.getType() == EForm.Type.QUOTA) {
      invoiceMenuItemActionPerformed();
    } else if (form.getType() == EForm.Type.INVOICE) {
      invoiceMenuItemActionPerformed();
    } else if (form.getType() == EForm.Type.CASHIER_WORK_SHIFT) {
      cashierWorkShiftMenuItemActionPerformed();
    }
  }

  private void reportMenuItemActionPerformed() {
    if (reportParamsFrame == null) reportParamsFrame = new ReportParamsFrame(this);
    reportParamsFrame.setVisible(true);
  }

  private void invoiceMenuItemActionPerformed() {
    if (quotaInvoiceFrame == null) quotaInvoiceFrame = new QuotaInvoiceFrame(this, actionList, actionEventList);
    quotaInvoiceFrame.setVisible(true);
  }

  private void quotaReportMenuItemActionPerformed() {
    if (quotaReportFrame == null) quotaReportFrame = new QuotaReportFrame(this, actionList, actionEventList);
    quotaReportFrame.setVisible(true);
  }

  private void passMenuItemActionPerformed() {
    if (passReportFrame == null) passReportFrame = new PassReportFrame(this, cityList, venueList, actionList, actionEventList);
    passReportFrame.setVisible(true);
  }

  private void mecMenuItemActionPerformed() {
    if (mecReportFrame == null) mecReportFrame = new MecReportFrame(this, actionList);
    mecReportFrame.setVisible(true);
  }

  private void cashierWorkShiftMenuItemActionPerformed() {
    if (cashierWorkShiftFrame == null) cashierWorkShiftFrame = new CashierWorkShiftFrame(this);
    cashierWorkShiftFrame.setVisible(true);
  }

  private void queryStatsItemActionPerformed() {
    if (queryStatsFrame == null) queryStatsFrame = new QueryStatsFrame(this, frontendList);
    queryStatsFrame.setVisible(true);
  }

  private void getButtonActionPerformed() {
    AcquiringObj acquiring = acquiringComboBox.getItemAt(acquiringComboBox.getSelectedIndex());
    ROrganizer organizer = organizerComboBox.getItemAt(organizerComboBox.getSelectedIndex());
    RAgent agent = agentComboBox.getItemAt(agentComboBox.getSelectedIndex());
    RFrontend frontend = frontendComboBox.getItemAt(frontendComboBox.getSelectedIndex());
    RCity city = cityComboBox.getItemAt(cityComboBox.getSelectedIndex());
    RVenue venue = venueComboBox.getItemAt(venueComboBox.getSelectedIndex());
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    RActionEvent actionEvent = actionEventComboBox.getItemAt(actionEventComboBox.getSelectedIndex());
    GSystemObj system = gSystemComboBox.getItemAt(gSystemComboBox.getSelectedIndex());
    GatewayObj gateway = gatewayComboBox.getItemAt(gatewayComboBox.getSelectedIndex());

    FilterManager.setAcquiring(acquiring);
    FilterManager.setOrganizer(organizer);
    FilterManager.setCity(city);
    FilterManager.setVenue(venue);
    FilterManager.setAction(action);
    FilterManager.setActionEvent(actionEvent);
    FilterManager.setAgent(agent);
    FilterManager.setFrontend(frontend);
    FilterManager.setSystem(system);
    FilterManager.setGateway(gateway);
    FilterManager.setFullReport(fullCheckBox.isSelected());
    FilterManager.setAllStatuses(allStatusesCheckBox.isSelected());

    if (!fullCheckBox.isSelected()) {
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
      PeriodType periodType = periodTypeComboBox.getItemAt(periodTypeComboBox.getSelectedIndex());
      FilterManager.setDateFrom(date1);
      FilterManager.setDateTo(date2);
      FilterManager.setPeriodType(periodType.name());
      Object[] obj = new Object[14];
      obj[0] = lightRequestCheckBox.isSelected();
      obj[1] = requestFormat.format(date1);
      obj[2] = requestFormat.format(date2);
      obj[3] = allStatusesCheckBox.isSelected();
      if (acquiring != defAcquiring) obj[4] = acquiring.getId();
      if (organizer != defOrganizer) obj[5] = organizer.getId();
      if (agent != defAgent) obj[6] = agent.getId();
      if (frontend != defFrontend) obj[7] = frontend.getId();
      if (city != defCity) obj[8] = city.getId();
      if (venue != defVenue) obj[9] = venue.getId();
      if (action != defAction) obj[10] = action.getId();
      if (actionEvent != defActionEvent) obj[11] = actionEvent.getId();
      if (gateway != defGateway) obj[12] = gateway.getId();
      if (system != defSystem) obj[13] = system.getId();
      if (periodType == PeriodType.SALES) {
        net.create("GET_ORDER_LIST_1", new Request(obj), this, 60000).start();
      } else if (periodType == PeriodType.SHOWS) {
        net.create("GET_ORDER_LIST_2", new Request(obj), this, 60000).start();
      }
    } else {
      FilterManager.setDateFrom(null);
      FilterManager.setDateTo(null);
      FilterManager.setPeriodType(null);
      Object[] obj = new Object[8];
      obj[0] = lightRequestCheckBox.isSelected();
      obj[1] = action.getId();
      if (actionEvent != defActionEvent) obj[2] = actionEvent.getId();
      obj[3] = allStatusesCheckBox.isSelected();//не используется
      if (acquiring != defAcquiring) obj[4] = acquiring.getId();
      if (organizer != defOrganizer) obj[5] = organizer.getId();
      if (agent != defAgent) obj[6] = agent.getId();
      if (frontend != defFrontend) obj[7] = frontend.getId();
      net.create("GET_ORDER_LIST_3", new Request(obj), this, 60000).start();
    }
  }

  private void orderTableMousePopup(MouseEvent e) {
    int activeRow;
    JXTable table = (JXTable) e.getSource();
    activeRow = table.rowAtPoint(new Point(e.getX(), e.getY()));
    if (activeRow == -1) {
      table.clearSelection();
    } else {
      if (e.isPopupTrigger()) {
        if (table.convertRowIndexToModel(activeRow) == table.getModel().getRowCount() - 1) return;
        if (!table.isRowSelected(activeRow)) table.setRowSelectionInterval(activeRow, activeRow);
        orderMenuItem1.setEnabled(table.getSelectedRowCount() == 1);
        orderMenuItem2.setEnabled(table.getSelectedRowCount() == 1);
        orderPopupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private void orderTableSelectionChanged() {
    int[] rows = orderTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = orderTable.convertRowIndexToModel(rows[i]);
    }
    List<TicketObj> ticketList = orderTableModel.getTickets(modelRows);
    ticketTableModel.setData(ticketList);
    showDiscountReasonColumns(ticketTableModel.isDiscountReason());
    ticketTable.packAll();
  }

  private void orderMenuItem1ActionPerformed() {
    int[] rows = orderTable.getSelectedRows();
    if (rows.length != 1) return;//можно отправить только один
    int modelRow = orderTable.convertRowIndexToModel(rows[0]);
    OrderObj order = orderTableModel.getOrder(modelRow);
    String mail = order.getEmail();
    if (mail == null) mail = "";
    mail = (String) JOptionPane.showInputDialog(this, "Отправить заказ на следующий адрес:\n(текущий адрес заказа будет изменен)\n\n",
        "Отправка заказа", JOptionPane.PLAIN_MESSAGE, null, null, mail);
    if (mail != null) mail = mail.trim();
    if (mail != null && !mail.isEmpty()) {
      net.create("RESEND_ORDER", new Request(new Object[]{order.getId(), mail}), this).start();
    }
  }

  private void orderMenuItem2ActionPerformed() {
    int[] rows = orderTable.getSelectedRows();
    if (rows.length != 1) return;//можно отменить только один
    int modelRow = orderTable.convertRowIndexToModel(rows[0]);
    OrderObj order = orderTableModel.getOrder(modelRow);
    if (order.getStatus() != OrderObj.Status.CANCELLING_GATEWAY) {
      JOptionPane.showMessageDialog(this, "Невозможно отменить данный заказ", "Ошибка", JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (JOptionPane.showConfirmDialog(this, "Отменить заказ ID " + order.getId(), "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      net.create("ANNUL_ORDER", new Request(order.getId()), this).start();
    }
  }

  private void ticketTableMousePopup(MouseEvent e) {
    int activeRow;
    JXTable table = (JXTable) e.getSource();
    activeRow = table.rowAtPoint(new Point(e.getX(), e.getY()));
    if (activeRow == -1) {
      table.clearSelection();
    } else {
      if (e.isPopupTrigger()) {
        if (table.convertRowIndexToModel(activeRow) == table.getModel().getRowCount() - 1) return;
        if (!table.isRowSelected(activeRow)) table.setRowSelectionInterval(activeRow, activeRow);
        ticketMenuItem1.setVisible(!e.isAltDown());
        ticketMenuItem2.setVisible(e.isAltDown());
        ticketPopupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private void ticketMenuItem1ActionPerformed() {
    int[] rows = ticketTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = ticketTable.convertRowIndexToModel(rows[i]);
    }
    List<Long> ticketIdList = ticketTableModel.getTicketIdList(modelRows);
    if (ticketIdList.isEmpty()) return;//не выделено ни одного билета (например неоплаченный заказ)
    StringBuilder confirm = new StringBuilder();
    confirm.append("Вернуть ").append(L10n.pluralVal(ticketIdList.size(), "билет", "билета", "билетов")).append("? Данное дейстие отменить нельзя.\n");
    for (Long ticketId : ticketIdList) {
      confirm.append("билет ID ").append(ticketId).append("\n");
    }
    if (JOptionPane.showConfirmDialog(this, confirm.toString(), "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      net.create("REFUND_TICKETS", new Request(ticketIdList), this, 60000).start();
    }
  }

  private void ticketMenuItem2ActionPerformed() {
    int[] rows = ticketTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = ticketTable.convertRowIndexToModel(rows[i]);
    }
    List<String[]> records = new ArrayList<>();
    for (int row : modelRows) {
      if (row == ticketTableModel.getRowCount() - 1) continue;
      TicketObj ticket = ticketTableModel.getTicket(row);
      if (TicketTableModel.isFakeTicket(ticket)) continue;
      records.add(ticket.createExportData());
    }
    if (records.isEmpty()) return;
    if (exportTicketsDialog == null) {
      exportTicketsDialog = new FileChooser();
      exportTicketsDialog.setDialogType(JFileChooser.SAVE_DIALOG);
      exportTicketsDialog.setDialogTitle("Экспорт билетов");
      exportTicketsDialog.setAcceptAllFileFilterUsed(false);
      exportTicketsDialog.addChoosableFileFilter(new FileNameExtensionFilter("Формат CSV", "csv"));
      exportTicketsDialog.setAddingExtension(".csv");
    }
    if (exportTicketsDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = exportTicketsDialog.getSelectedFile();
      try {
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file), "windows-1251"), CSVFormat.DEFAULT.withDelimiter(';'));
        printer.printRecords(records);
        printer.close();
      } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Произошла ошибка при сохранении файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void organizerComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      filterActionList();
      filterGatewayList();
    }
  }

  private void agentComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      RAgent agent = agentComboBox.getItemAt(agentComboBox.getSelectedIndex());
      if (Env.agentAcquiring != null) {
        if (agent.getId() != user.getAuthorityId()) {
          acquiringComboBox.setSelectedItem(Env.agentAcquiring);
          acquiringComboBox.setEnabled(false);
        } else {
          acquiringComboBox.setSelectedItem(defAcquiring);
          acquiringComboBox.setEnabled(true);
        }
      }
      List<RFrontend> filterList = new ArrayList<>();
      for (RFrontend frontend : frontendList) {
        if (frontend.pass(agent)) filterList.add(frontend);
      }
      frontendComboBox.removeAllItems();
      frontendComboBox.addItem(defFrontend);
      for (RFrontend frontend : filterList) {
        frontendComboBox.addItem(frontend);
      }
    }
  }

  private void cityComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      RCity city = cityComboBox.getItemAt(cityComboBox.getSelectedIndex());
      List<RVenue> filterList = new ArrayList<>();
      for (RVenue venue : venueList) {
        if (venue.pass(city)) filterList.add(venue);
      }
      venueComboBox.removeAllItems();
      venueComboBox.addItem(defVenue);//вызывается venueComboBoxItemStateChanged
      for (RVenue venue : filterList) {
        venueComboBox.addItem(venue);
      }
    }
  }

  private void venueComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      filterActionList();
    }
  }

  private void actionComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
      List<RActionEvent> filterList = new ArrayList<>();
      for (RActionEvent actionEvent : actionEventList) {
        if (actionEvent.pass(action)) filterList.add(actionEvent);
      }
      defActionEvent.setSellEnd(action.isSellEnd());
      actionEventComboBox.removeAllItems();
      actionEventComboBox.addItem(defActionEvent);//вызывается actionEventComboBoxItemStateChanged
      for (RActionEvent actionEvent : filterList) {
        actionEventComboBox.addItem(actionEvent);
      }
    }
  }

  private void actionEventComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      boolean full = (defAction != actionComboBox.getItemAt(actionComboBox.getSelectedIndex()));
      fullCheckBox.setEnabled(full);
      if (!full) fullCheckBox.setSelected(false);
    }
  }

  private void actualCheckBoxItemStateChanged() {
    filterActionList();
  }

  private void fullCheckBoxItemStateChanged(ItemEvent e) {
    boolean full = (e.getStateChange() == ItemEvent.SELECTED);
    periodTypeComboBox.setEnabled(!full);
    periodLabel.setEnabled(!full);
    fromLabel.setEnabled(!full);
    toLabel.setEnabled(!full);
    fromDatePicker.setEnabled(!full);
    toDatePicker.setEnabled(!full);
    allStatusesCheckBox.setEnabled(!full);
    cityComboBox.setEnabled(!full);
    venueComboBox.setEnabled(!full);
    gSystemComboBox.setEnabled(!full);
    gatewayComboBox.setEnabled(!full);
    if (full) {
      frontendComboBox.setSelectedItem(defFrontend);
      allStatusesCheckBox.setSelected(false);
    }
  }

  private void gSystemComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      filterGatewayList();
    }
  }

  private void periodTypeComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      PeriodType periodType = periodTypeComboBox.getItemAt(periodTypeComboBox.getSelectedIndex());
      if (periodType == PeriodType.SALES) {
        allStatusesCheckBox.setEnabled(true);
      } else if (periodType == PeriodType.SHOWS) {
        allStatusesCheckBox.setSelected(false);
        allStatusesCheckBox.setEnabled(false);
      }
    }
  }

  private void lightRequestCheckBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      dividerLocation = splitPane.getDividerLocation() / (double) (splitPane.getHeight() - splitPane.getDividerSize());
      if (dividerLocation < 0.0d) dividerLocation = 0.0d;
      if (dividerLocation > 1.0d) dividerLocation = 1.0d;
      splitPane.setDividerLocation(1.0d);
    } else {
      splitPane.setDividerLocation(dividerLocation);
    }
  }

  private void filterActionList() {
    ROrganizer organizer = organizerComboBox.getItemAt(organizerComboBox.getSelectedIndex());
    RVenue venue = venueComboBox.getItemAt(venueComboBox.getSelectedIndex());
    RCity city = cityComboBox.getItemAt(cityComboBox.getSelectedIndex());
    List<RAction> filterList = new ArrayList<>();
    for (RAction action : actionList) {
      if (action.pass(organizer) && action.pass(venue) && action.pass(city)) filterList.add(action);
    }
    boolean actual = actualCheckBox.isSelected();
    actionComboBox.removeAllItems();
    actionComboBox.addItem(defAction);//вызывается actionComboBoxItemStateChanged
    for (RAction action : filterList) {
      if (!actual || action.isActual()) actionComboBox.addItem(action);
    }
  }

  private void filterGatewayList() {
    ROrganizer organizer = organizerComboBox.getItemAt(organizerComboBox.getSelectedIndex());
    GSystemObj system = gSystemComboBox.getItemAt(gSystemComboBox.getSelectedIndex());
    List<GatewayObj> filterList = new ArrayList<>();
    for (GatewayObj gateway : gatewayList) {
      if (organizer == defOrganizer || gateway.getOrganizerId() == null || gateway.getOrganizerId().equals(organizer.getId())) {
        if (system == defSystem || gateway.getSystemId() == system.getId()) filterList.add(gateway);
      }
    }
    gatewayComboBox.removeAllItems();
    gatewayComboBox.addItem(defGateway);
    if (system != null && system.getId() < 0) gatewayComboBox.addItem(anyGateway);
    for (GatewayObj gateway : filterList) {
      gatewayComboBox.addItem(gateway);
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel mainPanel = new JPanel();
    JPanel panel1 = new JPanel();
    acquiringComboBox = new SuggestionComboBox<>();
    organizerComboBox = new SuggestionComboBox<>();
    agentComboBox = new SuggestionComboBox<>();
    frontendComboBox = new SuggestionComboBox<>();
    JPanel panel2 = new JPanel();
    cityComboBox = new SuggestionComboBox<>();
    venueComboBox = new SuggestionComboBox<>();
    actualCheckBox = new JCheckBox();
    actionComboBox = new SuggestionComboBox<>();
    actionEventComboBox = new SuggestionComboBox<>();
    fullCheckBox = new JCheckBox();
    JPanel panel3 = new JPanel();
    gSystemComboBox = new SuggestionComboBox<>();
    gatewayComboBox = new SuggestionComboBox<>();
    periodLabel = new JLabel();
    periodTypeComboBox = new JComboBox<>();
    fromLabel = new JLabel();
    fromDatePicker = new JXDatePicker(new Date());
    toLabel = new JLabel();
    toDatePicker = new JXDatePicker(new Date());
    allStatusesCheckBox = new JCheckBox();
    lightRequestCheckBox = new JCheckBox();
    getButton = new JButton();
    splitPane = new JSplitPane();
    JScrollPane scrollPane1 = new JScrollPane();
    orderTable = new JXSummaryTable();
    JScrollPane scrollPane2 = new JScrollPane();
    ticketTable = new JXSummaryTable();
    statusBarPanel = new StatusBarPanel();
    JMenuBar menuBar = new JMenuBar();
    reportsMenu = new JMenu();
    JMenu distributionMenu = new JMenu();
    JMenuItem reportMenuItem = new JMenuItem();
    quotaMenu = new JMenu();
    JMenuItem invoiceMenuItem = new JMenuItem();
    JMenuItem quotaReportMenuItem = new JMenuItem();
    JMenu mskdMenu = new JMenu();
    JMenuItem passMenuItem = new JMenuItem();
    JMenuItem mecMenuItem = new JMenuItem();
    cashierWorkShiftMenu = new JMenu();
    JMenuItem cashierWorkShiftMenuItem = new JMenuItem();
    statsMenu = new JMenu();
    JMenuItem queryStatsItem = new JMenuItem();
    orderPopupMenu = new JPopupMenu();
    orderMenuItem1 = new JMenuItem();
    orderMenuItem2 = new JMenuItem();
    ticketPopupMenu = new JPopupMenu();
    ticketMenuItem1 = new JMenuItem();
    ticketMenuItem2 = new JMenuItem();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("\u041e\u0442\u0447\u0435\u0442\u044b \u043f\u043b\u0430\u0442\u0444\u043e\u0440\u043c\u044b BIL24 \u0432\u0435\u0440\u0441\u0438\u044f ");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== mainPanel ========
    {
      mainPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

      //======== panel1 ========
      {
        panel1.setBorder(new EmptyBorder(5, 5, 0, 5));
        panel1.setLayout(new GridBagLayout());
        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- acquiringComboBox ----
        acquiringComboBox.setMaximumRowCount(18);
        acquiringComboBox.setExcludeFirstItem(true);
        panel1.add(acquiringComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- organizerComboBox ----
        organizerComboBox.setMaximumRowCount(18);
        organizerComboBox.setExcludeFirstItem(true);
        organizerComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            organizerComboBoxItemStateChanged(e);
          }
        });
        panel1.add(organizerComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- agentComboBox ----
        agentComboBox.setExcludeFirstItem(true);
        agentComboBox.setMaximumRowCount(18);
        agentComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            agentComboBoxItemStateChanged(e);
          }
        });
        panel1.add(agentComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- frontendComboBox ----
        frontendComboBox.setExcludeFirstItem(true);
        frontendComboBox.setMaximumRowCount(18);
        panel1.add(frontendComboBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== panel2 ========
      {
        panel2.setBorder(new EmptyBorder(0, 5, 0, 5));
        panel2.setLayout(new GridBagLayout());
        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- cityComboBox ----
        cityComboBox.setMaximumRowCount(18);
        cityComboBox.setExcludeFirstItem(true);
        cityComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            cityComboBoxItemStateChanged(e);
          }
        });
        panel2.add(cityComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- venueComboBox ----
        venueComboBox.setMaximumRowCount(18);
        venueComboBox.setExcludeFirstItem(true);
        venueComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            venueComboBoxItemStateChanged(e);
          }
        });
        panel2.add(venueComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- actualCheckBox ----
        actualCheckBox.setText("\u0430\u043a\u0442\u0443\u0430\u043b\u044c\u043d\u044b\u0435:");
        actualCheckBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            actualCheckBoxItemStateChanged();
          }
        });
        panel2.add(actualCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- actionComboBox ----
        actionComboBox.setMaximumRowCount(18);
        actionComboBox.setExcludeFirstItem(true);
        actionComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            actionComboBoxItemStateChanged(e);
          }
        });
        panel2.add(actionComboBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- actionEventComboBox ----
        actionEventComboBox.setMaximumRowCount(18);
        actionEventComboBox.setExcludeFirstItem(true);
        actionEventComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            actionEventComboBoxItemStateChanged(e);
          }
        });
        actionEventComboBox.setPrototypeDisplayValue(new RActionEvent(0, 0, "88.88.8888 88:88"));
        panel2.add(actionEventComboBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- fullCheckBox ----
        fullCheckBox.setText("\u0432\u0441\u0435 \u043f\u0440\u043e\u0434\u0430\u0436\u0438");
        fullCheckBox.setEnabled(false);
        fullCheckBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            fullCheckBoxItemStateChanged(e);
          }
        });
        panel2.add(fullCheckBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(panel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== panel3 ========
      {
        panel3.setBorder(new EmptyBorder(0, 5, 0, 5));
        panel3.setLayout(new GridBagLayout());
        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- gSystemComboBox ----
        gSystemComboBox.setMaximumRowCount(18);
        gSystemComboBox.setExcludeFirstItem(true);
        gSystemComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            gSystemComboBoxItemStateChanged(e);
          }
        });
        panel3.add(gSystemComboBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- gatewayComboBox ----
        gatewayComboBox.setExcludeFirstItem(true);
        gatewayComboBox.setMaximumRowCount(18);
        panel3.add(gatewayComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- periodLabel ----
        periodLabel.setText("\u041f\u0435\u0440\u0438\u043e\u0434");
        panel3.add(periodLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- periodTypeComboBox ----
        periodTypeComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            periodTypeComboBoxItemStateChanged(e);
          }
        });
        periodTypeComboBox.addItem(PeriodType.SALES);
        periodTypeComboBox.addItem(PeriodType.SHOWS);
        panel3.add(periodTypeComboBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- fromLabel ----
        fromLabel.setText("\u0441");
        panel3.add(fromLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- fromDatePicker ----
        fromDatePicker.setFormats("EEE dd.MM.yyyy");
        panel3.add(fromDatePicker, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- toLabel ----
        toLabel.setText("\u043f\u043e");
        panel3.add(toLabel, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- toDatePicker ----
        toDatePicker.setFormats("EEE dd.MM.yyyy");
        panel3.add(toDatePicker, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- allStatusesCheckBox ----
        allStatusesCheckBox.setText("\u0432\u0441\u0435 \u0441\u0442\u0430\u0442\u0443\u0441\u044b");
        panel3.add(allStatusesCheckBox, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- lightRequestCheckBox ----
        lightRequestCheckBox.setText("\u0442\u043e\u043b\u044c\u043a\u043e \u0437\u0430\u043a\u0430\u0437\u044b");
        lightRequestCheckBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            lightRequestCheckBoxItemStateChanged(e);
          }
        });
        panel3.add(lightRequestCheckBox, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- getButton ----
        getButton.setText("\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c");
        getButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            getButtonActionPerformed();
          }
        });
        panel3.add(getButton, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(panel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== splitPane ========
      {
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        splitPane.setOneTouchExpandable(true);

        //======== scrollPane1 ========
        {

          //---- orderTable ----
          orderTable.setColumnControlVisible(true);
          orderTable.setHorizontalScrollEnabled(true);
          orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              orderTableMousePopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
              orderTableMousePopup(e);
            }
          });
          scrollPane1.setViewportView(orderTable);
        }
        splitPane.setTopComponent(scrollPane1);

        //======== scrollPane2 ========
        {

          //---- ticketTable ----
          ticketTable.setColumnControlVisible(true);
          ticketTable.setHorizontalScrollEnabled(true);
          ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              ticketTableMousePopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
              ticketTableMousePopup(e);
            }
          });
          scrollPane2.setViewportView(ticketTable);
        }
        splitPane.setBottomComponent(scrollPane2);
      }
      mainPanel.add(splitPane, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(mainPanel, BorderLayout.CENTER);
    contentPane.add(statusBarPanel, BorderLayout.SOUTH);

    //======== menuBar ========
    {

      //======== reportsMenu ========
      {
        reportsMenu.setText("\u041e\u0442\u0447\u0435\u0442\u044b");
        reportsMenu.setEnabled(false);
      }
      menuBar.add(reportsMenu);

      //======== distributionMenu ========
      {
        distributionMenu.setText("\u0420\u0430\u0441\u0441\u044b\u043b\u043a\u0430");

        //---- reportMenuItem ----
        reportMenuItem.setText("\u041e\u0442\u0447\u0435\u0442\u044b");
        reportMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            reportMenuItemActionPerformed();
          }
        });
        distributionMenu.add(reportMenuItem);
      }
      menuBar.add(distributionMenu);

      //======== quotaMenu ========
      {
        quotaMenu.setText("\u041a\u0432\u043e\u0442\u044b");

        //---- invoiceMenuItem ----
        invoiceMenuItem.setText("\u041d\u0430\u043a\u043b\u0430\u0434\u043d\u044b\u0435");
        invoiceMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            invoiceMenuItemActionPerformed();
          }
        });
        quotaMenu.add(invoiceMenuItem);

        //---- quotaReportMenuItem ----
        quotaReportMenuItem.setText("\u0424\u043e\u0440\u043c\u0430 \u21169. \u041e\u0431\u0449\u0438\u0439 \u043e\u0442\u0447\u0435\u0442 \u043f\u043e \u043f\u0440\u043e\u0434\u0430\u0436\u0430\u043c");
        quotaReportMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            quotaReportMenuItemActionPerformed();
          }
        });
        quotaMenu.add(quotaReportMenuItem);
      }
      menuBar.add(quotaMenu);

      //======== mskdMenu ========
      {
        mskdMenu.setText("\u041c\u0421\u041a\u0414");

        //---- passMenuItem ----
        passMenuItem.setText("\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u0431\u0438\u043b\u0435\u0442\u043e\u0432");
        passMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            passMenuItemActionPerformed();
          }
        });
        mskdMenu.add(passMenuItem);

        //---- mecMenuItem ----
        mecMenuItem.setText("\u0412\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u0438 \u043a\u0430\u0440\u0442");
        mecMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            mecMenuItemActionPerformed();
          }
        });
        mskdMenu.add(mecMenuItem);
      }
      menuBar.add(mskdMenu);

      //======== cashierWorkShiftMenu ========
      {
        cashierWorkShiftMenu.setText("\u0421\u043c\u0435\u043d\u044b");

        //---- cashierWorkShiftMenuItem ----
        cashierWorkShiftMenuItem.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u0441\u043c\u0435\u043d");
        cashierWorkShiftMenuItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cashierWorkShiftMenuItemActionPerformed();
          }
        });
        cashierWorkShiftMenu.add(cashierWorkShiftMenuItem);
      }
      menuBar.add(cashierWorkShiftMenu);

      //======== statsMenu ========
      {
        statsMenu.setText("\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430");

        //---- queryStatsItem ----
        queryStatsItem.setText("\u0417\u0430\u043f\u0440\u043e\u0441\u044b \u043f\u043e \u043f\u0440\u043e\u0442\u043e\u043a\u043e\u043b\u0443");
        queryStatsItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            queryStatsItemActionPerformed();
          }
        });
        statsMenu.add(queryStatsItem);
      }
      menuBar.add(statsMenu);
    }
    contentPane.add(menuBar, BorderLayout.NORTH);

    //======== orderPopupMenu ========
    {

      //---- orderMenuItem1 ----
      orderMenuItem1.setText("\u041e\u0442\u043f\u0440\u0430\u0432\u043a\u0430 \u0437\u0430\u043a\u0430\u0437\u0430");
      orderMenuItem1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          orderMenuItem1ActionPerformed();
        }
      });
      orderPopupMenu.add(orderMenuItem1);

      //---- orderMenuItem2 ----
      orderMenuItem2.setText("\u0420\u0443\u0447\u043d\u0430\u044f \u043e\u0442\u043c\u0435\u043d\u0430 \u0437\u0430\u043a\u0430\u0437\u0430");
      orderMenuItem2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          orderMenuItem2ActionPerformed();
        }
      });
      orderPopupMenu.add(orderMenuItem2);
    }

    //======== ticketPopupMenu ========
    {

      //---- ticketMenuItem1 ----
      ticketMenuItem1.setText("\u0412\u043e\u0437\u0432\u0440\u0430\u0442 \u0431\u0438\u043b\u0435\u0442\u0430");
      ticketMenuItem1.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ticketMenuItem1ActionPerformed();
        }
      });
      ticketPopupMenu.add(ticketMenuItem1);

      //---- ticketMenuItem2 ----
      ticketMenuItem2.setText("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u0431\u0438\u043b\u0435\u0442\u0430");
      ticketMenuItem2.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ticketMenuItem2ActionPerformed();
        }
      });
      ticketPopupMenu.add(ticketMenuItem2);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void showDatesColumns(boolean b) {
    orderTable.getColumnExt(orderTableModel.getColumnName(2)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(3)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(4)).setVisible(b);
  }

  private void showFilteredColumns(boolean b) {
    orderTable.getColumnExt(orderTableModel.getColumnName(10)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(11)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(12)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(13)).setVisible(b);
    orderTable.getColumnExt(orderTableModel.getColumnName(14)).setVisible(b);
  }

  private void showDiscountReasonColumns(boolean b) {
    ticketTable.getColumnExt(ticketTableModel.getColumnName(9)).setVisible(b);
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) {
      if (event.getCommand().equals("GET_INIT_REPORTER")) waitingDialog.setVisible(true);
      else {
        getButton.setEnabled(false);
        reportsMenu.setEnabled(false);
      }
    }
    if (state == Network.State.FINISHED) {
      if (event.getCommand().equals("GET_INIT_REPORTER")) waitingDialog.setVisible(false);
      else {
        getButton.setEnabled(true);
        reportsMenu.setEnabled(true);
      }
    }
  }

  @SuppressWarnings({"unchecked", "IfCanBeSwitch"})
  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (result.getCommand().equals("GET_INIT_REPORTER")) {
      Object[] data = (Object[]) result.getResponse().getData();
      loadData(data);
    }
    if (result.getCommand().startsWith("GET_ORDER_LIST")) {
      List<OrderObj> orderList = (List<OrderObj>) result.getResponse().getData();
      orderTableModel.setData(orderList);
      BuildManager.setOrderListFilter(orderList);

      boolean lightRequest = (Boolean) ((Object[]) result.getRequest().getData())[0];
      reportsMenu.setEnabled(!lightRequest);
    }
    if (result.getCommand().equals("GET_ORDER_LIST_1")) {
      Object[] obj = ((Object[]) result.getRequest().getData());
      if (((Boolean) obj[3])) showDatesColumns(true);
      else showDatesColumns(false);
      if (user.getUserType() == UserType.ORGANIZER) showFilteredColumns(true);
      else if (obj[5] != null || obj[8] != null || obj[9] != null || obj[10] != null || obj[11] != null || obj[12] != null || obj[13] != null) {
        showFilteredColumns(true);
      } else showFilteredColumns(false);
      orderTable.packAll();
    } else if (result.getCommand().equals("GET_ORDER_LIST_2") || result.getCommand().equals("GET_ORDER_LIST_3")) {
      showFilteredColumns(true);
      orderTable.packAll();
    } else if (result.getCommand().equals("RESEND_ORDER")) {
      boolean res = (Boolean) result.getResponse().getData();
      if (res) {
        Object[] req = (Object[]) result.getRequest().getData();
        long orderId = (Long) req[0];
        String email = (String) req[1];
        orderTableModel.setOrderEmail(orderId, email);
        JOptionPane.showMessageDialog(this, "Заказ будет отправлен в ближайшее время", "Ответ", JOptionPane.INFORMATION_MESSAGE);
      } else JOptionPane.showMessageDialog(this, "E-mail указан неверно", "Ошибка", JOptionPane.ERROR_MESSAGE);
    } else if (result.getCommand().equals("ANNUL_ORDER")) {
      OrderObj.Status status = (OrderObj.Status) result.getResponse().getData();
      long orderId = (Long) result.getRequest().getData();
      orderTableModel.setOrderStatus(orderId, status);
      if (status == OrderObj.Status.CANCELLED) {
        JOptionPane.showMessageDialog(this, "Заказ отменен", "Ответ", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Статус заказа изменился", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    } else if (result.getCommand().equals("REFUND_TICKETS")) {
      Object[] obj = (Object[]) result.getResponse().getData();
      boolean res = (Boolean) obj[0];
      Map<Long, TicketObj.HolderStatus> idHolderStatusMap = (Map<Long, TicketObj.HolderStatus>) obj[1];
      orderTableModel.setTicketHolderStatus(idHolderStatusMap);
      ticketTableModel.refresh();
      if (res) JOptionPane.showMessageDialog(this, "Билеты возвращены", "Ответ", JOptionPane.INFORMATION_MESSAGE);
      else JOptionPane.showMessageDialog(this, "Не удалось вернуть билеты", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  public enum PeriodType {
    SALES("продаж"), SHOWS("сеансов");

    @NotNull
    private final String name;

    PeriodType(@NotNull String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
