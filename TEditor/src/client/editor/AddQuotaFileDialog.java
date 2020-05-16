/*
 * Created by JFormDesigner on Tue Nov 22 16:59:26 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.*;

import client.component.FileChooser;
import client.component.suggestion.SuggestionComboBox;
import client.editor.component.FileChoosers;
import org.jdesktop.swingx.JXDatePicker;
import org.jetbrains.annotations.Nullable;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class AddQuotaFileDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JTextField numberTextField;
  private JXDatePicker datePicker;
  private SuggestionComboBox<QuotaFormatObj> formatComboBox;
  private JTextField fileTextField;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static int lastFormatIndex = -1;
  private final long actionEventId;
  @Nullable
  private QuotaInFileObj quota = null;

  public AddQuotaFileDialog(Window owner, long actionEventId) {
    super(owner);
    this.actionEventId = actionEventId;
    initComponents();

    for (QuotaFormatObj quotaFormat : Env.quotaFormatList) {
      formatComboBox.addItem(quotaFormat);
    }
    formatComboBox.setSelectedIndex(lastFormatIndex);

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void openButtonActionPerformed() {
    FileChooser openQuotaDialog = FileChoosers.getOpenQuotaDialog();
    openQuotaDialog.resetChoosableFileFilters();
    QuotaFormatObj quotaFormat = formatComboBox.getItemAt(formatComboBox.getSelectedIndex());
    if (quotaFormat != null) {
      FileFilter fileFilter = new FileNameExtensionFilter(quotaFormat.getFileFilterDesc(), quotaFormat.getFileFilterExtensions());
      openQuotaDialog.addChoosableFileFilter(fileFilter);
      openQuotaDialog.setFileFilter(fileFilter);
    }
    if (openQuotaDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openQuotaDialog.getSelectedFile();
      fileTextField.setText(file.getAbsolutePath());
    }
  }

  private void okButtonActionPerformed() {
    lastFormatIndex = formatComboBox.getSelectedIndex();
    String number = numberTextField.getText().trim();
    if (number.isEmpty()) {
      numberTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Номер накладной не указан", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Date date = datePicker.getDate();
    if (date == null) {
      datePicker.requestFocus();
      JOptionPane.showMessageDialog(this, "Дата накладной не указана", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    QuotaFormatObj quotaFormat = formatComboBox.getItemAt(formatComboBox.getSelectedIndex());
    if (quotaFormat == null) {
      formatComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Формат файла квоты не выбран", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    File file = new File(fileTextField.getText());
    if (!file.exists()) {
      fileTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Файл квоты не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (file.length() > 5 * 1024 * 1024) {
      fileTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Файл квоты превышает максимальный размер 5 Мб", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      byte[] quotaData = Files.readAllBytes(file.toPath());
      quota = new QuotaInFileObj(actionEventId, number, date, quotaFormat, file.getName(), quotaData);
      this.dispose();
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Ошибка чтения файла квоты:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void cancelButtonActionPerformed() {
    quota = null;
    this.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JPanel panel1 = new JPanel();
    JLabel label1 = new JLabel();
    numberTextField = new JTextField();
    JLabel label2 = new JLabel();
    datePicker = new JXDatePicker();
    JLabel label6 = new JLabel();
    formatComboBox = new SuggestionComboBox<>();
    JLabel label7 = new JLabel();
    fileTextField = new JTextField();
    JButton openButton = new JButton();
    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043a\u0432\u043e\u0442\u0443 \u0438\u0437 \u0444\u0430\u0439\u043b\u0430...");
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 205, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

        //======== panel1 ========
        {
          panel1.setLayout(new GridBagLayout());
          ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
          ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label1 ----
          label1.setText("\u041d\u0430\u043a\u043b\u0430\u0434\u043d\u0430\u044f \u2116");
          panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          panel1.add(numberTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label2 ----
          label2.setText("\u043e\u0442");
          panel1.add(label2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- datePicker ----
          datePicker.setFormats("dd.MM.yyyy");
          panel1.add(datePicker, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(panel1, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label6 ----
        label6.setText("\u0424\u043e\u0440\u043c\u0430\u0442:");
        contentPanel.add(label6, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(formatComboBox, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label7 ----
        label7.setText("\u0424\u0430\u0439\u043b:");
        contentPanel.add(label7, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));
        contentPanel.add(fileTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- openButton ----
        openButton.setIcon(new ImageIcon(getClass().getResource("/resources/open.png")));
        openButton.setMargin(new Insets(0, 0, 1, 1));
        openButton.setToolTipText("\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u0444\u0430\u0439\u043b");
        openButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openButtonActionPerformed();
          }
        });
        contentPanel.add(openButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
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

  @Nullable
  public QuotaInFileObj getQuota() {
    return quota;
  }
}
