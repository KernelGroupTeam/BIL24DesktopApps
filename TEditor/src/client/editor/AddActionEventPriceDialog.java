/*
 * Created by JFormDesigner on Fri Jul 24 00:48:01 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import client.component.*;
import client.editor.component.*;
import client.editor.component.listener.*;
import client.editor.component.renderer.ActionEventElementListRenderer;
import client.net.*;
import server.protocol2.*;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class AddActionEventPriceDialog extends JDialog implements ResizeListener, NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel contentPanel;
  private JList<ActionEventElement> mainActionEventList;
  private JButton addButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final Window owner;
  private final ChildlessControl childlessControl;
  private final OperationComboBox<ActionEventObj> actionEventComboBox;
  private final List<CategoryObj> categoryList;
  private final DefaultListModel<ActionEventElement> actionEventModel = new DefaultListModel<>();
  private final Runnable afterSuccessAdding;
  private WaitingDialog waitingDialog;

  public AddActionEventPriceDialog(Window owner, ChildlessControl childlessControl, OperationComboBox<ActionEventObj> actionEventComboBox,
                                   List<CategoryObj> categoryList, List<ActionEventObj> actionEventList, Runnable afterSuccessAdding) {
    super(owner);
    this.owner = owner;
    this.childlessControl = childlessControl;
    this.actionEventComboBox = actionEventComboBox;
    this.categoryList = categoryList;
    this.afterSuccessAdding = afterSuccessAdding;
    if (categoryList.isEmpty()) throw new IllegalArgumentException("category list is empty");
    initComponents();

    ArrayList<ActionEventElement> actionEventElementList = new ArrayList<>(actionEventList.size());
    for (ActionEventObj actionEvent : actionEventList) {
      actionEventElementList.add(new ActionEventElement(actionEvent));
    }
    Collections.sort(actionEventElementList);
    for (ActionEventElement element : actionEventElementList) {
      actionEventModel.addElement(element);
    }
    mainActionEventList.setModel(actionEventModel);
    mainActionEventList.setCellRenderer(new ActionEventElementListRenderer());
    if (actionEventElementList.size() == 1) {
      mainActionEventList.setSelectedIndex(0);
      addButtonActionPerformed();
    } else mainActionEventList.setSelectionInterval(0, actionEventModel.getSize() - 1);

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void addButtonActionPerformed() {
    List<ActionEventElement> elementList = mainActionEventList.getSelectedValuesList();
    if (elementList.isEmpty()) return;
    for (ActionEventElement element : elementList) {
      actionEventModel.removeElement(element);
    }
    ActionEventPriceItemPanel panel = new ActionEventPriceItemPanel(actionEventModel, elementList, categoryList);
    panel.addResizeListener(this);
    contentPanel.add(panel);
    needResize(new ResizeEvent(this, ResizeEvent.Dimension.BOTH));
  }

  private void mainActionEventListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) addButton.setEnabled(!mainActionEventList.isSelectionEmpty());
  }

  private void okButtonActionPerformed() {
    if (actionEventModel.getSize() > 0) {
      JOptionPane.showMessageDialog(this, "Необходимо установить значения для всех сеансов", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    for (int i = 1; i < contentPanel.getComponentCount(); i++) {
      ActionEventPriceItemPanel itemPanel = (ActionEventPriceItemPanel) contentPanel.getComponent(i);
      if (!itemPanel.checkData(this)) return;
    }
    ArrayList<ActionEventObj> actionEventList = new ArrayList<>();
    for (int i = 1; i < contentPanel.getComponentCount(); i++) {
      ActionEventPriceItemPanel itemPanel = (ActionEventPriceItemPanel) contentPanel.getComponent(i);
      actionEventList.addAll(itemPanel.getActionEventPriceList());
    }
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
    Env.net.create("ADD_ACTION_EVENT", new Request(actionEventList), this, true, 30000).start();
  }

  private void cancelButtonActionPerformed() {
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    contentPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JLabel label1 = new JLabel();
    mainActionEventList = new JList<>();
    addButton = new JButton();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u0446\u0435\u043d \u043d\u0430 \u0441\u0435\u0430\u043d\u0441\u044b...");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

        //======== mainPanel ========
        {
          mainPanel.setBorder(new CompoundBorder(
            UIManager.getBorder("TitledBorder.border"),
            new EmptyBorder(5, 5, 5, 5)));
          mainPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {0, 0};
          ((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
          ((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
          ((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

          //---- label1 ----
          label1.setText("\u0434\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u0441\u0435\u0430\u043d\u0441\u044b:");
          mainPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 1, 0), 0, 0));

          //---- mainActionEventList ----
          mainActionEventList.setBorder(new EtchedBorder());
          mainActionEventList.setToolTipText("\u0421 \u043f\u043e\u043c\u043e\u0449\u044c\u044e Shift \u0438\u043b\u0438 Ctrl \u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0430\u043d\u0441\u043e\u0432");
          mainActionEventList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
              mainActionEventListValueChanged(e);
            }
          });
          mainPanel.add(mainActionEventList, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 1, 0), 0, 0));

          //---- addButton ----
          addButton.setText("\u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u2192");
          addButton.setMargin(new Insets(2, 10, 2, 10));
          addButton.setEnabled(false);
          addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addButtonActionPerformed();
            }
          });
          mainPanel.add(addButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(mainPanel);
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
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void needResize(ResizeEvent event) {
    pack();
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
    owner.dispose();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.isDataSent())
      JOptionPane.showMessageDialog(this, "Команда отправлена на сервер, но подтверждение не было получено", "Ошибка", JOptionPane.ERROR_MESSAGE);
    else
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить сеансы", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }
}
