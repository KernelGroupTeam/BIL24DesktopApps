/*
 * Created by JFormDesigner on Wed Nov 18 15:14:08 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.editor.model.GenresTableModel;
import org.jdesktop.swingx.JXTable;

/**
 * @author Maksim
 */
public class GenresDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXTable genresTable;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final GenresTableModel genresTableModel;

  public GenresDialog(Window owner, Component parentComponent, GenresTableModel genresTableModel) {
    super(owner);
    this.genresTableModel = genresTableModel;
    initComponents();

    genresTable.setModel(genresTableModel);
    genresTable.getColumnExt(genresTableModel.getColumnName(1)).setVisible(false);
    genresTable.getColumnModel().getColumn(0).setMaxWidth(30);
    genresTable.packAll();

    pack();
    if (parentComponent != null) setLocationRelativeTo(parentComponent);
    else setLocationRelativeTo(getOwner());
  }

  private void tableKeyPressed(KeyEvent e) {//todo галочка нажимается
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      int[] rows = genresTable.getSelectedRows();
      int[] modelRows = new int[rows.length];
      for (int i = 0; i < rows.length; i++) {
        modelRows[i] = genresTable.convertRowIndexToModel(rows[i]);
      }
      genresTableModel.invertSelection(modelRows);
    }
  }

  private void closeButtonActionPerformed() {
    this.setVisible(false);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel buttonBar = new JPanel();
    JButton closeButton = new JButton();
    JScrollPane scrollPane1 = new JScrollPane();
    genresTable = new JXTable();

    //======== this ========
    setIconImages(Env.frameIcons);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0416\u0430\u043d\u0440\u044b");
    setType(Window.Type.UTILITY);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== buttonBar ========
      {
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        buttonBar.setLayout(new GridBagLayout());
        ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0};

        //---- closeButton ----
        closeButton.setText("\u0417\u0430\u043a\u0440\u044b\u0442\u044c");
        closeButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            closeButtonActionPerformed();
          }
        });
        buttonBar.add(closeButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);

      //======== scrollPane1 ========
      {

        //---- genresTable ----
        genresTable.addKeyListener(new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            tableKeyPressed(e);
          }
        });
        scrollPane1.setViewportView(genresTable);
      }
      dialogPane.add(scrollPane1, BorderLayout.CENTER);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
}
