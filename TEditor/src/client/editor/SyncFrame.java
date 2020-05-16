/*
 * Created by JFormDesigner on Wed Apr 12 15:02:12 MSK 2017
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.*;
import client.editor.component.listener.*;
import client.editor.component.renderer.ActionEventSyncListRenderer;
import client.net.*;
import client.utils.*;
import org.jdesktop.swingx.JXList;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.editor.*;

import static client.editor.Env.net;

/**
 * @author Maksim
 */
public class SyncFrame extends JFrame implements ActionEventSyncListener, NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXList eventList;
  private JButton controlButton;
  private JButton syncButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final OperationComboBox<ActionEventObj> actionEventComboBox;
  private final WaitingDialog waitingDialog;
  private final DefaultListModel<ActionEventSync> eventListModel = new DefaultListModel<>();

  public SyncFrame(Window owner, OperationComboBox<ActionEventObj> actionEventComboBox) {
    this.actionEventComboBox = actionEventComboBox;
    initComponents();
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    ActionEventSyncListRenderer listRenderer = new ActionEventSyncListRenderer(Env.user.getUserType() == UserType.OPERATOR);
    eventList.setModel(eventListModel);
    eventList.setCellRenderer(listRenderer);
    ComponentClipboard.setJListCopyAction(eventList, listRenderer);

    pack();
    this.setSize(owner.getWidth() / 2, this.getHeight());
    setLocationRelativeTo(owner);
  }

  public void startWork() {
    this.setVisible(true);
    if ((this.getExtendedState() & ICONIFIED) != 0) this.setExtendedState(this.getExtendedState() & ~ICONIFIED);
    this.toFront();
    updateButtonActionPerformed();
  }

  private void eventListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      syncButton.setEnabled(!eventList.isSelectionEmpty());
      controlButton.setEnabled(eventList.getSelectedIndices().length == 1);
    }
  }

  private void updateButtonActionPerformed() {
    net.create("GET_EVENT_SYNC_LIST", new Request(null), this).start();
  }

  private void syncButtonActionPerformed() {
    @SuppressWarnings("unchecked")
    List<ActionEventSync> elementList = eventList.getSelectedValuesList();
    if (elementList.isEmpty()) return;
    if (elementList.size() == 1) {
      ActionEventSync actionEventSync = elementList.get(0);
      net.create("SYNC_EBS", new Request(actionEventSync.getId()), this, 150000).start();
    } else {
      new SyncDialog(this, actionEventComboBox, elementList, this).startWork();
    }
  }

  private void controlButtonActionPerformed() {
    ActionEventSync actionEventSync = (ActionEventSync) eventList.getSelectedValue();
    if (actionEventSync == null) return;
    ActionEventObj actionEvent = getActionEventById(actionEventSync.getId());
    if (actionEvent == null) {
      JOptionPane.showMessageDialog(this, "Данный сеанс еще не загружен в основное окно программы, обновите данные в основном окне", "Ошибка", JOptionPane.ERROR_MESSAGE);
    } else {
      new EventControlFrame(this, actionEvent, actionEventComboBox, this).startWork();
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel mainPanel = new JPanel();
    JLabel label1 = new JLabel();
    JScrollPane scrollPane = new JScrollPane();
    eventList = new JXList();
    JPanel buttonPanel = new JPanel();
    JButton updateButton = new JButton();
    controlButton = new JButton();
    JPanel panel1 = new JPanel();
    syncButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u0441 \u0412\u0411\u0421");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== mainPanel ========
    {
      mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      mainPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u0441\u0435\u0430\u043d\u0441\u043e\u0432 \u0441 \u0440\u0430\u0441\u0445\u043e\u0436\u0434\u0435\u043d\u0438\u0435\u043c \u043c\u0435\u0441\u0442:");
      mainPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== scrollPane ========
      {

        //---- eventList ----
        eventList.setVisibleRowCount(25);
        eventList.setToolTipText("\u0421 \u043f\u043e\u043c\u043e\u0449\u044c\u044e Shift \u0438\u043b\u0438 Ctrl \u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0430\u043d\u0441\u043e\u0432");
        eventList.addListSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            eventListValueChanged(e);
          }
        });
        scrollPane.setViewportView(eventList);
      }
      mainPanel.add(scrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(mainPanel, BorderLayout.CENTER);

    //======== buttonPanel ========
    {
      buttonPanel.setBorder(new EmptyBorder(3, 5, 5, 5));
      buttonPanel.setLayout(new BorderLayout(5, 5));

      //---- updateButton ----
      updateButton.setText("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c");
      updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateButtonActionPerformed();
        }
      });
      buttonPanel.add(updateButton, BorderLayout.WEST);

      //---- controlButton ----
      controlButton.setText("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0435\u0441\u0442\u0430\u043c\u0438");
      controlButton.setEnabled(false);
      controlButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          controlButtonActionPerformed();
        }
      });
      buttonPanel.add(controlButton, BorderLayout.EAST);

      //======== panel1 ========
      {
        panel1.setLayout(new GridBagLayout());
        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //---- syncButton ----
        syncButton.setText("\u0421\u0438\u043d\u0445. \u0441 \u0412\u0411\u0421");
        syncButton.setEnabled(false);
        syncButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            syncButtonActionPerformed();
          }
        });
        panel1.add(syncButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      buttonPanel.add(panel1, BorderLayout.CENTER);
    }
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Nullable
  private ActionEventObj getActionEventById(long actionEventId) {
    for (ActionEventObj actionEvent : actionEventComboBox.getElementList()) {
      if (actionEvent.getId() == actionEventId) return actionEvent;
    }
    return null;
  }

  @Override
  public void syncComplete(@NotNull ActionEventSyncEvent event) {
    Enumeration<ActionEventSync> enumeration = eventListModel.elements();
    while (enumeration.hasMoreElements()) {
      ActionEventSync actionEventSync = enumeration.nextElement();
      if (actionEventSync.getId() == event.getActionEventData().getId()) {
        eventListModel.removeElement(actionEventSync);
        break;
      }
    }
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

    if (result.getCommand().equals("GET_EVENT_SYNC_LIST")) {
      List<ActionEventSync> eventSyncList = (List<ActionEventSync>) result.getResponse().getData();
      eventListModel.clear();
      for (ActionEventSync actionEventSync : eventSyncList) {
        eventListModel.addElement(actionEventSync);
      }
    }
    if (result.getCommand().equals("SYNC_EBS")) {
      Object[] data = (Object[]) result.getResponse().getData();
      ActionEventData actionEventData = (ActionEventData) data[1];
      boolean pricesChanged = (Boolean) data[2];
      String report = (String) data[3];
      Env.updateCategoryPrices(actionEventData, null, actionEventComboBox);
      if (pricesChanged) {
        JOptionPane.showMessageDialog(this, "После синхронизации с ВБС изменились цены", "Внимание", JOptionPane.WARNING_MESSAGE);
      }
      ScrollOptionPane.showMessageDialog(this, report, "Отчет о синхронизации", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.RIGHT));
      syncComplete(new ActionEventSyncEvent(this, actionEventData));
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.getCommand().equals("GET_EVENT_SYNC_LIST")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("SYNC_EBS")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось провести синхронизацию", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }
}
