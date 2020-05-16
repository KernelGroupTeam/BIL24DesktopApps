/*
 * Created by JFormDesigner on Fri Jul 24 01:12:54 MSK 2015
 */

package client.editor.component;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import client.editor.component.listener.*;
import client.editor.component.renderer.ActionEventElementListRenderer;
import client.formatter.PositiveNumberFormatter;
import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class ActionEventPriceItemPanel extends JPanel implements CanResizeComponent {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JList<ActionEventElement> actionEventList;
  private JPanel dataPanel;
  private JButton removeButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final DefaultListModel<ActionEventElement> parentActionEventModel;
  private final List<CategoryData> categoryDataList;
  private final DefaultListModel<ActionEventElement> actionEventModel = new DefaultListModel<>();

  public ActionEventPriceItemPanel(DefaultListModel<ActionEventElement> parentActionEventModel, List<ActionEventElement> elementList, List<CategoryObj> categoryList) {
    this.parentActionEventModel = parentActionEventModel;
    if (elementList.isEmpty()) throw new IllegalArgumentException("element list is empty");
    if (categoryList.isEmpty()) throw new IllegalArgumentException("category list is empty");
    initComponents();

    for (ActionEventElement element : elementList) {
      actionEventModel.addElement(element);
    }
    actionEventList.setModel(actionEventModel);
    actionEventList.setCellRenderer(new ActionEventElementListRenderer());

    categoryDataList = new ArrayList<>(categoryList.size());
    for (CategoryObj category : categoryList) {
      categoryDataList.add(new CategoryData(category));
    }
    initCategories();
  }

  private void actionEventListValueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) removeButton.setEnabled(!actionEventList.isSelectionEmpty());
  }

  private void removeButtonActionPerformed() {
    List<ActionEventElement> elementList = actionEventList.getSelectedValuesList();
    if (elementList.isEmpty()) return;
    for (ActionEventElement element : elementList) {
      actionEventModel.removeElement(element);
      parentActionEventModel.addElement(element);
    }
    ResizeEvent resizeEvent;
    if (actionEventList.getModel().getSize() == 0) {
      resizeEvent = new ResizeEvent(this, ResizeEvent.Dimension.BOTH);
      Container parent = getParent();
      if (parent != null) {
        parent.remove(this);
        parent.revalidate();
      }
    } else {
      resizeEvent = new ResizeEvent(this, ResizeEvent.Dimension.HEIGHT);
    }
    ResizeListener[] listeners = listenerList.getListeners(ResizeListener.class);
    for (ResizeListener listener : listeners) {
      listener.needResize(resizeEvent);
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    actionEventList = new JList<>();
    dataPanel = new JPanel();
    JLabel label3 = new JLabel();
    JTextField textField1 = new JTextField();
    JLabel label4 = new JLabel();
    JLabel label5 = new JLabel();
    JLabel label6 = new JLabel();
    removeButton = new JButton();

    //======== this ========
    setBorder(new CompoundBorder(
      new CompoundBorder(
        new EmptyBorder(0, 5, 0, 0),
        UIManager.getBorder("TitledBorder.border")),
      new EmptyBorder(5, 5, 5, 5)));
    setLayout(new GridBagLayout());
    ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0};
    ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0, 0};
    ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
    ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

    //---- label1 ----
    label1.setText("\u0441\u0435\u0430\u043d\u0441\u044b:");
    add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 1, 5), 0, 0));

    //---- label2 ----
    label2.setText("\u0446\u0435\u043d\u044b \u043f\u043e \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044f\u043c:");
    add(label2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 1, 0), 0, 0));

    //---- actionEventList ----
    actionEventList.setBorder(new EtchedBorder());
    actionEventList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        actionEventListValueChanged(e);
      }
    });
    add(actionEventList, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 1, 5), 0, 0));

    //======== dataPanel ========
    {
      dataPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)dataPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)dataPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
      ((GridBagLayout)dataPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
      ((GridBagLayout)dataPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

      //---- label3 ----
      label3.setText("Classic:");
      dataPanel.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- textField1 ----
      textField1.setColumns(5);
      dataPanel.add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- label4 ----
      label4.setText("\u0440\u0443\u0431.");
      dataPanel.add(label4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- label5 ----
      label5.setText("\u041c\u0435\u0441\u0442: 50");
      dataPanel.add(label5, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- label6 ----
      label6.setText("\u0412\u0441\u0435\u0433\u043e \u043c\u0435\u0441\u0442: 50");
      dataPanel.add(label6, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    add(dataPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 1, 0), 0, 0));

    //---- removeButton ----
    removeButton.setText("\u2190 \u0443\u0431\u0440\u0430\u0442\u044c");
    removeButton.setMargin(new Insets(2, 10, 2, 10));
    removeButton.setEnabled(false);
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeButtonActionPerformed();
      }
    });
    add(removeButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
      new Insets(0, 0, 0, 5), 0, 0));
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void initCategories() {
    dataPanel.removeAll();
    dataPanel.setLayout(new GridBagLayout());

    int[] rowHeights = new int[categoryDataList.size() + 2];
    Arrays.fill(rowHeights, 0);
    double[] rowWeights = new double[categoryDataList.size() + 2];
    Arrays.fill(rowWeights, 0.0);
    rowWeights[rowWeights.length - 1] = 1.0E-4;
    ((GridBagLayout) dataPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
    ((GridBagLayout) dataPanel.getLayout()).rowHeights = rowHeights;
    ((GridBagLayout) dataPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};
    ((GridBagLayout) dataPanel.getLayout()).rowWeights = rowWeights;

    int totalSeats = 0;
    for (int i = 0; i < categoryDataList.size(); i++) {
      CategoryData data = categoryDataList.get(i);
      dataPanel.add(data.getNameLabel(), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
      dataPanel.add(data.getSumTextField(), new GridBagConstraints(1, i, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
      dataPanel.add(new JLabel("руб."), new GridBagConstraints(2, i, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
      dataPanel.add(data.getNumLabel(), new GridBagConstraints(3, i, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
      totalSeats += data.getCategory().getSeatsNumber();
    }
    dataPanel.add(new JLabel("Всего мест: " + totalSeats), new GridBagConstraints(0, categoryDataList.size(), 4, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }

  public boolean checkData(Component showErrorComponent) {
    for (CategoryData data : categoryDataList) {
      BigDecimal sum;
      try {
        data.getSumTextField().commitEdit();
        sum = (BigDecimal) data.getSumTextField().getValue();
      } catch (ParseException e) {
        sum = null;
      }
      if (sum == null) {
        data.getSumTextField().requestFocus();
        JOptionPane.showMessageDialog(showErrorComponent, "Стоимость не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
    }
    return true;
  }

  //можно вызывать только если checkData вернет true
  public List<ActionEventObj> getActionEventPriceList() {
    ArrayList<CategoryPriceObj> priceList = new ArrayList<>(categoryDataList.size());
    for (CategoryData data : categoryDataList) {
      CategoryPriceObj categoryPrice = new CategoryPriceObj(data.getCategory().getId());
      categoryPrice.setPrice((BigDecimal) data.getSumTextField().getValue());
      priceList.add(categoryPrice);
    }

    ArrayList<ActionEventObj> result = new ArrayList<>(actionEventModel.getSize());
    for (int i = 0; i < actionEventModel.getSize(); i++) {
      ActionEventObj actionEvent = actionEventModel.get(i).getActionEvent();
      actionEvent.setPriceList(priceList);
      result.add(actionEvent);
    }
    return result;
  }

  @Override
  public void addResizeListener(ResizeListener l) {
    listenerList.add(ResizeListener.class, l);
  }

  @Override
  public void removeResizeListener(ResizeListener l) {
    listenerList.remove(ResizeListener.class, l);
  }

  @Override
  public ResizeListener[] getResizeListeners() {
    return listenerList.getListeners(ResizeListener.class);
  }

  private static class CategoryData {
    @NotNull
    private final CategoryObj category;
    @NotNull
    private final JLabel nameLabel;
    @NotNull
    private final JFormattedTextField sumTextField;
    @NotNull
    private final JLabel numLabel;

    public CategoryData(@NotNull CategoryObj category) {
      this.category = category;
      nameLabel = new JLabel(category.getName() + ":");
      sumTextField = new JFormattedTextField(new PositiveNumberFormatter());
      sumTextField.setColumns(5);
      BigDecimal initPrice = category.getInitPrice();
      if (initPrice != null) sumTextField.setValue(initPrice);
      sumTextField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          sumTextField.transferFocus();
        }
      });
      numLabel = new JLabel("Мест: " + category.getSeatsNumber());
    }

    @NotNull
    public CategoryObj getCategory() {
      return category;
    }

    @NotNull
    public JLabel getNameLabel() {
      return nameLabel;
    }

    @NotNull
    public JFormattedTextField getSumTextField() {
      return sumTextField;
    }

    @NotNull
    public JLabel getNumLabel() {
      return numLabel;
    }
  }
}
