/*
 * Created by JFormDesigner on Wed Nov 18 15:14:08 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.editor.component.renderer.CategoryPriceListRenderer;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class ChCategoryDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel label;
  private JComboBox<CategoryPriceObj> catComboBox;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private CategoryPriceObj result = null;

  public ChCategoryDialog(Window owner, Component parentComponent, List<CategoryPriceObj> categoryPriceList, List<EventSeatObj> eventSeatList) {
    super(owner);
    initComponents();

    label.setText(label.getText() + "(" + eventSeatList.size() + ")");
    catComboBox.setRenderer(new CategoryPriceListRenderer());
    for (CategoryPriceObj categoryPrice : categoryPriceList) {
      catComboBox.addItem(categoryPrice);
    }

    pack();
    if (parentComponent != null) setLocationRelativeTo(parentComponent);
    else setLocationRelativeTo(getOwner());
  }

  private void okButtonActionPerformed() {
    result = catComboBox.getItemAt(catComboBox.getSelectedIndex());
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
    label = new JLabel();
    catComboBox = new JComboBox<>();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e...");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {300, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

        //---- label ----
        label.setText("\u0418\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e \u0432\u044b\u0434\u0435\u043b\u0435\u043d\u043d\u044b\u0445 \u043c\u0435\u0441\u0442 ");
        contentPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));
        contentPanel.add(catComboBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
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

  public CategoryPriceObj getResult() {
    return result;
  }
}
