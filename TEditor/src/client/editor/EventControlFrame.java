/*
 * Created by JFormDesigner on Thu Nov 05 14:47:06 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.*;
import client.component.summary.JXSummaryTable;
import client.editor.component.*;
import client.editor.component.listener.*;
import client.editor.model.*;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.utils.*;
import common.svg.*;
import eventim.spl.managers.EventimManager;
import eventim.spl.models.*;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.GatewayObj;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class EventControlFrame extends JFrame implements NetPoolStateListener, NetListener<Request, Response>, SVGDocEventSeatListener {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JButton updateButton;
  private JButton catButton;
  private JButton inaccessibleButton;
  private JButton availableButton;
  private JButton addQuotaButton;
  private JButton returnQuotaButton;
  private JButton infoEBSButton;
  private JButton syncEBSButton;
  private JCheckBox ebsViewCheckBox;
  private JButton orderCatButton;
  private JButton setSplButton;
  private JXBusyLabel busyLabel;
  private JLabel splLabel;
  private JButton splitButton;
  private JSplitPane splitPane;
  private JPanel tablePanel;
  private JXSummaryTable eventSeatTable;
  private JLabel barLabel;
  private PlanSvgPanel planPanel;
  private JPopupMenu addQuotaPopupMenu;
  private JPopupMenu returnQuotaPopupMenu;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  private static final L10n.PluralForm seatsForm = new L10n.PluralForm("место", "места", "мест");
  private static final String splitText1 = "Схема →";
  private static final String splitText2 = "Схема ←";
  private static final String addQuotaButtonText1 = "Добавить квоту";
  private static final String addQuotaButtonText2 = "Добавить места";
  private static final String returnQuotaButtonText1 = "Вернуть квоту";
  private static final String returnQuotaButtonText2 = "Отменить квоту";
  private final ActionEventObj actionEvent;
  private final OperationComboBox<ActionEventObj> actionEventComboBox;
  @Nullable
  private final ActionEventSyncListener syncListener;
  private final EventSeatTableModel eventSeatTableModel = new EventSeatTableModel();
  private final NetPool net;
  private final Set<Long> placementSelection = new HashSet<>();
  private final Set<Long> allSelection = new HashSet<>();
  private ActionEventData actionEventData = null;
  private boolean ignoreSelectionChange = false;
  private boolean split = false;//окно разделено на 2 отдельных если true
  private SVGFrame splitFrame = null;
  @Nullable
  private QuotaInManualObj quotaInManual = null;//если не null, значит режим добавления квоты из накладной

  public EventControlFrame(Window owner, ActionEventObj actionEvent, OperationComboBox<ActionEventObj> actionEventComboBox) {
    this(owner, actionEvent, actionEventComboBox, null);
  }

  public EventControlFrame(Window owner, ActionEventObj actionEvent, OperationComboBox<ActionEventObj> actionEventComboBox, @Nullable ActionEventSyncListener syncListener) {
    this.actionEvent = actionEvent;
    this.actionEventComboBox = actionEventComboBox;
    this.syncListener = syncListener;
    initComponents();
    GatewayEventObj gatewayEvent = actionEvent.getGatewayEvent();
    GatewayObj gateway = gatewayEvent.getGateway();
    addQuotaButton.setVisible(actionEvent.isQuota());
    returnQuotaButton.setVisible(actionEvent.isQuota());
    infoEBSButton.setVisible(gateway.getId() != 0);
    syncEBSButton.setVisible(gateway.getId() != 0);
    ebsViewCheckBox.setVisible(gateway.getId() != 0);
    String title = actionEvent.getActionName() + " " + actionEvent.toString();
    if (gateway.getId() != 0) {
      title += " [" + gateway.getName() + ": " + gatewayEvent + "]";
    }
    setTitle(title);
    planPanel.setPlanName(actionEvent.getPlanName());
    splitButton.setText(splitText1);

    if (addQuotaButton.isVisible()) {//всё это только чтобы не изменялась ширина панели при смене текста кнопки
      int maxWidth = addQuotaButton.getPreferredSize().width;
      addQuotaButton.setText(addQuotaButtonText2);
      maxWidth = Math.max(maxWidth, addQuotaButton.getPreferredSize().width);
      addQuotaButton.setText(addQuotaButtonText1);
      addQuotaButton.setPreferredSize(new Dimension(maxWidth, addQuotaButton.getPreferredSize().height));
    }

    eventSeatTable.setModel(eventSeatTableModel);
    eventSeatTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    eventSeatTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) eventSeatTableSelectionChanged();
      }
    });
    eventSeatTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.OCCUPIED, false), new Color(183, 255, 198), Color.BLACK, new Color(130, 227, 170), Color.BLACK));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.OCCUPIED, true), new Color(183, 255, 198), Color.LIGHT_GRAY, new Color(130, 227, 170), Color.DARK_GRAY));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.INACCESSIBLE), new Color(255, 209, 155), Color.BLACK, new Color(210, 200, 179), Color.BLACK));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.REFUND), new Color(255, 209, 155), Color.BLACK, new Color(210, 200, 179), Color.BLACK));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.PRE_RESERVED), new Color(255, 255, 105), Color.BLACK, new Color(195, 195, 92), Color.BLACK));
    eventSeatTable.addHighlighter(new ColorHighlighter(new EventSeatStatePredicate(eventSeatTableModel, EventSeatObj.State.RESERVED), new Color(255, 255, 105), Color.BLACK, new Color(195, 195, 92), Color.BLACK));

    if (!actionEvent.isPlacementPlan()) {
      this.remove(splitPane);
      this.add(tablePanel, BorderLayout.CENTER);
      orderCatButton.setVisible(false);
      splitButton.setVisible(false);
    }
    pack();
    setLocationRelativeTo(owner);
    eventSeatTable.requestFocus();//В java 8 updateButton.setEnabled(false); приводит к перемещению окна назад, из-за того, что updateButton имеет фокус

    net = Env.net.clone(true);
    net.addPoolStateListener(this, Network.EventMode.EDT_INVOKE_LATER);
  }

  public void startWork() {
    this.setVisible(true);
    updateButtonActionPerformed();
  }

  private void thisWindowActivated() {
    if (splitFrame != null && splitFrame.isVisible()) {
      if ((splitFrame.getExtendedState() & ICONIFIED) != 0) splitFrame.setExtendedState(splitFrame.getExtendedState() & ~ICONIFIED);
      splitFrame.toFront();
      this.toFront();
    }
  }

  private void thisWindowClosed() {
    if (splitFrame != null) splitFrame.dispose();
  }

  private void eventSeatTableSelectionChanged() {
    if (ignoreSelectionChange) return;
    int[] rows = eventSeatTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = eventSeatTable.convertRowIndexToModel(rows[i]);
    }
    eventSeatTableModel.setEventSeatIds(modelRows, placementSelection, allSelection);
    if (actionEvent.isPlacementPlan()) {
      planPanel.updateEventSelection(placementSelection);
    }
    barLabel.setText("Выделено мест: " + allSelection.size());
    Boolean commonPlacement = eventSeatTableModel.getCommonPlacement(modelRows);
    EventSeatObj.State commonState = eventSeatTableModel.getCommonState(modelRows);
    if (commonState == null) commonState = EventSeatObj.State.OCCUPIED;
    switch (commonState) {
      case AVAILABLE:
        catButton.setEnabled(!actionEvent.isQuota() && commonPlacement != null);
        inaccessibleButton.setEnabled(true);
        availableButton.setEnabled(false);
        break;
      case INACCESSIBLE:
        catButton.setEnabled(!actionEvent.isQuota() && commonPlacement != null);
        inaccessibleButton.setEnabled(false);
        availableButton.setEnabled(true);
        break;
      case REFUND:
        catButton.setEnabled(false);
        inaccessibleButton.setEnabled(!actionEvent.isQuota());
        availableButton.setEnabled(!actionEvent.isQuota());
        break;
      case PRE_RESERVED:
      case RESERVED:
      case OCCUPIED:
        catButton.setEnabled(false);
        inaccessibleButton.setEnabled(false);
        availableButton.setEnabled(false);
        break;
    }
  }

  private void updateButtonActionPerformed() {
    net.create("GET_EVENT_SEAT_LIST", new Request(actionEvent.getId()), this, Env.GATEWAY_TIMEOUT).start();
  }

  private void catButtonActionPerformed() {
    int[] rows = eventSeatTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = eventSeatTable.convertRowIndexToModel(rows[i]);
    }
    List<CategoryPriceObj> categoryPriceList;
    Boolean placement = eventSeatTableModel.getCommonPlacement(modelRows);
    if (placement == null) return;
    if (placement) categoryPriceList = eventSeatTableModel.getCategoryPricePlacementList();
    else categoryPriceList = eventSeatTableModel.getCategoryPriceNonPlacementList();
    List<EventSeatObj> eventSeatList = eventSeatTableModel.getEventSeatList(modelRows);
    ChCategoryDialog dialog = new ChCategoryDialog(this, tablePanel, categoryPriceList, eventSeatList);
    dialog.setVisible(true);
    CategoryPriceObj categoryPrice = dialog.getResult();
    if (categoryPrice == null) return;
    Object[] obj = new Object[4];
    obj[0] = actionEvent.getId();
    obj[1] = categoryPrice.getId();
    long[] seatIds = new long[eventSeatList.size()];
    for (int i = 0; i < eventSeatList.size(); i++) {
      EventSeatObj eventSeat = eventSeatList.get(i);
      seatIds[i] = eventSeat.getId();
    }
    Arrays.sort(seatIds);
    obj[3] = seatIds;
    net.create("SET_EVENT_SEAT_CATEGORY", new Request(obj), this, true, Env.GATEWAY_TIMEOUT).start();
  }

  private void inaccessibleButtonActionPerformed() {
    changeState(EventSeatObj.State.INACCESSIBLE);
  }

  private void availableButtonActionPerformed() {
    changeState(EventSeatObj.State.AVAILABLE);
  }

  private void addQuotaButtonActionPerformed() {
    if (quotaInManual == null) addQuotaPopupMenu.show(addQuotaButton, addQuotaButton.getWidth(), 0);
    else {
      if (!quotaInManual.isInputComplete()) {
        if (allSelection.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Не выделено ни одного места", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (quotaInManual.checkIntersection(allSelection)) {
          JOptionPane.showMessageDialog(this, "Выделение содержит места уже добавленные в квоту", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      AddQuotaManualDialog dialog = new AddQuotaManualDialog(this, eventSeatTableModel, allSelection, quotaInManual);
      dialog.setVisible(true);
      QuotaInManualObj quota = dialog.getQuota();
      if (quota == null) return;
      if (quota.isInputComplete()) {
        Env.planQuotaDataMap.put(actionEvent.getPlanId(), new ManualQuotaData(quota, eventSeatTableModel));
        net.create("ADD_QUOTA_MANUAL", new Request(quota), this, true).start();
        setQuotaInManual(null);
      }
    }
  }

  private void addManualQuotaMenuItemActionPerformed() {
    AddQuotaManualDialog dialog = null;
    ManualQuotaData quotaData = Env.planQuotaDataMap.get(actionEvent.getPlanId());
    if (actionEventData != null && quotaData != null) {
      if (JOptionPane.showConfirmDialog(this, "Найдена введенная ранее квота, использовать её?", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
        QuotaInManualObj quota = quotaData.createQuota(actionEvent.getId(), actionEventData.getEventSeatList(), actionEventData.getCategoryPriceList());
        if (quota == null) JOptionPane.showMessageDialog(this, "Не удалось применить введенную ранее квоту", "Ошибка", JOptionPane.ERROR_MESSAGE);
        else dialog = new AddQuotaManualDialog(this, eventSeatTableModel, allSelection, quota);
      }
    }
    if (dialog == null && allSelection.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Не выделено ни одного места", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (dialog == null) dialog = new AddQuotaManualDialog(this, eventSeatTableModel, actionEvent.getId(), allSelection);
    dialog.setVisible(true);
    QuotaInManualObj quota = dialog.getQuota();
    if (quota == null) return;
    if (quota.isInputComplete()) {
      Env.planQuotaDataMap.put(actionEvent.getPlanId(), new ManualQuotaData(quota, eventSeatTableModel));
      net.create("ADD_QUOTA_MANUAL", new Request(quota), this, true).start();
    } else {
      setQuotaInManual(quota);
    }
  }

  private void addFileQuotaMenuItemActionPerformed() {
    AddQuotaFileDialog dialog = new AddQuotaFileDialog(this, actionEvent.getId());
    dialog.setVisible(true);
    QuotaInFileObj quota = dialog.getQuota();
    if (quota == null) return;
    net.create("ADD_QUOTA_FILE", new Request(quota), this, true).start();
  }

  private void returnQuotaButtonActionPerformed() {
    if (quotaInManual == null) returnQuotaPopupMenu.show(returnQuotaButton, returnQuotaButton.getWidth(), 0);
    else {
      String message = "Отменить ввод квоты?";
      if (quotaInManual.getTotalQty() == null) {//итог квоты неизвестен
        String[] buttons = {"OK", "Ввод завершен", "Отмена"};
        int res = JOptionPane.showOptionDialog(this, message, "Подтверждение", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);
        if (res == 0) setQuotaInManual(null);
        if (res == 1) {
          quotaInManual.setInputComplete();
          setQuotaInManual(quotaInManual);
        }
      } else if (JOptionPane.showConfirmDialog(this, message, "Подтверждение", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
        setQuotaInManual(null);
      }
    }
  }

  private void returnSelectedQuotaMenuItemActionPerformed() {
    if (allSelection.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Не выделено ни одного места", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    ReturnQuotaDialog dialog = new ReturnQuotaDialog(this, actionEvent.getId(), allSelection);
    dialog.setVisible(true);
    QuotaOutObj quota = dialog.getQuota();
    if (quota == null) return;
    net.create("RETURN_QUOTA", new Request(quota), this, true).start();
  }

  private void returnAllQuotaMenuItemActionPerformed() {
    ReturnQuotaDialog dialog = new ReturnQuotaDialog(this, actionEvent.getId());
    dialog.setVisible(true);
    QuotaOutObj quota = dialog.getQuota();
    if (quota == null) return;
    net.create("RETURN_QUOTA", new Request(quota), this, true).start();
  }

  private void infoEBSButtonActionPerformed() {
    net.create("GET_GATEWAY_EVENT_INFO", new Request(actionEvent.getGatewayEvent()), this, Env.GATEWAY_TIMEOUT).start();
  }

  private void syncEBSButtonActionPerformed() {
    net.create("SYNC_EBS", new Request(actionEvent.getId()), this, 150000).start();
  }

  private void ebsViewCheckBoxItemStateChanged(ItemEvent e) {
    boolean ebsView = (e.getStateChange() == ItemEvent.SELECTED);
    boolean res = eventSeatTableModel.setEbsView(ebsView);
    if (res) eventSeatTable.packAll();
    if (actionEvent.isPlacementPlan()) planPanel.setEventEbsView(ebsView);
  }

  private void orderCatButtonActionPerformed() {
    List<CategoryPriceObj> categoryPriceList = eventSeatTableModel.getCategoryPricePlacementList();
    OrderCategoryDialog dialog = new OrderCategoryDialog(this, tablePanel, categoryPriceList);
    dialog.setVisible(true);
    List<Long> newOrder = dialog.getResult();
    if (newOrder == null) return;
    Object[] obj = new Object[2];
    obj[0] = actionEvent.getId();
    obj[1] = newOrder;
    net.create("SET_EVENT_CATEGORY_ORDER", new Request(obj), this, Env.GATEWAY_TIMEOUT).start();
  }

  private void setSplButtonActionPerformed() {
    if (actionEventData == null) return;
    FileChooser openSplDialog = FileChoosers.getOpenSplDialog();
    if (openSplDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openSplDialog.getSelectedFile();
      try {
        byte[] splData = Files.readAllBytes(file.toPath());
        EventimManager eventimManager = EventimManager.read(splData);
        eventimManager.setSplId(actionEvent.getId());
        List<EventimSeat> seatList = eventimManager.getSeatList();
        List<EventimNplCategory> categoryList = eventimManager.getNplCategoryList();
        List<CategoryPriceObj> categoryPriceList = new ArrayList<>();
        for (CategoryPriceObj categoryPrice : actionEvent.getPriceList()) {
          if (!categoryPrice.isPlacement()) categoryPriceList.add(categoryPrice);
        }
        if (!actionEvent.isPlacementPlan()) {//безместовая
          if (!seatList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема содержит места с размещением", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (categoryList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема не содержит мест без размещения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
        } else if (!actionEvent.isCombinedPlan()) {//только местовая
          if (seatList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема не содержит мест с размещением", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (!categoryList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема содержит места без размещения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
        } else {//комбинированная
          if (seatList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема не содержит мест с размещением", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (categoryList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема не содержит мест без размещения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
        if (categoryList.size() != categoryPriceList.size()) {
          JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: Схема содержит " +
              categoryList.size() + " категорий без размещения, необходимо " + categoryPriceList.size(), "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        StringBuilder report = new StringBuilder();
        if (!seatList.isEmpty()) {
          if (actionEventData.getSvgData() == null) return;
          SVGPlanEvent svgPlan = new SVGPlanEvent(actionEventData.getSvgData());
          Map<Long, String> svgStringMap = svgPlan.getSvgStringMap();
          Map<EventimSeat, String> splCompareStringMap = new HashMap<>(seatList.size());
          StringBuilder eventimStr = new StringBuilder("Список мест с размещением в spl:\n");
          for (EventimSeat eventimSeat : seatList) {
            String eventimString = eventimSeat.toString();
            eventimStr.append(eventimString).append("\n");
            String eventimCompareString = eventimString.replace(" ", "").toLowerCase();
            splCompareStringMap.put(eventimSeat, eventimCompareString);
          }
          List<String> notFoundSeatList = new ArrayList<>();
          for (Map.Entry<Long, String> svgStringEntry : svgStringMap.entrySet()) {
            String svgCompareString = svgStringEntry.getValue().replace(" ", "").toLowerCase();
            boolean found = false;
            for (Iterator<Map.Entry<EventimSeat, String>> iterator = splCompareStringMap.entrySet().iterator(); iterator.hasNext(); ) {
              Map.Entry<EventimSeat, String> splCompareStringEntry = iterator.next();
              String splCompareString = splCompareStringEntry.getValue();
              if (svgCompareString.equals(splCompareString)) {
                splCompareStringEntry.getKey().replaceSeatId(svgStringEntry.getKey());
                iterator.remove();//удаляем найденное место
                found = true;
                break;
              }
            }
            if (!found) notFoundSeatList.add(svgStringEntry.getValue());
          }
          for (Map.Entry<EventimSeat, String> entry : splCompareStringMap.entrySet()) {//всё что осталось, не найдено в svg
            entry.getKey().replaceSeatId(0);
          }
          eventimStr.append("\n").append("Всего на схеме spl ").append(seatsForm.val(seatList.size())).append(" с размещением\n");

          report.append("Всего на схеме spl ").append(seatsForm.val(seatList.size())).append("\n");
          report.append("Всего на схеме svg ").append(seatsForm.val(svgStringMap.size())).append("\n\n");
          if (!notFoundSeatList.isEmpty()) {
            report.append("Не найдено на схеме spl ").append(seatsForm.val(notFoundSeatList.size())).append(" из svg:\n");
            for (String seat : notFoundSeatList) {
              report.append(seat).append("\n");
            }
            report.append("\n");
            report.append("Найдено на схеме spl ").append(seatsForm.val(svgStringMap.size() - notFoundSeatList.size())).append(" из svg\n");
          } else report.append("Все места из svg найдены на схеме spl\n");
          ScrollOptionPane.showMessageDialog(this, eventimStr.toString(), "Список мест spl", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.RIGHT));
        }
        if (!categoryList.isEmpty()) {
          if (!seatList.isEmpty()) report.append("\n");
          report.append("Список категорий без размещения:\n");
          for (int i = 0; i < categoryList.size(); i++) {
            EventimNplCategory splCategory = categoryList.get(i);
            CategoryPriceObj categoryPrice = categoryPriceList.get(i);
            splCategory.replaceCategoryId(categoryPrice.getId());
            report.append(splCategory.getSector()).append("\n");
          }
        }
        report.append("\nЗагрузить spl схему в сеанс?\n");
        if (ScrollOptionPane.showConfirmDialog(this, report.toString(), "Загрузить spl схему?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
          eventimManager.write();
          byte[] splZip = SVGTranscoder.toGzip(eventimManager.getData());
          net.create("SET_EVENT_SPL", new Request(new Object[]{actionEvent.getId(), splZip}), this, Env.GATEWAY_TIMEOUT).start();
        }
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      Env.pref.put("dir.spl", openSplDialog.getCurrentDirectory().getAbsolutePath());
    }
  }

  private void splitButtonActionPerformed() {
    if (!split) {
      this.remove(splitPane);
      this.add(tablePanel, BorderLayout.CENTER);
      int delta = splitPane.getWidth() - splitPane.getDividerLocation();
      this.setSize(this.getWidth() - delta, this.getHeight());
      this.revalidate();
      splitFrame = new SVGFrame("Сеанс: " + actionEvent.toString(), planPanel);
      splitFrame.setSize(delta, this.getHeight());
      splitFrame.setLocation(this.getX() + this.getWidth(), this.getY());
      splitFrame.setAutoRequestFocus(false);//важно, чтобы splitFrame.toFront() не вызывало мигания
      splitFrame.setVisible(true);
      splitButton.setText(splitText2);
      split = true;
    } else {
      splitFrame.dispose();
      splitFrame = null;
      this.remove(tablePanel);
      this.add(splitPane, BorderLayout.CENTER);
      splitPane.setLeftComponent(tablePanel);
      splitPane.setRightComponent(planPanel);
      int delta = Math.max(tablePanel.getWidth(), planPanel.getMinimumSize().width) + splitPane.getDividerSize();
      this.setSize(this.getWidth() + delta, this.getHeight());
      int dividerLocation = tablePanel.getWidth();
      this.revalidate();
      splitPane.setDividerLocation(dividerLocation);
      splitButton.setText(splitText1);
      split = false;
    }
    this.repaint();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel leftPanel = new JPanel();
    updateButton = new JButton();
    catButton = new JButton();
    inaccessibleButton = new JButton();
    availableButton = new JButton();
    addQuotaButton = new JButton();
    returnQuotaButton = new JButton();
    infoEBSButton = new JButton();
    syncEBSButton = new JButton();
    ebsViewCheckBox = new JCheckBox();
    orderCatButton = new JButton();
    setSplButton = new JButton();
    busyLabel = new JXBusyLabel();
    splLabel = new JLabel();
    splitButton = new JButton();
    splitPane = new JSplitPane();
    tablePanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    eventSeatTable = new JXSummaryTable();
    barLabel = new JLabel();
    planPanel = new PlanSvgPanel();
    addQuotaPopupMenu = new JPopupMenu();
    JMenuItem addManualQuotaMenuItem = new JMenuItem();
    JMenuItem addFileQuotaMenuItem = new JMenuItem();
    returnQuotaPopupMenu = new JPopupMenu();
    JMenuItem returnSelectedQuotaMenuItem = new JMenuItem();
    JMenuItem returnAllQuotaMenuItem = new JMenuItem();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        thisWindowActivated();
      }
      @Override
      public void windowClosed(WindowEvent e) {
        thisWindowClosed();
      }
    });
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== leftPanel ========
    {
      leftPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      leftPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)leftPanel.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)leftPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 0, 0, 0};
      ((GridBagLayout)leftPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
      ((GridBagLayout)leftPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

      //---- updateButton ----
      updateButton.setText("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c");
      updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateButtonActionPerformed();
        }
      });
      leftPanel.add(updateButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- catButton ----
      catButton.setText("\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044f");
      catButton.setEnabled(false);
      catButton.setToolTipText("\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e");
      catButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          catButtonActionPerformed();
        }
      });
      leftPanel.add(catButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 0, 10, 0), 0, 0));

      //---- inaccessibleButton ----
      inaccessibleButton.setText("\u041d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u043d\u043e");
      inaccessibleButton.setToolTipText("\u0421\u0434\u0435\u043b\u0430\u0442\u044c \u043c\u0435\u0441\u0442\u0430 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u043c\u0438 \u0434\u043b\u044f \u043f\u0440\u043e\u0434\u0430\u0436\u0438");
      inaccessibleButton.setEnabled(false);
      inaccessibleButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          inaccessibleButtonActionPerformed();
        }
      });
      leftPanel.add(inaccessibleButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- availableButton ----
      availableButton.setText("\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u043e");
      availableButton.setToolTipText("\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u043c\u0435\u0441\u0442\u0430 \u0432 \u043f\u0440\u043e\u0434\u0430\u0436\u0443");
      availableButton.setEnabled(false);
      availableButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          availableButtonActionPerformed();
        }
      });
      leftPanel.add(availableButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 10, 0), 0, 0));

      //---- addQuotaButton ----
      addQuotaButton.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043a\u0432\u043e\u0442\u0443");
      addQuotaButton.setMargin(new Insets(2, 8, 2, 8));
      addQuotaButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          addQuotaButtonActionPerformed();
        }
      });
      leftPanel.add(addQuotaButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- returnQuotaButton ----
      returnQuotaButton.setText("\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u043a\u0432\u043e\u0442\u0443");
      returnQuotaButton.setMargin(new Insets(2, 8, 2, 8));
      returnQuotaButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          returnQuotaButtonActionPerformed();
        }
      });
      leftPanel.add(returnQuotaButton, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 10, 0), 0, 0));

      //---- infoEBSButton ----
      infoEBSButton.setText("\u0418\u043d\u0444. \u043e\u0442 \u0412\u0411\u0421");
      infoEBSButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          infoEBSButtonActionPerformed();
        }
      });
      leftPanel.add(infoEBSButton, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- syncEBSButton ----
      syncEBSButton.setText("\u0421\u0438\u043d\u0445. \u0441 \u0412\u0411\u0421");
      syncEBSButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          syncEBSButtonActionPerformed();
        }
      });
      leftPanel.add(syncEBSButton, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- ebsViewCheckBox ----
      ebsViewCheckBox.setText("\u0412\u0438\u0434 \u043e\u0442 \u0412\u0411\u0421");
      ebsViewCheckBox.setSelected(true);
      ebsViewCheckBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          ebsViewCheckBoxItemStateChanged(e);
        }
      });
      leftPanel.add(ebsViewCheckBox, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 10, 0), 0, 0));

      //---- orderCatButton ----
      orderCatButton.setText("\u041f\u043e\u0440\u044f\u0434\u043e\u043a \u0446\u0435\u043d");
      orderCatButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          orderCatButtonActionPerformed();
        }
      });
      leftPanel.add(orderCatButton, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- setSplButton ----
      setSplButton.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c spl");
      setSplButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          setSplButtonActionPerformed();
        }
      });
      leftPanel.add(setSplButton, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- busyLabel ----
      busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
      busyLabel.setVisible(false);
      leftPanel.add(busyLabel, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- splLabel ----
      splLabel.setText("SPL:");
      splLabel.setFont(splLabel.getFont().deriveFont(splLabel.getFont().getStyle() & ~Font.BOLD, splLabel.getFont().getSize() - 1f));
      leftPanel.add(splLabel, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0,
        GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- splitButton ----
      splitButton.setText("\u0421\u0445\u0435\u043c\u0430");
      splitButton.setMargin(new Insets(2, 8, 2, 8));
      splitButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          splitButtonActionPerformed();
        }
      });
      leftPanel.add(splitButton, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(leftPanel, BorderLayout.WEST);

    //======== splitPane ========
    {
      splitPane.setResizeWeight(0.5);

      //======== tablePanel ========
      {
        tablePanel.setLayout(new BorderLayout());

        //======== scrollPane ========
        {

          //---- eventSeatTable ----
          eventSeatTable.setColumnControlVisible(true);
          scrollPane.setViewportView(eventSeatTable);
        }
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        //---- barLabel ----
        barLabel.setText(" ");
        barLabel.setFont(barLabel.getFont().deriveFont(barLabel.getFont().getStyle() & ~Font.BOLD, barLabel.getFont().getSize() - 1f));
        tablePanel.add(barLabel, BorderLayout.SOUTH);
      }
      splitPane.setLeftComponent(tablePanel);

      //---- planPanel ----
      planPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      splitPane.setRightComponent(planPanel);
    }
    contentPane.add(splitPane, BorderLayout.CENTER);

    //======== addQuotaPopupMenu ========
    {

      //---- addManualQuotaMenuItem ----
      addManualQuotaMenuItem.setText("\u0432\u0440\u0443\u0447\u043d\u0443\u044e");
      addManualQuotaMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          addManualQuotaMenuItemActionPerformed();
        }
      });
      addQuotaPopupMenu.add(addManualQuotaMenuItem);

      //---- addFileQuotaMenuItem ----
      addFileQuotaMenuItem.setText("\u0438\u0437 \u0444\u0430\u0439\u043b\u0430");
      addFileQuotaMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          addFileQuotaMenuItemActionPerformed();
        }
      });
      addQuotaPopupMenu.add(addFileQuotaMenuItem);
    }

    //======== returnQuotaPopupMenu ========
    {

      //---- returnSelectedQuotaMenuItem ----
      returnSelectedQuotaMenuItem.setText("\u0432\u044b\u0434\u0435\u043b\u0435\u043d\u043d\u044b\u0435 \u043c\u0435\u0441\u0442\u0430");
      returnSelectedQuotaMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          returnSelectedQuotaMenuItemActionPerformed();
        }
      });
      returnQuotaPopupMenu.add(returnSelectedQuotaMenuItem);

      //---- returnAllQuotaMenuItem ----
      returnAllQuotaMenuItem.setText("\u0432\u0441\u0435 \u043c\u0435\u0441\u0442\u0430");
      returnAllQuotaMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          returnAllQuotaMenuItemActionPerformed();
        }
      });
      returnQuotaPopupMenu.add(returnAllQuotaMenuItem);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void setQuotaInManual(@Nullable QuotaInManualObj quotaInManual) {
    this.quotaInManual = quotaInManual;
    if (quotaInManual == null) {
      addQuotaButton.setText(addQuotaButtonText1);
      returnQuotaButton.setText(returnQuotaButtonText1);
    } else {
      if (quotaInManual.isInputComplete()) addQuotaButton.setText(addQuotaButtonText1);
      else addQuotaButton.setText(addQuotaButtonText2);
      returnQuotaButton.setText(returnQuotaButtonText2);
    }
  }

  private void changeState(EventSeatObj.State newState) {
    int[] rows = eventSeatTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = eventSeatTable.convertRowIndexToModel(rows[i]);
    }
    EventSeatObj.State oldState = eventSeatTableModel.getCommonState(modelRows);
    if (oldState == null) {
      JOptionPane.showMessageDialog(tablePanel, "Выделены места с разным состоянием", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (oldState == EventSeatObj.State.REFUND && actionEvent.isQuota()) {
      JOptionPane.showMessageDialog(tablePanel, "Недопустимая операция", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    switch (newState) {
      case INACCESSIBLE:
        if (oldState != EventSeatObj.State.AVAILABLE && oldState != EventSeatObj.State.REFUND) {
          String text = "'" + EventSeatObj.State.AVAILABLE.getDesc() + "'";
          JOptionPane.showMessageDialog(tablePanel, "Выделенные места должны иметь состояние " + text, "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;
      case AVAILABLE:
        if (oldState != EventSeatObj.State.INACCESSIBLE && oldState != EventSeatObj.State.REFUND) {
          String text = "'" + EventSeatObj.State.INACCESSIBLE.getDesc() + "'";
          JOptionPane.showMessageDialog(tablePanel, "Выделенные места должны иметь состояние " + text, "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        break;
    }
    List<EventSeatObj> eventSeatList = eventSeatTableModel.getEventSeatList(modelRows);
    //если хотим добавить места в продажу, нужно проверить задан ли у них штрих-код и категория
    if (newState == EventSeatObj.State.AVAILABLE) {
      GatewayObj gatewayObj = actionEvent.getGatewayEvent().getGateway();
      for (EventSeatObj eventSeat : eventSeatList) {
        if (eventSeat.isUndefinedCategory()) {
          JOptionPane.showMessageDialog(tablePanel, "У доступных мест должна быть задана категория", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (actionEvent.isQuota() && eventSeat.getBarcode() == null) {
          JOptionPane.showMessageDialog(tablePanel, "У доступных мест должен быть задан штрих-код (квота)", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (gatewayObj.getId() == 0 && eventSeatTableModel.isBarcodeUsed() && eventSeat.getBarcode() == null) {//устарело, ШК только через квоты
          JOptionPane.showMessageDialog(tablePanel, "У доступных мест должен быть задан штрих-код", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    }
    String text = "Вы хотите изменить статус " + L10n.pluralVal(eventSeatList.size(), "выделенного места", "выделенных мест", "выделенных мест") +
        " на '" + newState.getDesc() + "'?";
    if (JOptionPane.showConfirmDialog(tablePanel, text, "Подтверждение", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
      Object[] obj = new Object[4];
      obj[0] = actionEvent.getId();
      obj[1] = oldState.getId();
      obj[2] = newState.getId();
      long[] seatIds = new long[eventSeatList.size()];
      for (int i = 0; i < eventSeatList.size(); i++) {
        EventSeatObj eventSeat = eventSeatList.get(i);
        seatIds[i] = eventSeat.getId();
      }
      Arrays.sort(seatIds);
      obj[3] = seatIds;
      net.create("SET_EVENT_SEAT_STATE", new Request(obj), this, true, Env.GATEWAY_TIMEOUT).start();
    }
  }

  private boolean getCommonSelected(@NotNull List<Integer> viewList) {
    Boolean result = null;
    for (Integer viewRow : viewList) {
      if (result == null) result = eventSeatTable.isRowSelected(viewRow);
      else if (result != eventSeatTable.isRowSelected(viewRow)) return false;
    }
    if (result == null) return false;
    else return result;
  }

  @Override
  public void netPoolStarted() {
    updateButton.setEnabled(false);
    addQuotaButton.setEnabled(false);
    returnQuotaButton.setEnabled(false);
    infoEBSButton.setEnabled(false);
    syncEBSButton.setEnabled(false);
    ebsViewCheckBox.setEnabled(false);
    orderCatButton.setEnabled(false);
    setSplButton.setEnabled(false);
    busyLabel.setVisible(true);
    busyLabel.setBusy(true);
  }

  @Override
  public void netPoolFinished() {
    updateButton.setEnabled(true);
    addQuotaButton.setEnabled(true);
    returnQuotaButton.setEnabled(true);
    infoEBSButton.setEnabled(true);
    syncEBSButton.setEnabled(true);
    ebsViewCheckBox.setEnabled(true);
    orderCatButton.setEnabled(true);
    setSplButton.setEnabled(true);
    busyLabel.setBusy(false);
    busyLabel.setVisible(false);
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (result.getCommand().equals("GET_EVENT_SEAT_LIST")
        || result.getCommand().equals("SET_EVENT_SEAT_STATE")
        || result.getCommand().equals("SET_EVENT_SEAT_CATEGORY")
        || result.getCommand().equals("SET_EVENT_CATEGORY_ORDER")
        || result.getCommand().equals("SET_EVENT_SPL")
        || result.getCommand().startsWith("ADD_QUOTA_")
        || result.getCommand().equals("RETURN_QUOTA")
        || result.getCommand().equals("SYNC_EBS")) {
      Object[] data = (Object[]) result.getResponse().getData();
      boolean success = (Boolean) data[0];
      actionEventData = (ActionEventData) data[1];
      if (actionEventData.getSvgData() != null) {
        boolean ebsView = (actionEventData.isEbsIdsPresent() && ebsViewCheckBox.isSelected());
        planPanel.setEventSvgData(actionEvent.isCombinedPlan(), actionEventData.getSvgData(), actionEventData.getEbsNotAvailIdSet(), ebsView, placementSelection, this);
      }
      if (actionEventData.isSplExists()) {
        setSplButton.setText("Заменить spl");
        splLabel.setText("SPL: есть");
      } else {
        setSplButton.setText("Добавить spl");
        splLabel.setText("SPL: нет");
      }
      eventSeatTableModel.setData(actionEventData.getEventSeatList(), actionEventData.getCategoryPriceList(), actionEventData.getEbsNotAvailIdSet());
      eventSeatTableSelectionChanged();
      eventSeatTable.packAll();
      ebsViewCheckBox.setEnabled(actionEventData.isEbsIdsPresent());
      if (!actionEventData.isEbsIdsPresent()) ebsViewCheckBox.setSelected(false);//после eventSeatTableModel.setData()
      if (!success) {
        if (result.getCommand().equals("SET_EVENT_SEAT_STATE") || result.getCommand().equals("SET_EVENT_SEAT_CATEGORY")) {
          JOptionPane.showMessageDialog(this, "Не удалось внести изменения", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } else if (result.getCommand().equals("SET_EVENT_CATEGORY_ORDER")) {
          JOptionPane.showMessageDialog(this, "Схема не поддерживает управление категориями", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      } else {
        if (result.getCommand().equals("SET_EVENT_SEAT_STATE") || result.getCommand().equals("SET_EVENT_SEAT_CATEGORY")) {
          Env.updateCategoryPrices(actionEventData, actionEvent, actionEventComboBox);
        }
      }
      if (result.getCommand().equals("SET_EVENT_SPL")) {
        JOptionPane.showMessageDialog(this, "Схема spl загружена в сеанс", "Сообщение", JOptionPane.INFORMATION_MESSAGE);
      }
      if (result.getCommand().startsWith("ADD_QUOTA_")) {
        boolean pricesChanged = (Boolean) data[2];
        String report = (String) data[3];
        if (success) {
          QuotaInObj request = (QuotaInObj) result.getRequest().getData();
          if (!request.isConfirmed()) {//1-й этап успешно
            if (ScrollOptionPane.showConfirmDialog(this, report, "Добавить квоту?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              request.setConfirmed(true);
              net.create(result.getCommand(), new Request(request), this, true).start();
            }
          } else {//2-й этап успешно
            Env.updateCategoryPrices(actionEventData, actionEvent, actionEventComboBox);
            ScrollOptionPane.showMessageDialog(this, report, "Квота добавлена", JOptionPane.INFORMATION_MESSAGE, pricesChanged);
            if (pricesChanged) {
              JOptionPane.showMessageDialog(this, "После добавления квоты изменились цены", "Внимание", JOptionPane.WARNING_MESSAGE);
            }
          }
        } else {//любой этап неуспешно
          ScrollOptionPane.showMessageDialog(this, report, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      }
      if (result.getCommand().equals("RETURN_QUOTA")) {
        boolean returnFile = (Boolean) data[2];
        String report = (String) data[3];
        long[] eventSeatIds = (long[]) data[4];
        if (success) {//пока всегда true
          QuotaOutObj request = (QuotaOutObj) result.getRequest().getData();
          if (!request.isConfirmed()) {//1-й этап успешно
            if (ScrollOptionPane.showConfirmDialog(this, report, "Вернуть квоту?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
              request.setConfirmedSeatIds(eventSeatIds);
              request.setConfirmed(true);
              request.setReturnFile(returnFile);
              net.create(result.getCommand(), new Request(request), this, true).start();
              FileChoosers.getSaveQuotaDialog();//создаем заранее
            }
          } else {//2-й этап успешно
            Env.updateCategoryPrices(actionEventData, actionEvent, actionEventComboBox);
            ScrollOptionPane.showMessageDialog(this, report, "Квота возвращена", JOptionPane.INFORMATION_MESSAGE, returnFile);
            if (returnFile) {
              String fileName = (String) data[5];
              byte[] fileData = (byte[]) data[6];
              FileChooser saveQuotaDialog = FileChoosers.getSaveQuotaDialog();
              saveQuotaDialog.setSelectedFile(new File(fileName));
              if (saveQuotaDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = saveQuotaDialog.getSelectedFile();
                try {
                  Files.write(file.toPath(), fileData);
                } catch (IOException e) {
                  e.printStackTrace();
                  JOptionPane.showMessageDialog(this, "Произошла ошибка при сохранении файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
              }
            }
          }
        } else {//любой этап неуспешно
          ScrollOptionPane.showMessageDialog(this, report, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      }
      if (result.getCommand().equals("SYNC_EBS")) {
        boolean pricesChanged = (Boolean) data[2];
        String report = (String) data[3];
        Env.updateCategoryPrices(actionEventData, actionEvent, actionEventComboBox);
        if (pricesChanged) {
          JOptionPane.showMessageDialog(this, "После синхронизации с ВБС изменились цены", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
        ScrollOptionPane.showMessageDialog(this, report, "Отчет о синхронизации", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.RIGHT));
        if (syncListener != null) syncListener.syncComplete(new ActionEventSyncEvent(this, actionEventData));
      }
    }
    if (result.getCommand().equals("GET_QUOTA_OUT_FILE")) {
      Object[] data = (Object[]) result.getResponse().getData();
      String fileName = (String) data[0];
      byte[] fileZip = (byte[]) data[1];
      FileChooser saveQuotaDialog = FileChoosers.getSaveQuotaDialog();
      saveQuotaDialog.setSelectedFile(new File(fileName));
      if (saveQuotaDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = saveQuotaDialog.getSelectedFile();
        try {
          Files.write(file.toPath(), SVGTranscoder.fromGzip(fileZip));
        } catch (IOException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(this, "Произошла ошибка при сохранении файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    if (result.getCommand().equals("GET_GATEWAY_EVENT_INFO")) {
      String report = (String) result.getResponse().getData();
      ScrollOptionPane.showMessageDialog(this, report, "Доп. информация от ВБС", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.LEFT));
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.getCommand().equals("ADD_QUOTA_MANUAL")) {
      setQuotaInManual(((QuotaInManualObj) error.getRequest().getData()));
    }
    if (error.getCommand().startsWith("GET")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().startsWith("SET")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось изменить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().startsWith("ADD_QUOTA_")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить квоту", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("RETURN_QUOTA")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось вернуть квоту", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("GET_GATEWAY_EVENT_INFO")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить доп. информацию по сеансу ВБС", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("SYNC_EBS")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось провести синхронизацию", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("RETURN_QUOTA")) {
      QuotaOutObj quota = (QuotaOutObj) error.getRequest().getData();
      if (quota.isReturnFile() && error.isDataSent()) {//isReturnFile() может быть true только после второго этапа
        if (JOptionPane.showConfirmDialog(this, "Файл для возврата квоты не получен, повторить запрос?", "Ошибка",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
          net.create("GET_QUOTA_OUT_FILE", new Request(new Object[]{quota.getActionEventId(), quota.getNumber()}), this).start();
        }
      }
    }
    if (error.getCommand().equals("GET_QUOTA_OUT_FILE")) {
      Object request = error.getRequest().getData();
      if (JOptionPane.showConfirmDialog(this, "Файл для возврата квоты не получен, повторить запрос?", "Ошибка",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
        net.create(error.getCommand(), new Request(request), this).start();
      }
    }
  }

  @Override
  public void seatClicked(final long seatId, final boolean shiftDown, final boolean controlDown, final boolean altDown) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ignoreSelectionChange = true;
        if (!controlDown) eventSeatTable.clearSelection();
        List<Integer> indexList;
        if (shiftDown && altDown) {
          indexList = eventSeatTableModel.getAllIndexList();
        } else if (shiftDown) {
          indexList = eventSeatTableModel.getRowIndexList(seatId);
        } else if (altDown) {
          indexList = eventSeatTableModel.getSectorIndexList(seatId);
        } else {
          indexList = Collections.singletonList(eventSeatTableModel.getSeatIndex(seatId));
        }
        List<Integer> viewList = new ArrayList<>(indexList.size());
        for (Integer modelRow : indexList) {
          viewList.add(eventSeatTable.convertRowIndexToView(modelRow));
        }
        @SuppressWarnings("SimplifiableConditionalExpression")
        boolean selected = (controlDown ? getCommonSelected(viewList) : false);
        if (selected) {
          for (Integer viewRow : viewList) {
            eventSeatTable.removeRowSelectionInterval(viewRow, viewRow);
          }
        } else {
          for (Integer viewRow : viewList) {
            eventSeatTable.addRowSelectionInterval(viewRow, viewRow);
          }
        }
        ignoreSelectionChange = false;
        eventSeatTableSelectionChanged();
      }
    });
  }

  private class AddQuotaPredicate implements HighlightPredicate {
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
      if (quotaInManual == null) return false;
      JTable table = (JTable) adapter.getComponent();
      int row = table.convertRowIndexToModel(adapter.row);
      Long seatId = eventSeatTableModel.getEventSeatId(row);
      if (seatId == null) return false;
      return quotaInManual.getSeatIdPriceCatMap().containsKey(seatId);
    }
  }
}
