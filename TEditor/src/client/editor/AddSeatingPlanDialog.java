/*
 * Created by JFormDesigner on Tue Aug 25 14:18:50 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;

import client.component.*;
import client.component.filter.IdFileFilter;
import client.component.suggestion.SuggestionComboBox;
import client.editor.component.FileChoosers;
import client.editor.model.*;
import client.editor.splsvg.SplToSvg;
import client.formatter.*;
import client.net.*;
import client.utils.*;
import client.utils.Position;
import common.svg.*;
import eventim.spl.managers.EventimManager;
import eventim.spl.models.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.editor.*;

/**
 * @author Maksim
 */
public class AddSeatingPlanDialog extends JDialog implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private SuggestionComboBox<VenueObj> venueIntComboBox;
  private JTextField planTextField;
  private JRadioButton radioButton1;
  private JRadioButton radioButton2;
  private JRadioButton radioButton3;
  private JPanel placementPanel;
  private JXHyperlink fileHyperlink;
  private JXHyperlink addSplFileHyperlink;
  private JCheckBox selImageCheckBox;
  private JPanel svgPanel;
  private JPanel nonPlacementPanel;
  private JXHyperlink splNplFileHyperlink;
  private JScrollPane catScrollPane;
  private JXTable catTable;
  private JButton addButton;
  private JButton removeButton;
  private JButton addLimitButton;
  private JButton okButton;
  private JXBusyLabel busyLabel;
  private JLabel svgLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final L10n.PluralForm seatsForm = new L10n.PluralForm("место", "места", "мест");
  private final ChildlessControl childlessControl;
  private final OperationComboBox<SeatingPlanObj> planComboBox;
  private final AddCatTableModel catTableModel = new AddCatTableModel();
  private final Rectangle gcBounds;
  private WaitingDialog waitingDialog;
  private SVGLoader loader;
  private SVGResult svgResult = null;
  private byte[] nplsplData;

  public AddSeatingPlanDialog(Window owner, ChildlessControl childlessControl, OperationComboBox<SeatingPlanObj> planComboBox, OperationComboBox<VenueObj> venueComboBox) {
    super(owner);
    this.childlessControl = childlessControl;
    this.planComboBox = planComboBox;
    initComponents();

    for (int i = 0; i < venueComboBox.getItemCount(); i++) {
      venueIntComboBox.addItem(venueComboBox.getItemAt(i));
    }
    venueIntComboBox.setSelectedIndex(venueComboBox.getSelectedIndex());

    //обзательно до установки модели в таблицу, чтобы вызывалось после обновления таблицы
    catTableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        TableUtils.updateRowHeights(catTable);
      }
    });
    catTable.setRowHeightEnabled(true);//индивидуальная высота строк
    catTable.setModel(catTableModel);
    NumberFormatter initPriceEditorFormatter = new InsensitiveDSNumberFormatter(true);
    initPriceEditorFormatter.setValueClass(BigDecimal.class);
    initPriceEditorFormatter.setMinimum(BigDecimal.ZERO);
    NumberEditorExt initPriceEditor = new NumberEditorExt();
    initPriceEditor.setClickCountToStart(1);
    JFormattedTextField initPriceEditorTextField = (JFormattedTextField) initPriceEditor.getComponent();
    initPriceEditorTextField.setFormatterFactory(new DefaultFormatterFactory(initPriceEditorFormatter));
    catTable.setDefaultEditor(BigDecimal.class, initPriceEditor);

    PositiveIntegerFormatter seatsNumberEditorFormatter = new PositiveIntegerFormatter(Env.MAX_CAT_NUMBER_SEATS);
    seatsNumberEditorFormatter.setNullableValue(false);
    NumberEditorExt seatsNumberEditor = new NumberEditorExt();
    seatsNumberEditor.setClickCountToStart(1);
    JFormattedTextField seatsNumberEditorTextField = (JFormattedTextField) seatsNumberEditor.getComponent();
    seatsNumberEditorTextField.setFormatterFactory(new DefaultFormatterFactory(seatsNumberEditorFormatter));
    catTable.setDefaultEditor(Integer.class, seatsNumberEditor);

    JXTable.GenericEditor nameEditor = new JXTable.GenericEditor();
    nameEditor.setClickCountToStart(1);
    catTable.setDefaultEditor(String.class, nameEditor);

    catTable.addHighlighter(new ColorHighlighter(new AddCatTableLimitPredicate(catTableModel), new Color(221, 221, 221), Color.BLACK, new Color(150, 173, 195), Color.BLACK));

    catTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) catTableSelectionChanged();
      }
    });
    catTable.getColumn(0).setMinWidth(30);
    catTable.getColumn(0).setMaxWidth(30);
    catTable.getColumn(2).setMinWidth(60);
    catTable.getColumn(2).setMaxWidth(60);
    catTable.getColumn(3).setMinWidth(120);
    catTable.getColumn(3).setMaxWidth(120);
    catTable.getTableHeader().setReorderingAllowed(false);

    gcBounds = owner.getGraphicsConfiguration().getBounds();

    pack();
    setLocationRelativeTo(getOwner());
    catTable.packAll();
    nonPlacementPanel.setVisible(false);
  }

  private void radioButton1ItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      placementPanel.setVisible(false);
      nonPlacementPanel.setVisible(true);
      splNplFileHyperlink.setVisible(true);
      catScrollPane.setPreferredSize(new Dimension(100, 294));
      catScrollPane.revalidate();
      pack();
    }
  }

  private void radioButton2ItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      placementPanel.setVisible(true);
      nonPlacementPanel.setVisible(false);
      pack();
    }
  }

  private void radioButton3ItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      placementPanel.setVisible(true);
      nonPlacementPanel.setVisible(true);
      splNplFileHyperlink.setVisible(false);
      catScrollPane.setPreferredSize(new Dimension(100, 132));
      catScrollPane.revalidate();
      pack();
    }
  }

  private void catTableSelectionChanged() {
    int[] rows = catTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = catTable.convertRowIndexToModel(rows[i]);
    }
    addButton.setEnabled(catTableModel.isCanAddCat());
    removeButton.setEnabled(catTableModel.isCanRemove(modelRows));
    addLimitButton.setEnabled(catTableModel.isCanAddLimit(modelRows));
  }

  private void addButtonActionPerformed() {
    catTableModel.addCat();
  }

  private void removeButtonActionPerformed() {
    int[] rows = catTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = catTable.convertRowIndexToModel(rows[i]);
    }
    catTableModel.remove(modelRows);
  }

  private void addLimitButtonActionPerformed() {
    int[] rows = catTable.getSelectedRows();
    int[] modelRows = new int[rows.length];
    for (int i = 0; i < rows.length; i++) {
      modelRows[i] = catTable.convertRowIndexToModel(rows[i]);
    }
    catTableModel.addLimit(modelRows);
  }

  @SuppressWarnings("unchecked")
  private void fileHyperlinkActionPerformed() {
    FileChooser openSvgDialog = FileChoosers.getOpenSvgDialog();
    if (openSvgDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openSvgDialog.getSelectedFile();
      File svgFile = null;
      File splFile = null;
      IdFileFilter<Integer> fileFilter = ((IdFileFilter<Integer>) openSvgDialog.getFileFilter());
      if (fileFilter.getId() == 0) svgFile = file;
      else if (fileFilter.getId() == 1) splFile = file;
      int dy = SwingUtilities.convertPoint(fileHyperlink, fileHyperlink.getLocation(), this).y + fileHyperlink.getHeight();
      if (radioButton3.isSelected()) dy += 132;//высота меньше за счет доп. категорий
      loader = new SVGLoader(svgFile, splFile, (int) (gcBounds.width * 0.9), (int) ((gcBounds.height - dy) * 0.8));
      loader.prepare();
      loader.execute();
      Env.pref.put("dir.svg", openSvgDialog.getCurrentDirectory().getAbsolutePath());
    }
  }

  private void addSplFileHyperlinkActionPerformed() {
    FileChooser openSplDialog = FileChoosers.getOpenSplDialog();
    if (openSplDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openSplDialog.getSelectedFile();
      try {
        byte[] splData = Files.readAllBytes(file.toPath());
        EventimManager eventimManager = EventimManager.read(splData);
        List<EventimSeat> seatList = eventimManager.getSeatList();
        if (seatList.isEmpty()) throw new PlanException("Схема не содержит мест с размещением");
        List<EventimNplCategory> categoryList = eventimManager.getNplCategoryList();
        if (!categoryList.isEmpty() && radioButton2.isSelected()) {
          throw new PlanException("Схема содержит места без размещения");
        }
        if (categoryList.isEmpty() && radioButton3.isSelected()) {
          throw new PlanException("Схема не содержит мест без размещения");
        }
        List<String> eventimStringList = new ArrayList<>(seatList.size());
        StringBuilder eventimStr = new StringBuilder("Список мест с размещением в spl:\n");
        for (EventimSeat eventimSeat : seatList) {
          String eventimString = eventimSeat.toString();
          eventimStringList.add(eventimString);
          eventimStr.append(eventimString).append("\n");
        }
        eventimStr.append("\n").append("Всего на схеме spl ").append(seatsForm.val(seatList.size())).append(" с размещением\n");
        SVGPlanEditor svgPlan = svgResult.getPlan();
        List<SVGSeat> notFoundSeatList = svgPlan.getNotFoundSeatList(eventimStringList);
        StringBuilder report = new StringBuilder();
        report.append("Всего на схеме spl ").append(seatsForm.val(seatList.size())).append("\n");
        report.append("Всего на схеме svg ").append(seatsForm.val(svgPlan.getSeatSize())).append("\n\n");
        if (!notFoundSeatList.isEmpty()) {
          report.append("Не найдено на схеме spl ").append(seatsForm.val(notFoundSeatList.size())).append(" из svg:\n");
          for (SVGSeat seat : notFoundSeatList) {
            report.append(seat.toEventimString()).append("\n");
          }
          report.append("\n");
          report.append("Найдено на схеме spl ").append(seatsForm.val(svgPlan.getSeatSize() - notFoundSeatList.size())).append(" из svg\n");
        } else report.append("Все места из svg найдены на схеме spl\n");

        addSplFileHyperlink.setText(file.getName());
        svgResult.setEventimManager(eventimManager);
        radioButton1.setEnabled(false);
        if (radioButton2.isSelected()) radioButton3.setEnabled(false);
        if (radioButton3.isSelected()) radioButton2.setEnabled(false);
        catTableModel.setDataFromSpl(eventimManager.getNplCategoryList());
        addButton.setEnabled(false);
        ScrollOptionPane.showMessageDialog(this, eventimStr.toString(), "Список мест spl", JOptionPane.INFORMATION_MESSAGE, false, new Position(Position.Horizontal.RIGHT));
        ScrollOptionPane.showMessageDialog(this, report.toString(), "Отчет", JOptionPane.INFORMATION_MESSAGE);
      } catch (PlanException e) {
        JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      Env.pref.put("dir.spl", openSplDialog.getCurrentDirectory().getAbsolutePath());
    }
  }

  private void splNplFileHyperlinkActionPerformed() {
    FileChooser openSplDialog = FileChoosers.getOpenSplDialog();
    if (openSplDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openSplDialog.getSelectedFile();
      try {
        byte[] splData = Files.readAllBytes(file.toPath());
        EventimManager eventimManager = EventimManager.read(splData);
        if (!eventimManager.getSeatList().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Схема spl содержит места с размещением", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
        radioButton2.setEnabled(false);
        radioButton3.setEnabled(false);
        catTableModel.setDataFromSpl(eventimManager.getNplCategoryList());
        addButton.setEnabled(false);
        splNplFileHyperlink.setText(file.getName());
        nplsplData = splData;
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ошибка при загрузке схемы зала spl", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      Env.pref.put("dir.spl", openSplDialog.getCurrentDirectory().getAbsolutePath());
    }
  }

  private void selImageCheckBoxItemStateChanged(ItemEvent e) {
    if (svgResult == null) return;
    if (e.getStateChange() == ItemEvent.SELECTED) {
      svgLabel.setIcon(svgResult.getSelImageIcon());
    } else {
      svgLabel.setIcon(svgResult.getImageIcon());
    }
  }

  private void okButtonActionPerformed() {
    if ((radioButton1.isSelected() || radioButton3.isSelected()) && catTable.isEditing()) return;
    int totalSeats = 0;
    if (venueIntComboBox.getSelectedIndex() == -1) {
      venueIntComboBox.requestFocus();
      JOptionPane.showMessageDialog(this, "Необходимо выбрать место проведения", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (planTextField.getText().trim().isEmpty()) {
      planTextField.requestFocus();
      JOptionPane.showMessageDialog(this, "Название схемы зала не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (radioButton2.isSelected() || radioButton3.isSelected()) {//с местами или комбинированная
      if (svgResult == null) {
        JOptionPane.showMessageDialog(this, "Схема зала не загружена", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (svgResult.getPlan().getCategoryList().isEmpty()) {
        JOptionPane.showMessageDialog(this, "На схеме нет доступных мест", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      for (SVGCategory category : svgResult.getPlan().getCategoryList()) {
        totalSeats += category.getSeatsNumber();
      }
    }
    List<CategoryObj> categoryList = null;
    List<CategoryLimitObj> catLimitList = null;
    if (radioButton1.isSelected() || radioButton3.isSelected()) {//без мест или комбинированная
      categoryList = catTableModel.getCategoryList();
      if (categoryList.size() < 1) {
        JOptionPane.showMessageDialog(this, "Необходимо создать хотя бы одну категорию", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      for (int i = 0; i < categoryList.size(); i++) {
        CategoryObj category = categoryList.get(i);
        int num = i + 1;
        if (category.getName().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Название категории " + num + " не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
          TableUtils.editCellAtModel(catTable, i, 1);
          return;
        }
        if (category.getSeatsNumber() < 0) {
          JOptionPane.showMessageDialog(this, "Количество мест категории " + num + " не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
          TableUtils.editCellAtModel(catTable, i, 2);
          return;
        }
        totalSeats += category.getSeatsNumber();
      }
      catLimitList = catTableModel.getCategoryLimitList();
      for (int i = 0; i < catLimitList.size(); i++) {
        CategoryLimitObj categoryLimit = catLimitList.get(i);
        if (categoryLimit.getLimit() <= 0) {
          JOptionPane.showMessageDialog(this, "Ограничение не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
          TableUtils.editCellAtModel(catTable, categoryList.size() + i, 2);
          return;
        }
        int limitSeatsNumber = 0;
        for (CategoryObj category : categoryLimit.getCategoryList()) {
          limitSeatsNumber += category.getSeatsNumber();
        }
        if (categoryLimit.getLimit() >= limitSeatsNumber) {
          JOptionPane.showMessageDialog(this, "Ограничение не имеет смысла", "Ошибка", JOptionPane.ERROR_MESSAGE);
          TableUtils.editCellAtModel(catTable, categoryList.size() + i, 2);
          return;
        }
      }
    }
    if (totalSeats > Env.MAX_PLAN_NUMBER_SEATS && Env.user.getUserType() != UserType.OPERATOR) {
      JOptionPane.showMessageDialog(this, "Схема содержит " + totalSeats + " мест (больше " + Env.MAX_PLAN_NUMBER_SEATS + ")", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (totalSeats > Env.MAX_PLAN_NUMBER_SEATS2) {
      JOptionPane.showMessageDialog(this, "Схема содержит " + totalSeats + " мест (больше " + Env.MAX_PLAN_NUMBER_SEATS2 + ")", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    long venueId = venueIntComboBox.getItemAt(venueIntComboBox.getSelectedIndex()).getId();
    String planName = planTextField.getText().trim();
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
    if (radioButton1.isSelected()) {
      Object[] data = {venueId, planName, categoryList, catLimitList, nplsplData};
      Env.net.create("ADD_PLAN_1", new Request(data), this).start();
    } else if (radioButton2.isSelected()) {
      Object[] data = {venueId, planName, svgResult.getPlan().getSvgData(), svgResult.getSplData()};
      Env.net.create("ADD_PLAN_2", new Request(data), this, true, 90000).start();
    } else if (radioButton3.isSelected()) {
      Object[] data = {venueId, planName, svgResult.getPlan().getSvgData(), categoryList, catLimitList, svgResult.getSplData()};
      Env.net.create("ADD_PLAN_3", new Request(data), this, true, 90000).start();
    }
  }

  private void cancelButtonActionPerformed() {
    thisWindowClosing();
    this.dispose();
  }

  private void thisWindowClosing() {
    if (loader != null) loader.cancel();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel dialogPane = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel label1 = new JLabel();
    venueIntComboBox = new SuggestionComboBox<>();
    JLabel label2 = new JLabel();
    planTextField = new JTextField();
    JPanel typePanel = new JPanel();
    radioButton1 = new JRadioButton();
    radioButton2 = new JRadioButton();
    radioButton3 = new JRadioButton();
    placementPanel = new JPanel();
    JPanel panel1 = new JPanel();
    fileHyperlink = new JXHyperlink();
    fileHyperlink.setClickedColor(fileHyperlink.getUnclickedColor());
    addSplFileHyperlink = new JXHyperlink();
    addSplFileHyperlink.setClickedColor(addSplFileHyperlink.getUnclickedColor());
    selImageCheckBox = new JCheckBox();
    svgPanel = new JPanel();
    nonPlacementPanel = new JPanel();
    splNplFileHyperlink = new JXHyperlink();
    splNplFileHyperlink.setClickedColor(splNplFileHyperlink.getUnclickedColor());
    catScrollPane = new JScrollPane();
    catTable = new JXTable();
    addButton = new JButton();
    removeButton = new JButton();
    addLimitButton = new JButton();
    JPanel buttonBar = new JPanel();
    okButton = new JButton();
    JButton cancelButton = new JButton();
    busyLabel = new JXBusyLabel();
    svgLabel = new JLabel();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    setTitle("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0445\u0435\u043c\u0443 \u0437\u0430\u043b\u0430...");
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        thisWindowClosing();
      }
    });
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(new EmptyBorder(10, 10, 10, 10));
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 100, 0};
        ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u041c\u0435\u0441\u0442\u043e:");
        contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(venueIntComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //---- label2 ----
        label2.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
        contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 5), 0, 0));
        contentPanel.add(planTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== typePanel ========
        {
          typePanel.setMinimumSize(new Dimension(600, 20));
          typePanel.setPreferredSize(new Dimension(600, 20));
          typePanel.setLayout(new GridBagLayout());
          ((GridBagLayout)typePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
          ((GridBagLayout)typePanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)typePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)typePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- radioButton1 ----
          radioButton1.setText("\u0411\u0435\u0437 \u0440\u0430\u0437\u043c\u0435\u0449\u0435\u043d\u0438\u044f");
          radioButton1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              radioButton1ItemStateChanged(e);
            }
          });
          typePanel.add(radioButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- radioButton2 ----
          radioButton2.setText("\u0421 \u0440\u0430\u0437\u043c\u0435\u0449\u0435\u043d\u0438\u0435\u043c");
          radioButton2.setSelected(true);
          radioButton2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              radioButton2ItemStateChanged(e);
            }
          });
          typePanel.add(radioButton2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- radioButton3 ----
          radioButton3.setText("\u041a\u043e\u043c\u0431\u0438\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u0430\u044f");
          radioButton3.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              radioButton3ItemStateChanged(e);
            }
          });
          typePanel.add(radioButton3, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(typePanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== placementPanel ========
        {
          placementPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)placementPanel.getLayout()).columnWidths = new int[] {0, 0};
          ((GridBagLayout)placementPanel.getLayout()).rowHeights = new int[] {0, 100, 0};
          ((GridBagLayout)placementPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
          ((GridBagLayout)placementPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

          //======== panel1 ========
          {
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- fileHyperlink ----
            fileHyperlink.setText("\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u0444\u0430\u0439\u043b...");
            fileHyperlink.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                fileHyperlinkActionPerformed();
              }
            });
            panel1.add(fileHyperlink, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 15), 0, 0));

            //---- addSplFileHyperlink ----
            addSplFileHyperlink.setText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c spl...");
            addSplFileHyperlink.setVisible(false);
            addSplFileHyperlink.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                addSplFileHyperlinkActionPerformed();
              }
            });
            panel1.add(addSplFileHyperlink, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- selImageCheckBox ----
            selImageCheckBox.setText("\u0412\u044b\u0434\u0435\u043b\u0438\u0442\u044c \u043c\u0435\u0441\u0442\u0430");
            selImageCheckBox.setEnabled(false);
            selImageCheckBox.addItemListener(new ItemListener() {
              @Override
              public void itemStateChanged(ItemEvent e) {
                selImageCheckBoxItemStateChanged(e);
              }
            });
            panel1.add(selImageCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 0), 0, 0));
          }
          placementPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //======== svgPanel ========
          {
            svgPanel.setLayout(new BorderLayout());
          }
          placementPanel.add(svgPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPanel.add(placementPanel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== nonPlacementPanel ========
        {
          nonPlacementPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)nonPlacementPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)nonPlacementPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
          ((GridBagLayout)nonPlacementPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
          ((GridBagLayout)nonPlacementPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

          //---- splNplFileHyperlink ----
          splNplFileHyperlink.setText("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c spl...");
          splNplFileHyperlink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              splNplFileHyperlinkActionPerformed();
            }
          });
          nonPlacementPanel.add(splNplFileHyperlink, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //======== catScrollPane ========
          {
            catScrollPane.setMinimumSize(new Dimension(100, 75));
            catScrollPane.setPreferredSize(new Dimension(100, 132));

            //---- catTable ----
            catTable.setSortable(false);
            catTable.setAutoCreateRowSorter(false);
            catScrollPane.setViewportView(catTable);
          }
          nonPlacementPanel.add(catScrollPane, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- addButton ----
          addButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addButton.setMargin(new Insets(1, 1, 1, 1));
          addButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e");
          addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addButtonActionPerformed();
            }
          });
          nonPlacementPanel.add(addButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- removeButton ----
          removeButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          removeButton.setMargin(new Insets(1, 1, 1, 1));
          removeButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e");
          removeButton.setEnabled(false);
          removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              removeButtonActionPerformed();
            }
          });
          nonPlacementPanel.add(removeButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- addLimitButton ----
          addLimitButton.setIcon(new ImageIcon(getClass().getResource("/resources/limit.png")));
          addLimitButton.setMargin(new Insets(1, 1, 1, 1));
          addLimitButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435");
          addLimitButton.setEnabled(false);
          addLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addLimitButtonActionPerformed();
            }
          });
          nonPlacementPanel.add(addLimitButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPanel.add(nonPlacementPanel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
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

    //---- busyLabel ----
    busyLabel.setText("\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0444\u0430\u0439\u043b\u0430...");
    busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

    //---- buttonGroup1 ----
    ButtonGroup buttonGroup1 = new ButtonGroup();
    buttonGroup1.add(radioButton1);
    buttonGroup1.add(radioButton2);
    buttonGroup1.add(radioButton3);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) waitingDialog.setVisible(true);
    if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    SeatingPlanObj seatingPlanObj = (SeatingPlanObj) result.getResponse().getData();
    seatingPlanObj.setChildless(true);
    planComboBox.addElement(seatingPlanObj, true);
    childlessControl.updateChildless();
    this.dispose();
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.isDataSent())
      JOptionPane.showMessageDialog(this, "Команда отправлена на сервер, но подтверждение не было получено", "Ошибка", JOptionPane.ERROR_MESSAGE);
    else
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось добавить схему зала", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private class SVGLoader extends SwingWorker<SVGResult, String> {
    private static final String convertBusyText = "Конвертация файла...";
    private static final String processBusyText = "Обработка файла...";
    private static final String renderBusyText = "Отрисовка файла...";
    private final File svgFile;
    private final File splFile;
    private final int maxWidth;
    private final int maxHeight;
    private volatile boolean cancelled = false;

    public SVGLoader(File svgFile, File splFile, int maxWidth, int maxHeight) {
      this.svgFile = svgFile;
      this.splFile = splFile;
      this.maxWidth = maxWidth;
      this.maxHeight = maxHeight;
      if (svgFile == null && splFile == null) throw new IllegalArgumentException("need at least one file");
    }

    public void cancel() {
      cancelled = true;//используем свою переменную, поскольку isCancelled далеко не всегда возвращает true
      cancel(true);
    }

    public void prepare() {
      svgResult = null;
      radioButton1.setEnabled(false);
      radioButton2.setEnabled(false);
      radioButton3.setEnabled(false);
      fileHyperlink.setEnabled(false);
      addSplFileHyperlink.setVisible(false);
      selImageCheckBox.setSelected(false);
      selImageCheckBox.setEnabled(false);
      okButton.setEnabled(false);
      fileHyperlink.setText("Выбрать файл...");
      if (svgFile == null) busyLabel.setText(convertBusyText);
      else busyLabel.setText(processBusyText);
      busyLabel.setBusy(true);
      svgPanel.removeAll();
      svgPanel.add(busyLabel, BorderLayout.CENTER);
      svgPanel.revalidate();
    }

    @Override
    protected SVGResult doInBackground() throws Exception {
      byte[] svgData;
      EventimManager eventimManager = null;
      if (svgFile == null) {
        eventimManager = EventimManager.read(Files.readAllBytes(splFile.toPath()));
        List<EventimSeat> seatList = eventimManager.getSeatList();
        if (seatList.isEmpty()) throw new PlanException("Схема spl не содержит мест с размещением");
        List<EventimNplCategory> categoryList = eventimManager.getNplCategoryList();
        if (!categoryList.isEmpty() && radioButton2.isSelected()) {
          throw new PlanException("Схема spl содержит места без размещения");
        }
        if (categoryList.isEmpty() && radioButton3.isSelected()) {
          throw new PlanException("Схема spl не содержит мест без размещения");
        }
        svgData = SplToSvg.convert(seatList);
        publish(processBusyText);
      } else {
        svgData = Files.readAllBytes(svgFile.toPath());
        if (svgFile.getName().endsWith("svgz")) svgData = SVGTranscoder.fromGzip(svgData);
      }
      SVGPlanEditor plan = new SVGPlanEditor(svgData);
      byte[] editor1SvgData = plan.getEditor1SvgData();
      byte[] editor2SvgData = plan.getEditor2SvgData();
      String warnMessage = null;
      if (SVGPlanEditor.isSizeError(editor1SvgData.length, plan.isFromSpl())) {
        warnMessage = SVGPlanEditor.getSizeError(editor1SvgData.length, plan.isFromSpl());
      } else {
        Integer ratioError = SVGPlanEditor.getRatioError(editor1SvgData.length, plan.getSeatSize());
        if (ratioError != null) {
          warnMessage = SVGPlanEditor.getRatioError(ratioError) + "\nДанная схема не будет принята сервером.";
        } else {
          Integer ratioWarn = SVGPlanEditor.getRatioWarn(editor1SvgData.length, plan.getSeatSize());
          if (ratioWarn != null) {
            warnMessage = SVGPlanEditor.getRatioWarn(ratioWarn)
                + "\nЕсли по тем или иным причинам в SVG схеме сознательно используются изображения большого размера\n"
                + "или какие-то другие тяжеловесные элементы дизайна, то можно игнорировать это предупреждение и продолжить загрузку схемы.\n"
                + "Не рекомендуется делать на основе схем с размером выше нормы большое количество сеансов.";
          }
        }
      }
      //todo start
//      try {
//        List<SVGCategory> categoryList = plan.getCategoryList();
//        for (int i = 0; i < categoryList.size(); i++) {
//          SVGCategory svgCategory = categoryList.get(i);
//          svgCategory.setObjectID(100 + i);
//        }
//        byte[] processedSvgData = plan.getProcessedSvgData();
//        byte[] indentXML = SVGPlan.svgToSvgIndentXML(processedSvgData);
//        Files.write(new File("D:\\test1.svg").toPath(), processedSvgData);
//        Files.write(new File("D:\\test1.xml").toPath(), indentXML);
//
//        SVGPlanVenue plan2 = new SVGPlanVenue(processedSvgData);
//        long seatOID = 1;
//        for (int i = 0; i < categoryList.size(); i++) {
//          plan2.replaceCategoryId(100 + i, 1000 + i);
//          for (SVGSeat svgSeat : plan2.getSeatSet(100 + i)) {
//            svgSeat.setObjectID(seatOID);
//            seatOID++;
//          }
//        }
//        for (SVGSeat svgSeat : plan2.getInaccessibleSeatSet()) {
//          svgSeat.setObjectID(seatOID);
//          seatOID++;
//        }
//        plan2.setInaccessibleCategory(100);
//        processedSvgData = plan2.getProcessedSvgData();
//        indentXML = SVGPlan.svgToSvgIndentXML(processedSvgData);
//        Files.write(new File("D:\\test2.svg").toPath(), processedSvgData);
//        Files.write(new File("D:\\test2.xml").toPath(), indentXML);
//
//        Random random = new Random();
//        SVGPlanEvent plan3 = new SVGPlanEvent(processedSvgData);
//        seatOID = 1;
//        for (int i = 0; i < categoryList.size(); i++) {
//          SVGCategory svgCategory = categoryList.get(i);
//          plan3.setCategoryData(1000 + i, svgCategory.getName(), svgCategory.getInitPrice());
//          for (SVGSeat svgSeat : plan2.getSeatSet(100 + i)) {
//            plan3.setSeatState(seatOID, random.nextInt(4) + 1, random.nextInt(10) < 1);
//            seatOID++;
//          }
//        }
//        for (SVGSeat svgSeat : plan2.getInaccessibleSeatSet()) {
//          plan3.setSeatState(seatOID, 0, false);
//          seatOID++;
//        }
//        plan3.metaUnusedCategories();
//        plan3.hideUnusedCategories();
//        processedSvgData = plan3.getProcessedSvgData();
//        indentXML = SVGPlan.svgToSvgIndentXML(processedSvgData);
//        Files.write(new File("D:\\test3.svg").toPath(), processedSvgData);
//        Files.write(new File("D:\\test3.xml").toPath(), indentXML);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
      //todo end
      publish(renderBusyText);
      Image image = SVGTranscoder.svgToScaledImage(editor1SvgData, maxWidth, maxHeight);
      Image selectedImage = SVGTranscoder.svgToScaledImage(editor2SvgData, maxWidth, maxHeight);
      return new SVGResult(new ImageIcon(image), new ImageIcon(selectedImage), plan, warnMessage, eventimManager);
    }

    @Override
    protected void process(List<String> chunks) {
      busyLabel.setText(chunks.get(chunks.size() - 1));
    }

    @Override
    protected void done() {
      if (cancelled) return;
      radioButton1.setEnabled(true);
      radioButton2.setEnabled(true);
      radioButton3.setEnabled(true);
      fileHyperlink.setEnabled(true);
      okButton.setEnabled(true);
      busyLabel.setBusy(false);
      try {
        SVGResult result = get();
        if (result.getEventimManager() != null) {
          radioButton1.setEnabled(false);
          if (radioButton2.isSelected()) radioButton3.setEnabled(false);
          if (radioButton3.isSelected()) radioButton2.setEnabled(false);
          catTableModel.setDataFromSpl(result.getEventimManager().getNplCategoryList());
          addButton.setEnabled(false);
        }
        fileHyperlink.setText(svgFile != null ? svgFile.getName() : splFile.getName());
        svgLabel.setIcon(result.getImageIcon());
        svgPanel.removeAll();
        svgPanel.add(svgLabel, BorderLayout.CENTER);
        svgPanel.revalidate();
        svgPanel.repaint();
        pack();
        setLocationRelativeTo(getOwner());
        addSplFileHyperlink.setText("Добавить spl...");
        addSplFileHyperlink.setVisible(result.getEventimManager() == null);
        selImageCheckBox.setEnabled(true);
        svgResult = result;
        if (result.getWarnMessage() != null) {
          JOptionPane.showMessageDialog(AddSeatingPlanDialog.this, result.getWarnMessage(), "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
        ScrollOptionPane.showMessageDialog(AddSeatingPlanDialog.this, result.getPlan().getReport(), "Отчет", JOptionPane.INFORMATION_MESSAGE);
      } catch (ExecutionException | InterruptedException e) {
        svgPanel.removeAll();
        svgPanel.revalidate();
        svgPanel.repaint();
        String error;
        if (e instanceof ExecutionException && e.getCause().getMessage() != null) {
          error = "Ошибка при загрузке схемы зала: " + e.getCause().getMessage();
        } else error = "Ошибка при загрузке схемы зала";
        if (!(e instanceof ExecutionException) || !(e.getCause() instanceof PlanException)) {
          e.printStackTrace();
        }
        JOptionPane.showMessageDialog(AddSeatingPlanDialog.this, error, "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static class SVGResult {
    private final ImageIcon image;
    private final ImageIcon selImage;
    @NotNull
    private final SVGPlanEditor plan;
    @Nullable
    private final String warnMessage;
    @Nullable
    private EventimManager eventimManager;

    public SVGResult(ImageIcon image, ImageIcon selImage, @NotNull SVGPlanEditor plan, @Nullable String warnMessage, @Nullable EventimManager eventimManager) {
      this.image = image;
      this.selImage = selImage;
      this.plan = plan;
      this.warnMessage = warnMessage;
      this.eventimManager = eventimManager;
    }

    public ImageIcon getImageIcon() {
      return image;
    }

    public ImageIcon getSelImageIcon() {
      return selImage;
    }

    @NotNull
    public SVGPlanEditor getPlan() {
      return plan;
    }

    @Nullable
    public String getWarnMessage() {
      return warnMessage;
    }

    @Nullable
    public EventimManager getEventimManager() {
      return eventimManager;
    }

    public void setEventimManager(@Nullable EventimManager eventimManager) {
      this.eventimManager = eventimManager;
    }

    @Nullable
    public byte[] getSplData() {
      if (eventimManager == null) return null;
      return eventimManager.getData();
    }
  }

  private static class PlanException extends Exception {
    public PlanException(String message) {
      super(message);
    }
  }
}
