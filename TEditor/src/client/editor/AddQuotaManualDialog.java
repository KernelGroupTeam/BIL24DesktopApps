/*
 * Created by JFormDesigner on Tue Nov 22 16:59:26 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.swing.text.*;

import client.component.ScrollOptionPane;
import client.component.suggestion.SuggestionComboBox;
import client.editor.model.*;
import client.formatter.*;
import client.utils.*;
import client.utils.Position;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.renderer.*;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jetbrains.annotations.*;
import server.protocol2.common.BarcodeFormat;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class AddQuotaManualDialog extends JDialog {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JTextField numberTextField;
  private JXDatePicker datePicker;
  private JFormattedTextField totalQtyFormattedTextField;
  private JFormattedTextField totalSumFormattedTextField;
  private SuggestionComboBox<BarcodeFormat> formatComboBox;
  private JXTable quotaTable;
  private JCheckBox unknownCheckBox;
  private JCheckBox completeCheckBox;
  private JButton okButton;
  private JPopupMenu popupMenu;
  private JMenuItem removeMenuItem;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final NumberFormat priceFormat = NumberFormat.getInstance(new Locale("ru", "RU"));
  private static final String okButtonText1 = "Продолжить...";
  private static final String okButtonText2 = "OK";
  private final QuotaTableModel quotaTableModel = new QuotaTableModel();
  private final EventSeatTableModel tableModel;
  @Nullable
  private final Long actionEventId;
  @NotNull
  private final Set<Long> seatIdSet;
  @Nullable
  private QuotaInManualObj quota;

  public AddQuotaManualDialog(Window owner, @NotNull EventSeatTableModel tableModel, long actionEventId, @NotNull Set<Long> seatIdSet) {//создание накладной и первого блока мест
    this(owner, tableModel, actionEventId, seatIdSet, null);
  }

  public AddQuotaManualDialog(Window owner, @NotNull EventSeatTableModel tableModel, @NotNull Set<Long> seatIdSet, @NotNull QuotaInManualObj quota) {//добавление мест в накладную
    this(owner, tableModel, null, seatIdSet, quota);
  }

  private AddQuotaManualDialog(Window owner, @NotNull EventSeatTableModel tableModel, @Nullable Long actionEventId,
                               @NotNull Set<Long> seatIdSet, @Nullable QuotaInManualObj quota) {
    super(owner);
    this.tableModel = tableModel;
    this.actionEventId = actionEventId;
    this.seatIdSet = seatIdSet;
    this.quota = quota;
    initComponents();

    for (BarcodeFormat barcodeFormat : Env.barcodeFormatList) {
      formatComboBox.addItem(barcodeFormat);
    }
    okButton.setText(okButtonText1);
    Integer quantity = seatIdSet.size();
    if (quota != null) {
      numberTextField.setText(quota.getNumber());
      numberTextField.setEditable(false);
      datePicker.setDate(quota.getTempDate());
      datePicker.setEnabled(false);
      unknownCheckBox.setSelected(quota.getTotalQty() == null);//должно быть перед total*Field !!!
      unknownCheckBox.setEnabled(false);
      totalQtyFormattedTextField.setValue(quota.getTotalQty());
      totalQtyFormattedTextField.setEditable(false);
      totalSumFormattedTextField.setValue(quota.getTotalSum());
      totalSumFormattedTextField.setEditable(false);
      formatComboBox.setSelectedItem(quota.getFormat());
      formatComboBox.setEnabled(false);
      if (quota.isInputComplete()) {
        quantity = null;
        completeCheckBox.setSelected(true);
        completeCheckBox.setEnabled(false);
      } else this.setTitle("Добавить места в квоту...");
    } else if (actionEventId != null) formatComboBox.setSelectedIndex(-1);

    quotaTableModel.setData(quota, quantity);
    quotaTableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        quotaTableChanged();
      }
    });
    quotaTable.setModel(quotaTableModel);
    NumberFormatter initPriceEditorFormatter = new InsensitiveDSNumberFormatter(true);
    initPriceEditorFormatter.setValueClass(BigDecimal.class);
    initPriceEditorFormatter.setMinimum(BigDecimal.ZERO);
    NumberEditorExt priceEditor = new NumberEditorExt();
    priceEditor.setClickCountToStart(1);
    JFormattedTextField priceEditorTextField = (JFormattedTextField) priceEditor.getComponent();
    priceEditorTextField.setFormatterFactory(new DefaultFormatterFactory(initPriceEditorFormatter));
    quotaTable.setDefaultEditor(BigDecimal.class, priceEditor);
    JComboBox<CategoryPriceObj> categoryPriceEditor = new JComboBox<>();
    categoryPriceEditor.addItem(QuotaTableModel.defCategoryPrice);
    for (CategoryPriceObj categoryPrice : tableModel.getCategoryPriceList()) {
      categoryPriceEditor.addItem(categoryPrice);
    }
    quotaTable.setDefaultEditor(CategoryPriceObj.class, new DefaultCellEditor(categoryPriceEditor));
    DefaultTableRenderer categoryPriceRenderer = new DefaultTableRenderer(new StringValue() {
      @Override
      public String getString(Object value) {
        if (value == null) return QuotaTableModel.defCategoryPrice.getName();
        if (value instanceof CategoryPriceObj) return ((CategoryPriceObj) value).getName();
        return value.toString();
      }
    });
    quotaTable.setDefaultRenderer(CategoryPriceObj.class, categoryPriceRenderer);
    quotaTable.getColumnModel().getColumn(3).setMinWidth(100);
    quotaTable.addHighlighter(new ColorHighlighter(new QuotaPredicate1(), new Color(240, 240, 224), null));
    quotaTable.addHighlighter(new ColorHighlighter(new QuotaPredicate2(), null, Color.LIGHT_GRAY));

    pack();
    setLocationRelativeTo(getOwner());

    quotaTable.scrollRowToVisible(quotaTable.getRowCount() - 1);
    if (quota != null && !quotaTableModel.isInputComplete()) {
      TableUtils.editCellAtModel(quotaTable, quotaTableModel.getAddingRow(), 1);
    }
  }

  @SuppressWarnings("RedundantIfStatement")
  private boolean getCompleteSelected() {
    if (unknownCheckBox.isSelected()) return false;
    Integer totalQty = (Integer) totalQtyFormattedTextField.getValue();
    if (totalQty == null) return false;
    BigDecimal totalSum = (BigDecimal) totalSumFormattedTextField.getValue();
    if (totalSum == null) return false;
    if (!quotaTableModel.isInputComplete()) return false;
    if (!totalQty.equals(quotaTableModel.getCompleteTotalQty())) return false;
    BigDecimal tableTotalSum = quotaTableModel.getCompleteTotalSum();
    if (tableTotalSum == null) return false;
    if (totalSum.compareTo(tableTotalSum) != 0) return false;
    return true;
  }

  @SuppressWarnings("RedundantIfStatement")
  private boolean getCompleteEnabled() {
    if (!unknownCheckBox.isSelected()) return false;
    if (!quotaTableModel.isInputComplete()) return false;
    return true;
  }

  private void totalPropertyChange(PropertyChangeEvent e) {
    if (Objects.equals(e.getOldValue(), e.getNewValue())) return;
    completeCheckBox.setSelected(getCompleteSelected());
    completeCheckBox.setEnabled(getCompleteEnabled());
  }

  private void quotaTableChanged() {
    completeCheckBox.setSelected(getCompleteSelected());
    completeCheckBox.setEnabled(getCompleteEnabled());
  }

  private void unknownCheckBoxItemStateChanged(ItemEvent e) {
    boolean unknown = (e.getStateChange() == ItemEvent.SELECTED);
    totalQtyFormattedTextField.setEditable(!unknown);
    totalSumFormattedTextField.setEditable(!unknown);
    completeCheckBox.setSelected(getCompleteSelected());
    completeCheckBox.setEnabled(getCompleteEnabled());
  }

  private void completeCheckBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      okButton.setText(okButtonText2);
    } else {
      okButton.setText(okButtonText1);
    }
  }

  private void quotaTableMousePopup(MouseEvent e) {
    JXTable table = (JXTable) e.getSource();
    Point point = new Point(e.getX(), e.getY());
    int activeRow = table.rowAtPoint(point);
    int activeCol = table.columnAtPoint(point);
    if (activeRow == -1 || activeCol == -1) return;
    if (e.isPopupTrigger()) {
      table.changeSelection(activeRow, activeCol, false, false);
      int modelRow = table.convertRowIndexToModel(activeRow);
      if (modelRow == table.getModel().getRowCount() - 1) return;
      if (quota != null && quota.isInputComplete()) {//ввод завершен, quantity == null в tableModel
        removeMenuItem.setEnabled(false);
      } else {//режим добавления новой строки
        removeMenuItem.setEnabled(modelRow < table.getModel().getRowCount() - 2);
      }
      popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private void showMenuItemActionPerformed() {
    int activeRow = quotaTable.getSelectedRow();
    int modelRow = quotaTable.convertRowIndexToModel(activeRow);
    Collection<Long> seatIdCollection = null;
    QuotaInManualObj.Data data = null;
    if (modelRow < quotaTableModel.getRowCount() - 2 || (quota != null && quota.isInputComplete())) {
      data = quotaTableModel.getData(modelRow);
      if (data == null) return;
      seatIdCollection = data.getSeatIdList();
    } else if (modelRow == quotaTableModel.getRowCount() - 2) {
      seatIdCollection = seatIdSet;
    }
    if (seatIdCollection == null) return;
    List<SeatLocation> seatLocationList = tableModel.getSeatLocationList(seatIdCollection);
    StringBuilder str = new StringBuilder();
    str.append(L10n.pluralVal(seatLocationList.size(), "место", "места", "мест"));
    if (data == null) str.append(":\n");
    else {
      str.append(" по ").append(priceFormat.format(data.getPrice())).append(" руб. на ");
      str.append(priceFormat.format(data.getSum())).append(" руб.:\n");
    }
    for (SeatLocation seatLocation : seatLocationList) {
      str.append(seatLocation).append("\n");
    }
    ScrollOptionPane.showMessageDialog(this, str.toString(), "Список мест", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.RIGHT));
  }

  private void removeMenuItemActionPerformed() {
    if (quota == null) return;
    int activeRow = quotaTable.getSelectedRow();
    int modelRow = quotaTable.convertRowIndexToModel(activeRow);
    QuotaInManualObj.Data data = quotaTableModel.getData(modelRow);
    if (data == null) return;
    StringBuilder confirm = new StringBuilder();
    confirm.append("Удалить строку с ").append(L10n.pluralVal(data.getQuantity(), "местом", "местами", "местами")).append(" на сумму ");
    confirm.append(priceFormat.format(data.getSum())).append(" руб.?");
    if (JOptionPane.showConfirmDialog(this, confirm.toString(), "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      quota.removeData(data);
      quotaTableModel.setData(quota, seatIdSet.size(), quotaTableModel.getPrice(), quotaTableModel.getPrefCategory());
    }
  }

  private void okButtonActionPerformed() {
    if (quotaTable.isEditing()) return;
    if (actionEventId != null && quota == null) {
      boolean unknownCheckSum = unknownCheckBox.isSelected();
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
      Integer totalQty;
      try {
        totalQtyFormattedTextField.commitEdit();
        totalQty = (Integer) totalQtyFormattedTextField.getValue();
      } catch (ParseException e) {
        totalQty = null;
      }
      if (totalQty == null && !unknownCheckSum) {
        totalQtyFormattedTextField.requestFocus();
        JOptionPane.showMessageDialog(this, "Общее количество мест не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      BigDecimal totalSum;
      try {
        totalSumFormattedTextField.commitEdit();
        totalSum = (BigDecimal) totalSumFormattedTextField.getValue();
      } catch (ParseException e) {
        totalSum = null;
      }
      if (totalSum == null && !unknownCheckSum) {
        totalSumFormattedTextField.requestFocus();
        JOptionPane.showMessageDialog(this, "Общая сумма мест не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      BarcodeFormat barcodeFormat = formatComboBox.getItemAt(formatComboBox.getSelectedIndex());
      if (barcodeFormat == null) {
        formatComboBox.requestFocus();
        JOptionPane.showMessageDialog(this, "Формат штрих-кода не выбран", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (!quotaTableModel.isInputComplete()) {
        JOptionPane.showMessageDialog(this, "Цена места не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
        TableUtils.editCellAtModel(quotaTable, quotaTableModel.getAddingRow(), 1);
        return;
      }
      if (unknownCheckSum) {
        quota = new QuotaInManualObj(actionEventId, number, date, barcodeFormat);
      } else {
        quota = new QuotaInManualObj(actionEventId, number, date, barcodeFormat, totalQty, totalSum);
      }
    }
    if (quota == null) throw new IllegalStateException();

    if (!quotaTableModel.isInputComplete()) {
      JOptionPane.showMessageDialog(this, "Цена места не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
      TableUtils.editCellAtModel(quotaTable, quotaTableModel.getAddingRow(), 1);
      return;
    }
    if (!quota.isInputComplete()) {
      BigDecimal price = quotaTableModel.getPrice();
      if (price == null) throw new IllegalStateException();
      CategoryPriceObj prefCategory = quotaTableModel.getPrefCategory();
      quota.addData(seatIdSet, price, prefCategory);
      if (completeCheckBox.isSelected()) quota.setInputComplete();
    }
    this.dispose();
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
    JPanel panel2 = new JPanel();
    JLabel label3 = new JLabel();
    totalQtyFormattedTextField = new JFormattedTextField(new PositiveIntegerFormatter());
    JLabel label4 = new JLabel();
    totalSumFormattedTextField = new JFormattedTextField(new PositiveNumberFormatter());
    JLabel label5 = new JLabel();
    JPanel panel3 = new JPanel();
    JLabel label6 = new JLabel();
    formatComboBox = new SuggestionComboBox<>();
    JLabel label7 = new JLabel();
    JScrollPane scrollPane = new JScrollPane();
    quotaTable = new JXTable();
    JPanel buttonBar = new JPanel();
    JPanel panel4 = new JPanel();
    unknownCheckBox = new JCheckBox();
    completeCheckBox = new JCheckBox();
    okButton = new JButton();
    JButton cancelButton = new JButton();
    popupMenu = new JPopupMenu();
    JMenuItem showMenuItem = new JMenuItem();
    removeMenuItem = new JMenuItem();

    //======== this ========
    setIconImages(Env.frameIcons);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043a\u0432\u043e\u0442\u0443 \u0432\u0440\u0443\u0447\u043d\u0443\u044e...");
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
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

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
        contentPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== panel2 ========
        {
          panel2.setLayout(new GridBagLayout());
          ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
          ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4};
          ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label3 ----
          label3.setText("\u041e\u0431\u0449\u0435\u0435 \u043a\u043e\u043b-\u0432\u043e \u043c\u0435\u0441\u0442:");
          panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- totalQtyFormattedTextField ----
          totalQtyFormattedTextField.setColumns(4);
          totalQtyFormattedTextField.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
              totalPropertyChange(e);
            }
          });
          panel2.add(totalQtyFormattedTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label4 ----
          label4.setText("\u041e\u0431\u0449\u0430\u044f \u0441\u0443\u043c\u043c\u0430:");
          panel2.add(label4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- totalSumFormattedTextField ----
          totalSumFormattedTextField.setColumns(6);
          totalSumFormattedTextField.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
              totalPropertyChange(e);
            }
          });
          panel2.add(totalSumFormattedTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label5 ----
          label5.setText("\u0440\u0443\u0431.");
          panel2.add(label5, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(panel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
          panel3.setLayout(new GridBagLayout());
          ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
          ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label6 ----
          label6.setText("\u0424\u043e\u0440\u043c\u0430\u0442 \u0428\u041a:");
          panel3.add(label6, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          panel3.add(formatComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(panel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label7 ----
        label7.setText("\u0421\u043e\u0434\u0435\u0440\u0436\u0438\u043c\u043e\u0435 \u043a\u0432\u043e\u0442\u044b:");
        label7.setBorder(new EmptyBorder(5, 0, 0, 0));
        contentPanel.add(label7, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane ========
        {
          scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
          scrollPane.setMinimumSize(new Dimension(100, 200));
          scrollPane.setPreferredSize(new Dimension(100, 200));

          //---- quotaTable ----
          quotaTable.setAutoCreateRowSorter(false);
          quotaTable.setSortable(false);
          quotaTable.setRowSelectionAllowed(false);
          quotaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              quotaTableMousePopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
              quotaTableMousePopup(e);
            }
          });
          scrollPane.setViewportView(quotaTable);
        }
        contentPanel.add(scrollPane, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
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

        //======== panel4 ========
        {
          panel4.setLayout(new GridBagLayout());
          ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0};
          ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0};
          ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
          ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

          //---- unknownCheckBox ----
          unknownCheckBox.setText("\u0418\u0442\u043e\u0433 \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u0435\u043d");
          unknownCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              unknownCheckBoxItemStateChanged(e);
            }
          });
          panel4.add(unknownCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

          //---- completeCheckBox ----
          completeCheckBox.setText("\u0412\u0432\u043e\u0434 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d");
          completeCheckBox.setEnabled(false);
          completeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              completeCheckBoxItemStateChanged(e);
            }
          });
          panel4.add(completeCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        buttonBar.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- okButton ----
        okButton.setText("\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c...");
        okButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            okButtonActionPerformed();
          }
        });
        buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
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
          GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);

    //======== popupMenu ========
    {

      //---- showMenuItem ----
      showMenuItem.setText("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043c\u0435\u0441\u0442\u0430");
      showMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          showMenuItemActionPerformed();
        }
      });
      popupMenu.add(showMenuItem);

      //---- removeMenuItem ----
      removeMenuItem.setText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u0443");
      removeMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          removeMenuItemActionPerformed();
        }
      });
      popupMenu.add(removeMenuItem);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Nullable
  public QuotaInManualObj getQuota() {
    return quota;
  }

  private static class QuotaPredicate1 implements HighlightPredicate {
    @Override
    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
      JTable table = (JTable) adapter.getComponent();
      int row = table.convertRowIndexToModel(adapter.row);
      TableModel model = table.getModel();
      int addingRow = model.getRowCount() - 2;
      return (row == addingRow && model.getValueAt(addingRow, 1) == null);
    }
  }

  private static class QuotaPredicate2 implements HighlightPredicate {
    @Override
    public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
      JTable table = (JTable) adapter.getComponent();
      int row = table.convertRowIndexToModel(adapter.row);
      TableModel model = table.getModel();
      int addingRow = model.getRowCount() - 2;
      return (row >= addingRow && model.getValueAt(addingRow, 1) == null);
    }
  }
}
