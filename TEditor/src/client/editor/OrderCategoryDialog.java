/*
 * Created by JFormDesigner on Tue Feb 09 12:55:07 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import client.editor.component.listener.ReorderListListener;
import client.editor.component.renderer.CategoryPriceListRenderer;
import org.jetbrains.annotations.Nullable;
import server.protocol2.editor.CategoryPriceObj;

/**
 * @author Maksim
 */
public class OrderCategoryDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JList<CategoryPriceObj> catList;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final DefaultListModel<CategoryPriceObj> listModel = new DefaultListModel<>();
  private final List<CategoryPriceObj> categoryPriceList;
  @Nullable
  private List<Long> result = null;

  public OrderCategoryDialog(Window owner, Component parentComponent, List<CategoryPriceObj> categoryPriceList) {
    super(owner);
    this.categoryPriceList = Collections.unmodifiableList(categoryPriceList);
    initComponents();

    catList.setCellRenderer(new CategoryPriceListRenderer());
    catList.setModel(listModel);
    MouseAdapter listener = new ReorderListListener<>(catList);
    catList.addMouseListener(listener);
    catList.addMouseMotionListener(listener);
    resetButtonActionPerformed();

    pack();
    if (parentComponent != null) setLocationRelativeTo(parentComponent);
    else setLocationRelativeTo(getOwner());
  }

  private void resetButtonActionPerformed() {
    listModel.clear();
    for (CategoryPriceObj categoryPriceObj : categoryPriceList) {
      listModel.addElement(categoryPriceObj);
    }
  }

  private void sort1ButtonActionPerformed() {
    listModel.clear();
    List<CategoryPriceObj> list = createSortedList();
    for (CategoryPriceObj categoryPriceObj : list) {
      listModel.addElement(categoryPriceObj);
    }
  }

  private void sort2ButtonActionPerformed() {
    listModel.clear();
    List<CategoryPriceObj> list = createSortedList();
    for (int i = list.size() - 1; i >= 0; i--) {
      CategoryPriceObj categoryPriceObj = list.get(i);
      listModel.addElement(categoryPriceObj);
    }
  }

  private void okButtonActionPerformed() {
    result = new ArrayList<>(listModel.size());
    Enumeration<CategoryPriceObj> enumeration = listModel.elements();
    while (enumeration.hasMoreElements()) result.add(enumeration.nextElement().getId());
    this.dispose();
  }

  private void cancelButtonActionPerformed() {
    result = null;
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    catList = new JList<>();
    JButton resetButton = new JButton();
    JButton sort1Button = new JButton();
    JButton sort2Button = new JButton();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u041f\u043e\u0440\u044f\u0434\u043e\u043a \u0446\u0435\u043d");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u041f\u043e\u0440\u044f\u0434\u043e\u043a \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0439:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));

        //---- catList ----
        catList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catList.setBorder(new EtchedBorder());
        catList.setToolTipText("\u0421 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u043f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u044f \u043c\u043e\u0436\u043d\u043e \u0443\u043f\u043e\u0440\u044f\u0434\u043e\u0447\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438");
        contentPanel.add(catList, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- resetButton ----
        resetButton.setText("\u0441\u0431\u0440\u043e\u0441");
        resetButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            resetButtonActionPerformed();
          }
        });
        contentPanel.add(resetButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- sort1Button ----
        sort1Button.setText("\u0446\u0435\u043d\u0430 \u2193");
        sort1Button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            sort1ButtonActionPerformed();
          }
        });
        contentPanel.add(sort1Button, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- sort2Button ----
        sort2Button.setText("\u0446\u0435\u043d\u0430 \u2191");
        sort2Button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            sort2ButtonActionPerformed();
          }
        });
        contentPanel.add(sort2Button, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new GridBagLayout());
        ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
        ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

        //---- okButton ----
        okButton.setText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c");
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

  private List<CategoryPriceObj> createSortedList() {
    List<CategoryPriceObj> list = new ArrayList<>(categoryPriceList);
    Collections.sort(list, new Comparator<CategoryPriceObj>() {
      @Override
      public int compare(CategoryPriceObj o1, CategoryPriceObj o2) {
        return o1.getPrice().compareTo(o2.getPrice());
      }
    });
    return list;
  }

  @Nullable
  public List<Long> getResult() {
    return result;
  }
}
