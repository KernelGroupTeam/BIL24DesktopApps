/*
 * Created by JFormDesigner on Fri Mar 25 13:38:49 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import client.component.*;
import client.component.listener.OperationListener;
import client.component.suggestion.SuggestionComboBox;
import client.editor.component.renderer.*;
import client.editor.model.*;
import client.net.*;
import client.utils.ComponentClipboard;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.*;
import server.protocol2.editor.*;

import static client.editor.Env.net;

/**
 * @author Maksim
 */
public class SubsFrame extends JFrame implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private OperationComboBox<SubsAgentObj> agentComboBox;
  private SuggestionComboBox<CityObj> subsCityComboBox;
  private JButton saveSubsButton;
  private JCheckBox autoAddingNewCheckBox;
  private JLabel headerLabel1;
  private JLabel headerLabel2;
  private JScrollPane scrollPane1;
  private JXList actionList;
  private JScrollPane scrollPane2;
  private JXList subsActionList;
  private JButton addButton;
  private JButton delButton;
  private JCheckBox groupCheckBox;
  private JButton exButton;
  private JButton inButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final CityObj defCity = new CityObj(0, "Город: любой");
  private final WaitingDialog waitingDialog;
  private final SubsActionFilter subsActionFilter1 = new SubsActionFilter(SubsActionFilter.Type.ACTION);
  private final SubsActionFilter subsActionFilter2 = new SubsActionFilter(SubsActionFilter.Type.SUBS_ACTION);
  private final ActionListRenderer actionListRenderer = new ActionListRenderer();
  private final SubsActionListRenderer subsActionListRenderer = new SubsActionListRenderer();
  private final ActionListModel actionListModel = new ActionListModel();
  private final SubsActionListModel subsActionListModel = new SubsActionListModel();
  private int mainSubsId;
  private boolean saveResult;

  public SubsFrame(Window owner) {
    initComponents();

    AgentListRenderer agentListRenderer = new AgentListRenderer();
    agentComboBox.setRenderer(agentListRenderer);
    agentComboBox.setElementToStringConverter(agentListRenderer);
    agentComboBox.setChangeConfirmComponent(this);
    agentComboBox.setChangeConfirmText("Сохранить изменения в подписке агента?");
    agentComboBox.setOperationListener(new AgentSubsOperationListener());
    agentComboBox.listenChanges(autoAddingNewCheckBox);
    agentComboBox.listenChangesIgnoringOrder(subsActionListModel, new Comparator<SubsActionObj>() {
      @Override
      public int compare(SubsActionObj o1, SubsActionObj o2) {
        return Long.compare(o1.getId(), o2.getId());
      }
    });
    agentComboBox.enableComponentChanges(saveSubsButton);

    CityListRenderer cityListRenderer = new CityListRenderer();
    subsCityComboBox.setRenderer(cityListRenderer);
    subsCityComboBox.setElementToStringConverter(cityListRenderer);

    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.DOCUMENT_MODAL);
    actionList.setModel(actionListModel);
    actionList.setCellRenderer(actionListRenderer);
    actionList.setTransferHandler(new ActionListTransferHandler());
    actionList.setFilterEnabled(true);
    actionList.setFilters(new FilterPipeline(subsActionFilter1));
    ComponentClipboard.setJListCopyAction(actionList, actionListRenderer);
    subsActionList.setModel(subsActionListModel);
    subsActionList.setCellRenderer(subsActionListRenderer);
    subsActionList.setTransferHandler(new SubsActionListTransferHandler());
    subsActionList.setFilterEnabled(true);
    subsActionList.setFilters(new FilterPipeline(subsActionFilter2));
    ComponentClipboard.setJListCopyAction(subsActionList, subsActionListRenderer);

    int maxWidth = Math.max(headerLabel1.getPreferredSize().width, headerLabel2.getPreferredSize().width);
    maxWidth = Math.max(maxWidth, groupCheckBox.getPreferredSize().width);
    maxWidth = Math.max(maxWidth, exButton.getPreferredSize().width + 5 + inButton.getPreferredSize().width);
    scrollPane1.setPreferredSize(new Dimension(maxWidth, scrollPane1.getPreferredSize().height));
    scrollPane2.setPreferredSize(new Dimension(maxWidth, scrollPane2.getPreferredSize().height));

    pack();
    this.setSize(owner.getWidth() / 3 * 2, this.getHeight());
    setLocationRelativeTo(owner);

    if (Env.user.getUserType() == UserType.ORGANIZER) {
      autoAddingNewCheckBox.setEnabled(false);
      groupCheckBox.setVisible(false);
    } else groupCheckBox.setSelected(true);
  }

  public void startWork(OperationComboBox<CityObj> cityComboBox, OperationComboBox<ActionObj> actionComboBox) {
    subsCityComboBox.removeAllItems();
    subsCityComboBox.addItem(defCity);
    for (CityObj city : cityComboBox.getElementList()) {
      subsCityComboBox.addItem(city);
    }
    actionListModel.setData(actionComboBox.getElementList());
    this.setVisible(true);
    if ((this.getExtendedState() & ICONIFIED) != 0) this.setExtendedState(this.getExtendedState() & ~ICONIFIED);
    this.toFront();
    net.create("GET_MAIN_SUBS_AGENT_LIST", new Request(null), this).start();
  }

  private void thisWindowClosing() {
    if (agentComboBox.canExit()) this.setVisible(false);
  }

  private void subsCityComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      actionList.clearSelection();
      subsActionList.clearSelection();
      CityObj city = subsCityComboBox.getItemAt(subsCityComboBox.getSelectedIndex());
      Long cityId = (city == null || city == defCity ? null : city.getId());
      subsActionFilter1.setCityId(cityId);
      subsActionFilter2.setCityId(cityId);
    }
  }

  private void saveSubsButtonActionPerformed() {
    agentComboBox.saveChanges();
  }

  private void actionListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) addButton.setEnabled(!actionList.isSelectionEmpty());
  }

  private void subsActionListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      boolean selectionEmpty = subsActionList.isSelectionEmpty();
      delButton.setEnabled(!selectionEmpty);
      exButton.setEnabled(!selectionEmpty);
      inButton.setEnabled(!selectionEmpty);
    }
  }

  private void addButtonActionPerformed() {
    @SuppressWarnings("unchecked")
    List<ActionObj> elementList = actionList.getSelectedValuesList();
    subsActionListModel.addActionList(elementList);
  }

  private void delButtonActionPerformed() {
    int[] indices = subsActionList.getSelectedIndices();
    int[] modelIndices = new int[indices.length];
    for (int i = 0; i < indices.length; i++) {
      modelIndices[i] = subsActionList.convertIndexToModel(indices[i]);
    }
    subsActionListModel.remove(modelIndices);
  }

  private void exButtonActionPerformed() {
    @SuppressWarnings("unchecked")
    List<SubsActionObj> selSubsActionList = subsActionList.getSelectedValuesList();
    if (selSubsActionList.isEmpty()) return;
    if (!agentComboBox.canExit()) return;
    SubsAgentObj agent = agentComboBox.getSelectedElement();
    if (agent == null) return;
    actionList.clearSelection();
    if (JOptionPane.showConfirmDialog(this, "Сделать выделенные представления недоступными для других агентов?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("EXCLUDE_MAIN_SUBS_AGENT", new Request(new Object[] {mainSubsId, agent.getId(), selSubsActionList}), this).start();
    }
  }

  private void inButtonActionPerformed() {
    @SuppressWarnings("unchecked")
    List<SubsActionObj> selSubsActionList = subsActionList.getSelectedValuesList();
    if (selSubsActionList.isEmpty()) return;
    if (!agentComboBox.canExit()) return;
    SubsAgentObj agent = agentComboBox.getSelectedElement();
    if (agent == null) return;
    actionList.clearSelection();
    if (JOptionPane.showConfirmDialog(this, "Сделать выделенные представления доступными для всех агентов?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("INCLUDE_MAIN_SUBS_AGENT", new Request(new Object[] {mainSubsId, selSubsActionList}), this).start();
    }
  }

  private void groupCheckBoxItemStateChanged(ItemEvent e) {
    boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
    actionListRenderer.setShowOrganizer(selected);
    subsActionListRenderer.setShowOrganizer(selected);
    actionList.clearSelection();
    subsActionList.clearSelection();
    actionListModel.setGroupByOrganizer(selected);
    subsActionListModel.setGroupByOrganizer(selected);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel mainPanel = new JPanel();
    JPanel agentPanel = new JPanel();
    JLabel label1 = new JLabel();
    agentComboBox = new OperationComboBox<>();
    subsCityComboBox = new SuggestionComboBox<>();
    saveSubsButton = new JButton();
    autoAddingNewCheckBox = new JCheckBox();
    JPanel subsPanel = new JPanel();
    headerLabel1 = new JLabel();
    headerLabel2 = new JLabel();
    scrollPane1 = new JScrollPane();
    actionList = new JXList();
    scrollPane2 = new JScrollPane();
    subsActionList = new JXList();
    addButton = new JButton();
    delButton = new JButton();
    groupCheckBox = new JCheckBox();
    JPanel panel1 = new JPanel();
    exButton = new JButton();
    inButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041f\u043e\u0434\u043f\u0438\u0441\u043a\u0438");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        thisWindowClosing();
      }
    });
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

      //======== agentPanel ========
      {
        agentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)agentPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
        ((GridBagLayout)agentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)agentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)agentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u0410\u0433\u0435\u043d\u0442:");
        agentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- agentComboBox ----
        agentComboBox.setMaximumRowCount(12);
        agentPanel.add(agentComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- subsCityComboBox ----
        subsCityComboBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            subsCityComboBoxItemStateChanged(e);
          }
        });
        agentPanel.add(subsCityComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- saveSubsButton ----
        saveSubsButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
        saveSubsButton.setMargin(new Insets(2, 2, 2, 2));
        saveSubsButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
        saveSubsButton.setEnabled(false);
        saveSubsButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            saveSubsButtonActionPerformed();
          }
        });
        agentPanel.add(saveSubsButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- autoAddingNewCheckBox ----
        autoAddingNewCheckBox.setText("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0435 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043d\u043e\u0432\u044b\u0445 \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0439");
        autoAddingNewCheckBox.setSelected(true);
        agentPanel.add(autoAddingNewCheckBox, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0,
          GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(agentPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //======== subsPanel ========
      {
        subsPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)subsPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
        ((GridBagLayout)subsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)subsPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)subsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

        //---- headerLabel1 ----
        headerLabel1.setText("\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u044f:");
        subsPanel.add(headerLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- headerLabel2 ----
        headerLabel2.setText("\u041f\u043e\u0434\u043f\u0438\u0441\u0430\u043d\u043d\u044b\u0435 \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u044f:");
        subsPanel.add(headerLabel2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane1 ========
        {

          //---- actionList ----
          actionList.setToolTipText("\u0421 \u043f\u043e\u043c\u043e\u0449\u044c\u044e Shift \u0438\u043b\u0438 Ctrl \u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0439");
          actionList.setDragEnabled(true);
          actionList.setVisibleRowCount(25);
          actionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
              actionListValueChanged(e);
            }
          });
          scrollPane1.setViewportView(actionList);
        }
        subsPanel.add(scrollPane1, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //======== scrollPane2 ========
        {

          //---- subsActionList ----
          subsActionList.setToolTipText("\u0421 \u043f\u043e\u043c\u043e\u0449\u044c\u044e Shift \u0438\u043b\u0438 Ctrl \u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0439");
          subsActionList.setDragEnabled(true);
          subsActionList.setVisibleRowCount(25);
          subsActionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
              subsActionListValueChanged(e);
            }
          });
          scrollPane2.setViewportView(subsActionList);
        }
        subsPanel.add(scrollPane2, new GridBagConstraints(2, 1, 1, 4, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- addButton ----
        addButton.setText("\u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u2192");
        addButton.setMargin(new Insets(2, 4, 2, 4));
        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            addButtonActionPerformed();
          }
        });
        subsPanel.add(addButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- delButton ----
        delButton.setText("\u2190 \u0443\u0434\u0430\u043b\u0438\u0442\u044c");
        delButton.setMargin(new Insets(2, 4, 2, 4));
        delButton.setEnabled(false);
        delButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            delButtonActionPerformed();
          }
        });
        subsPanel.add(delButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- groupCheckBox ----
        groupCheckBox.setText("\u0421\u0433\u0440\u0443\u043f\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u043e \u043e\u0440\u0433\u0430\u043d\u0438\u0437\u0430\u0442\u043e\u0440\u0443");
        groupCheckBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            groupCheckBoxItemStateChanged(e);
          }
        });
        subsPanel.add(groupCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 0, 5), 0, 0));

        //======== panel1 ========
        {
          panel1.setLayout(new GridBagLayout());
          ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
          ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- exButton ----
          exButton.setText("\u0442\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f \u0430\u0433\u0435\u043d\u0442\u0430");
          exButton.setMargin(new Insets(2, 4, 2, 4));
          exButton.setEnabled(false);
          exButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              exButtonActionPerformed();
            }
          });
          panel1.add(exButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- inButton ----
          inButton.setText("\u0434\u043b\u044f \u0432\u0441\u0435\u0445 \u0430\u0433\u0435\u043d\u0442\u043e\u0432");
          inButton.setMargin(new Insets(2, 4, 2, 4));
          inButton.setEnabled(false);
          inButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              inButtonActionPerformed();
            }
          });
          panel1.add(inButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        subsPanel.add(panel1, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(subsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(mainPanel, BorderLayout.CENTER);
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
      if (result.getCommand().startsWith("SAVE")) {
        saveResult = false;
        waitingDialog.setVisible(false);
      }
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (result.getCommand().equals("GET_MAIN_SUBS_AGENT_LIST")
        || result.getCommand().equals("EXCLUDE_MAIN_SUBS_AGENT") || result.getCommand().equals("INCLUDE_MAIN_SUBS_AGENT")) {
      boolean reload = (agentComboBox.getSelectedElement() != null);
      Object[] data = (Object[]) result.getResponse().getData();
      mainSubsId = (Integer) data[0];
      agentComboBox.setElementList((List<SubsAgentObj>) data[1]);
      if (reload) agentComboBox.reload();
      else {//если есть агент с таким же названием, выбираем его при первой загрузке
        for (SubsAgentObj subsAgentObj : agentComboBox.getElementList()) {
          if (subsAgentObj.getName().equalsIgnoreCase(Env.user.getAuthorityName())) {
            agentComboBox.setSelectedItem(subsAgentObj);
            break;
          }
        }
      }
    }
    if (result.getCommand().startsWith("SAVE")) {
      mainSubsId = (Integer) result.getResponse().getData();
      saveResult = true;
      waitingDialog.setVisible(false);
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.getCommand().startsWith("GET")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("EXCLUDE_MAIN_SUBS_AGENT") || error.getCommand().equals("INCLUDE_MAIN_SUBS_AGENT")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().startsWith("SAVE")) {
      saveResult = false;
      waitingDialog.setVisible(false);
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось сохранить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private class AgentSubsOperationListener implements OperationListener<SubsAgentObj> {
    @Override
    public void clear() {
      subsActionListModel.clear();
      autoAddingNewCheckBox.setSelected(true);
    }

    @Override
    public boolean check() {
      return true;
    }

    @Override
    public void load(@NotNull SubsAgentObj subsAgent) {
      subsActionListModel.setData(subsAgent.getSubsActionList());
      autoAddingNewCheckBox.setSelected(!subsAgent.isOnlySubscriptions());
    }

    @Override
    public boolean save(@NotNull SubsAgentObj subsAgent) {
      subsAgent.setSubsActionList(subsActionListModel.getSubsActionList());
      subsAgent.setOnlySubscriptions(!autoAddingNewCheckBox.isSelected());
      Network<Request, Response> network = net.create("SAVE_MAIN_SUBS_AGENT", new Request(new Object[]{mainSubsId, subsAgent}), SubsFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }

  private static class ActionListTransferHandler extends TransferHandler {
    @Override
    public boolean canImport(TransferSupport support) {
      return support.isDataFlavorSupported(SubsActionListTransferable.DATA_FLAVOR);
    }

    @Override
    public boolean importData(TransferSupport support) {
      return canImport(support);
    }

    @Nullable
    @Override
    protected Transferable createTransferable(JComponent c) {
      Transferable t = null;
      if (c instanceof JList) {
        JList<?> list = (JList<?>) c;
        List<?> values = list.getSelectedValuesList();
        ActionList actionList = new ActionList(values.size());
        for (Object value : values) {
          if (value instanceof ActionObj) actionList.add((ActionObj) value);
        }
        if (!actionList.isEmpty()) t = new ActionListTransferable(actionList);
      }
      return t;
    }

    @Override
    public int getSourceActions(JComponent c) {
      return DnDConstants.ACTION_COPY;
    }
  }

  private class SubsActionListTransferHandler extends TransferHandler {
    @Override
    public boolean canImport(TransferSupport support) {
      return support.isDataFlavorSupported(ActionListTransferable.DATA_FLAVOR);
    }

    @Override
    public boolean importData(TransferSupport support) {
      if (!canImport(support)) return false;
      try {
        ActionList actionList = (ActionList) support.getTransferable().getTransferData(ActionListTransferable.DATA_FLAVOR);
        subsActionListModel.addActionList(actionList);
        return true;
      } catch (Exception e) {
        return false;
      }
    }

    @Nullable
    @Override
    protected Transferable createTransferable(JComponent c) {
      Transferable t = null;
      if (c instanceof JList) {
        JList<?> list = (JList<?>) c;
        List<?> values = list.getSelectedValuesList();
        SubsActionList actionList = new SubsActionList(values.size());
        for (Object value : values) {
          if (value instanceof SubsActionObj) actionList.add((SubsActionObj) value);
        }
        if (!actionList.isEmpty()) t = new SubsActionListTransferable(actionList);
      }
      return t;
    }

    @Override
    public int getSourceActions(JComponent c) {
      return DnDConstants.ACTION_MOVE;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
      if (action != DnDConstants.ACTION_MOVE) return;
      try {
        SubsActionList subsActionList = (SubsActionList) data.getTransferData(SubsActionListTransferable.DATA_FLAVOR);
        subsActionListModel.remove(subsActionList);
      } catch (Exception ignored) {
      }
    }
  }

  private static class ActionListTransferable implements Transferable {
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ActionList.class, "java/ActionList");
    @NotNull
    private final ActionList actionList;

    public ActionListTransferable(@NotNull ActionList actionList) {
      this.actionList = actionList;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor.equals(DATA_FLAVOR);
    }

    @NotNull
    @Override
    public Object getTransferData(DataFlavor flavor) {
      return actionList;
    }
  }

  private static class SubsActionListTransferable implements Transferable {
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SubsActionList.class, "java/SubsActionList");
    @NotNull
    private final SubsActionList actionList;

    public SubsActionListTransferable(@NotNull SubsActionList actionList) {
      this.actionList = actionList;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor.equals(DATA_FLAVOR);
    }

    @NotNull
    @Override
    public Object getTransferData(DataFlavor flavor) {
      return actionList;
    }
  }

  private static class ActionList extends ArrayList<ActionObj> {
    public ActionList() {
    }

    public ActionList(int initialCapacity) {
      super(initialCapacity);
    }
  }

  private static class SubsActionList extends ArrayList<SubsActionObj> {
    public SubsActionList() {
    }

    public SubsActionList(int initialCapacity) {
      super(initialCapacity);
    }
  }
}
