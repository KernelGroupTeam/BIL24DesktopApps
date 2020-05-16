/*
 * Created by JFormDesigner on Sun Apr 14 02:21:22 MSK 2019
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.*;
import client.editor.component.listener.*;
import client.net.*;
import client.utils.Position;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.editor.*;

import static client.editor.Env.net;

/**
 * @author Maksim
 */
public class SyncDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel counterLabel;
  private JProgressBar progressBar;
  private JCheckBox reportsCheckBox;
  private JButton cancelButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final OperationComboBox<ActionEventObj> actionEventComboBox;
  @NotNull
  private final List<ActionEventSync> eventSyncList;
  @Nullable
  private final ActionEventSyncListener syncListener;
  private int index = 0;
  private boolean interrupted = false;

  public SyncDialog(Window owner, OperationComboBox<ActionEventObj> actionEventComboBox, @NotNull List<ActionEventSync> eventSyncList) {
    this(owner, actionEventComboBox, eventSyncList, null);
  }

  public SyncDialog(Window owner, OperationComboBox<ActionEventObj> actionEventComboBox, @NotNull List<ActionEventSync> eventSyncList, @Nullable ActionEventSyncListener syncListener) {
    super(owner);
    if (eventSyncList.size() < 2) throw new IllegalArgumentException("eventSyncList size");
    this.actionEventComboBox = actionEventComboBox;
    this.eventSyncList = eventSyncList;
    this.syncListener = syncListener;

    initComponents();
    counterLabel.setText("0 из " + eventSyncList.size());
    progressBar.setMaximum(eventSyncList.size());
  }

  public void startWork() {
    if (index != 0) return;
    net.create("SYNC_EBS", new Request(eventSyncList.get(index).getId()), this, 150000).start();
    this.setVisible(true);
  }

  private void cancelButtonActionPerformed() {
    cancelButton.setEnabled(false);
    interrupted = true;
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    counterLabel = new JLabel();
    progressBar = new JProgressBar();
    reportsCheckBox = new JCheckBox();
    JPanel buttonBar = new JPanel();
    cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f \u0441 \u0412\u0411\u0421...");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 100, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u043e\u0432\u0430\u043d\u043e \u0441\u0435\u0430\u043d\u0441\u043e\u0432:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- counterLabel ----
        counterLabel.setText("888 \u0438\u0437 888");
        contentPanel.add(counterLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- progressBar ----
        progressBar.setStringPainted(true);
        contentPanel.add(progressBar, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- reportsCheckBox ----
        reportsCheckBox.setText("\u0412\u044b\u0432\u043e\u0434\u0438\u0442\u044c \u043e\u0442\u0447\u0435\u0442\u044b");
        reportsCheckBox.setSelected(true);
        contentPanel.add(reportsCheckBox, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
          GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new GridBagLayout());

        //---- cancelButton ----
        cancelButton.setText("\u041e\u0442\u043c\u0435\u043d\u0430");
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cancelButtonActionPerformed();
          }
        });
        buttonBar.add(cancelButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {

  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    ActionEventSync eventSync = eventSyncList.get(index);
    String eventSyncStr = eventSync.getShowTime() + " " + eventSync.getActionName();
    if (!result.getResponse().isSuccess()) {
      String report = eventSyncStr + "\n\n" + result.getResponse().getErrorForUser();
      ScrollOptionPane.showMessageDialog(getOwner(), report, "Ошибка при синхронизации", JOptionPane.ERROR_MESSAGE, false,
          ModalExclusionType.APPLICATION_EXCLUDE, new Position(Position.Horizontal.RIGHT));
    } else {
      Object[] data = (Object[]) result.getResponse().getData();
      ActionEventData actionEventData = (ActionEventData) data[1];
      boolean pricesChanged = (Boolean) data[2];
      String report = (String) data[3];
      Env.updateCategoryPrices(actionEventData, null, actionEventComboBox);
      if (pricesChanged) {
        report = "После синхронизации с ВБС изменились цены\n\n" + report;
      }
      report = eventSyncStr + "\n\n" + report;
      if (reportsCheckBox.isSelected()) {
        ScrollOptionPane.showMessageDialog(getOwner(), report, "Отчет о синхронизации", JOptionPane.INFORMATION_MESSAGE, false,
            ModalExclusionType.APPLICATION_EXCLUDE, new Position(Position.Horizontal.RIGHT));
      }
      if (syncListener != null) syncListener.syncComplete(new ActionEventSyncEvent(this, actionEventData));
    }
    index++;
    counterLabel.setText(index + " из " + eventSyncList.size());
    progressBar.setValue(index);
    if (!interrupted && index < eventSyncList.size()) {
      net.create("SYNC_EBS", new Request(eventSyncList.get(index).getId()), this, 150000).start();
    } else {
      this.dispose();
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось провести синхронизацию", "Ошибка", JOptionPane.ERROR_MESSAGE);
    this.dispose();
  }
}
