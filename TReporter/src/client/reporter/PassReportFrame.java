/*
 * Created by JFormDesigner on Wed Mar 15 14:12:19 MSK 2017
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.WaitingDialog;
import client.component.suggestion.SuggestionComboBox;
import client.component.summary.JXSummaryTable;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.reporter.component.renderer.*;
import client.reporter.model.*;
import org.jdesktop.swingx.decorator.*;
import server.protocol2.*;
import server.protocol2.reporter.*;

/**
 * @author Maksim
 */
public class PassReportFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<RCity> cityComboBox;
  private SuggestionComboBox<RVenue> venueComboBox;
  private SuggestionComboBox<RAction> actionComboBox;
  private SuggestionComboBox<RActionEvent> actionEventComboBox;
  private JButton getButton;
  private JXSummaryTable passTable;
  private JLabel barLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final RVenue defVenue = new RVenue(0, 0, "Место: любое");
  private final RActionEvent defActionEvent = new RActionEvent(0, 0, "Сеанс: любой");
  private final PassTableModel passTableModel = new PassTableModel();
  private final List<RVenue> venueList;
  private final List<RAction> actionList;
  private final List<RActionEvent> actionEventList;
  private WaitingDialog waitingDialog;

  public PassReportFrame(Window owner, List<RCity> cityList, List<RVenue> venueList, List<RAction> actionList, List<RActionEvent> actionEventList) {
    this.venueList = new ArrayList<>(venueList);
    this.actionList = new ArrayList<>();
    for (RAction action : actionList) {
      if (action.getKind().getId() == 1) continue;
      this.actionList.add(action);
    }
    this.actionEventList = new ArrayList<>(actionEventList);
    initComponents();

    cityComboBox.addItem(new RCity(0, "Город: любой"));//вызывается cityComboBoxItemStateChanged
    for (RCity city : cityList) {
      cityComboBox.addItem(city);
    }
    venueComboBox.setRenderer(new VenueListRenderer(50));
    ActionListRenderer actionListRenderer = new ActionListRenderer(70);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);
    actionEventComboBox.setRenderer(new ActionEventListRenderer());

    passTable.setModel(passTableModel);
    passTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    passTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) passTableSelectionChanged();
      }
    });
    passTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));
    passTable.addHighlighter(new ColorHighlighter(new PassStatusPredicate(passTableModel, PassObj.Status.CHECK_IN_BY_CONTROLLER), new Color(255, 255, 105), Color.BLACK, new Color(195, 195, 92), Color.BLACK));
    passTable.addHighlighter(new ColorHighlighter(new PassStatusPredicate(passTableModel, PassObj.Status.STOP), new Color(255, 209, 155), Color.BLACK, new Color(210, 200, 179), Color.BLACK));
    if (Env.user.getUserType() == UserType.AGENT) {
      passTable.getColumnExt(passTableModel.getColumnName(2)).setVisible(false);
    }

    pack();
    setLocationRelativeTo(owner);
    passTable.packAll();
    venueComboBox.setPreferredSize(venueComboBox.getSize());
    actionComboBox.setPreferredSize(actionComboBox.getSize());
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) {
      int state = getExtendedState();
      if ((state & ICONIFIED) != 0) setExtendedState(state & ~ICONIFIED);
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
      RVenue venue = venueComboBox.getItemAt(venueComboBox.getSelectedIndex());
      RCity city = cityComboBox.getItemAt(cityComboBox.getSelectedIndex());
      List<RAction> filterList = new ArrayList<>();
      for (RAction action : actionList) {
        if (action.pass(venue) && action.pass(city)) filterList.add(action);
      }
      actionComboBox.removeAllItems();
      for (RAction action : filterList) {
        actionComboBox.addItem(action);//на первой итерации вызывается actionComboBoxItemStateChanged
      }
      if (filterList.isEmpty()) {
        actionEventComboBox.removeAllItems();
        getButton.setEnabled(false);
      }
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
      actionEventComboBox.addItem(defActionEvent);
      for (RActionEvent actionEvent : filterList) {
        actionEventComboBox.addItem(actionEvent);
      }
    }
  }

  private void actionEventComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      getButton.setEnabled(true);
    }
  }

  private void passTableSelectionChanged() {
    int count = passTable.getSelectedRowCount();
    if (passTable.isRowSelected(passTable.getRowCount() - 1)) count--;
    barLabel.setText("Выделено событий: " + count);
  }

  private void getButtonActionPerformed() {
    RAction action = actionComboBox.getItemAt(actionComboBox.getSelectedIndex());
    RActionEvent actionEvent = actionEventComboBox.getItemAt(actionEventComboBox.getSelectedIndex());
    if (action == null) return;
    Long actionEventId = (actionEvent != defActionEvent ? actionEvent.getId() : null);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_PASS_LIST", new Request(new Object[]{action.getId(), actionEventId, Boolean.FALSE}), this).start();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    cityComboBox = new SuggestionComboBox<>();
    venueComboBox = new SuggestionComboBox<>();
    JPanel panel3 = new JPanel();
    JLabel label1 = new JLabel();
    actionComboBox = new SuggestionComboBox<>();
    actionEventComboBox = new SuggestionComboBox<>();
    getButton = new JButton();
    JScrollPane scrollPane1 = new JScrollPane();
    passTable = new JXSummaryTable();
    barLabel = new JLabel();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u0431\u0438\u043b\u0435\u0442\u043e\u0432");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 0, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

      //======== panel2 ========
      {
        panel2.setLayout(new GridBagLayout());
        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
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
          new Insets(0, 0, 0, 0), 0, 0));
      }
      panel1.add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== panel3 ========
      {
        panel3.setLayout(new GridBagLayout());
        ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
        ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u041f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435:");
        panel3.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- actionComboBox ----
        actionComboBox.setMaximumRowCount(18);
        actionComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            actionComboBoxItemStateChanged(e);
          }
        });
        panel3.add(actionComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
        panel3.add(actionEventComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- getButton ----
        getButton.setText("\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c");
        getButton.setEnabled(false);
        getButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            getButtonActionPerformed();
          }
        });
        panel3.add(getButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      panel1.add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== scrollPane1 ========
      {

        //---- passTable ----
        passTable.setColumnControlVisible(true);
        passTable.setHorizontalScrollEnabled(true);
        scrollPane1.setViewportView(passTable);
      }
      panel1.add(scrollPane1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.CENTER);

    //---- barLabel ----
    barLabel.setFont(barLabel.getFont().deriveFont(barLabel.getFont().getStyle() & ~Font.BOLD, barLabel.getFont().getSize() - 1f));
    barLabel.setText(" ");
    barLabel.setBorder(new EmptyBorder(0, 5, 1, 5));
    contentPane.add(barLabel, BorderLayout.SOUTH);
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
    List<PassObj> passList = (List<PassObj>) result.getResponse().getData();
    passTableModel.setData(passList);
    passTable.packAll();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
