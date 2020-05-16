/*
 * Created by JFormDesigner on Wed Mar 15 14:12:19 MSK 2017
 */

package client.reporter;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import client.component.WaitingDialog;
import client.component.summary.JXSummaryTable;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.reporter.model.QueryStatsTableModel;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.SortOrder;
import server.protocol2.*;
import server.protocol2.reporter.RFrontend;

/**
 * @author Maksim
 */
public class QueryStatsFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXDatePicker datePicker;
  private JXSummaryTable queryStatsTable;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final DateFormat requestFormat = new SimpleDateFormat("yyyyMMdd");
  private final Map<Long, RFrontend> frontendMap;
  private final QueryStatsTableModel queryStatsTableModel = new QueryStatsTableModel();
  private WaitingDialog waitingDialog;

  public QueryStatsFrame(Window owner, List<RFrontend> allFrontendList) {
    frontendMap = new HashMap<>(allFrontendList.size());
    for (RFrontend frontend : allFrontendList) {
      frontendMap.put(frontend.getId(), frontend);
    }
    initComponents();

    queryStatsTable.setModel(queryStatsTableModel);
    queryStatsTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    queryStatsTable.addHighlighter(HighlighterFactory.createSimpleStriping(new Color(240, 240, 224)));

    Dimension frameSize = new Dimension(owner.getWidth(), (int) (owner.getHeight() * 0.8));
    setPreferredSize(frameSize);
    pack();
    setLocationRelativeTo(owner);
    queryStatsTable.packAll();
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b) {
      int state = getExtendedState();
      if ((state & ICONIFIED) != 0) setExtendedState(state & ~ICONIFIED);
    }
  }

  private void getButtonActionPerformed() {
    Date date = datePicker.getDate();
    if (date == null) {
      datePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Не указана дата", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    Env.net.create("GET_QUERY_STATS", new Request(requestFormat.format(date)), this).start();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    datePicker = new JXDatePicker(new Date());
    JButton getButton = new JButton();
    JScrollPane scrollPane1 = new JScrollPane();
    queryStatsTable = new JXSummaryTable();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0417\u0430\u043f\u0440\u043e\u0441\u044b \u043f\u043e \u043f\u0440\u043e\u0442\u043e\u043a\u043e\u043b\u0443");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(5, 5, 0, 5));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
      ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //---- label1 ----
      label1.setText("\u0414\u0435\u043d\u044c:");
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- datePicker ----
      datePicker.setFormats("EEE dd.MM.yyyy");
      panel1.add(datePicker, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- getButton ----
      getButton.setText("\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c");
      getButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          getButtonActionPerformed();
        }
      });
      panel1.add(getButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //======== scrollPane1 ========
      {

        //---- queryStatsTable ----
        queryStatsTable.setHorizontalScrollEnabled(true);
        scrollPane1.setViewportView(queryStatsTable);
      }
      panel1.add(scrollPane1, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(panel1, BorderLayout.CENTER);
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
    TableColumn sortedColumn = queryStatsTable.getSortedColumn();
    SortOrder sortOrder = null;
    if (sortedColumn != null) sortOrder = queryStatsTable.getSortOrder(sortedColumn.getIdentifier());
    Map<Long, Map<String, Integer>> statsMap = (Map<Long, Map<String, Integer>>) result.getResponse().getData();
    queryStatsTableModel.setData(frontendMap, statsMap);
    if (sortedColumn == null || sortOrder == null) {
      queryStatsTable.setSortOrder(queryStatsTableModel.getColumnName(1), SortOrder.DESCENDING);
    } else {
      queryStatsTable.setSortOrder(sortedColumn.getIdentifier(), sortOrder);
    }
    queryStatsTable.packAll();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
