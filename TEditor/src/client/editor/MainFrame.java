/*
 * Created by JFormDesigner on Mon Jun 15 16:46:10 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import client.component.*;
import client.component.filter.DigitsDocumentFilter;
import client.component.listener.OperationListener;
import client.editor.component.*;
import client.editor.component.filter.*;
import client.editor.component.listener.*;
import client.editor.component.renderer.*;
import client.editor.model.*;
import client.editor.svn.SvnRevision;
import client.formatter.*;
import client.net.*;
import client.renderer.NumberCellRenderer;
import client.utils.*;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.table.NumberEditorExt;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.*;
import server.protocol2.editor.*;

import static client.editor.Env.net;
import static client.editor.Env.user;

/**
 * @author Maksim
 */
public class MainFrame extends JFrame implements ChildlessControl, ActionEventSyncListener, NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private StatusBarPanel statusBarPanel;
  private OperationComboBox<CityObj> cityComboBox;
  private JButton addCityButton;
  private JButton delCityButton;
  private JTextField cityTextField;
  private JButton saveCityButton;
  private OperationComboBox<VenueObj> venueComboBox;
  private JButton addVenueButton;
  private JButton delVenueButton;
  private JButton saveVenueButton;
  private JTextField venueNameTextField;
  private JTextField addressTextField;
  private JComboBox<VenueType> venueTypeComboBox;
  private JTextField latTextField;
  private JTextField lonTextField;
  private JLabel venueDescLabel;
  private PosterLabel venueBigImageLabel;
  private JTextArea venueDescTextArea;
  private OperationComboBox<SeatingPlanObj> planComboBox;
  private JButton delPlanButton;
  private JTextField planNameTextField;
  private JButton savePlanButton;
  private JPanel planDataPanel;
  private PlanSvgPanel planImagePanel;
  private JScrollPane planCatScrollPane;
  private JXTable catTable;
  private OperationComboBox<KindObj> kindComboBox;
  private OperationComboBox<ActionObj> actionComboBox;
  private JButton delActionButton;
  private JButton saveActionButton;
  private JComboBox<ActionObj.Age> actionAgeComboBox;
  private JFormattedTextField actionDurationTextField;
  private JCheckBox actualActionCheckBox;
  private JTextField actionOwnerNameTextField;
  private JTextField actionOwnerInnTextField;
  private JTextField actionOwnerPhoneTextField;
  private JTextField actionNameTextField;
  private JButton genresButton;
  private JTextField actionFullNameTextField;
  private JLabel posterLabel;
  private JXHyperlink bookletLabel;
  private JXHyperlink newBookletLabel;
  private JComboBox<Integer> actionRatingComboBox;
  private JFormattedTextField kdpTextField;
  private JFormattedTextField chargeTextField;
  private JTextField posterNameTextField;
  private JTextArea posterDescTextArea;
  private PosterLabel actionBigImageLabel;
  private PosterLabel actionSmallImageLabel;
  private OperationComboBox<ActionEventObj> actionEventComboBox;
  private JButton delActionEventButton;
  private JButton loadArchivalButton;
  private JButton controlActionEventButton;
  private JButton saveActionEventButton;
  private JXHyperlink ebsLabel;
  private FixedTabbedPane eventTabbedPane;
  private JXDateTimePicker showDatePicker;
  private JXTable catPriceTable;
  private JXDateTimePicker sellStartDatePicker;
  private JXDateTimePicker sellEndDatePicker;
  private JCheckBox sellEnabledCheckBox;
  private JCheckBox fullNameRequiredCheckBox;
  private JCheckBox eTicketsCheckBox;
  private JCheckBox phoneRequiredCheckBox;
  private JCheckBox longReserveCheckBox;
  private JFormattedTextField maxReserveTimeTextField;
  private JCheckBox ticketRefundAllowedCheckBox;
  private JCheckBox vatCheckBox;
  private JFormattedTextField vatTextField;
  private JCheckBox ticketReissueAllowedCheckBox;
  private JButton applyEventParamsButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  private final PredicateFilter<ActionObj> actualActionFilter;
  private final PredicateFilter<ActionObj> onlyVenueActionFilter;
  private final WaitingDialog waitingDialog;
  private final CatTableModel catTableModel = new CatTableModel();
  private final CatPriceTableModel catPriceTableModel = new CatPriceTableModel();
  private final GenresTableModel genresTableModel = new GenresTableModel();
  private boolean allActionsLoaded = false;
  private GenresDialog genresDialog = null;
  private SubsFrame subsFrame = null;
  private SyncFrame syncFrame = null;
  private File dragFile;
  private boolean saveResult;

  public MainFrame() {
    initComponents();
    setTitle(getTitle() + Env.ver + " build " + SvnRevision.SVN_REV + " от " + SvnRevision.SVN_REV_DATE);
    if (Env.testZone) setTitle(getTitle() + " [Тестовая зона]");

    ((AbstractDocument) latTextField.getDocument()).setDocumentFilter(new GeoDocumentFilter(latTextField));
    ((AbstractDocument) lonTextField.getDocument()).setDocumentFilter(new GeoDocumentFilter(lonTextField));
    ((AbstractDocument) actionOwnerInnTextField.getDocument()).setDocumentFilter(new DigitsDocumentFilter(actionOwnerInnTextField));

    actualActionFilter = new PredicateFilter<ActionObj>() {
      @Override
      public boolean filter(@NotNull ActionObj element) {
        return element.isActual();
      }
    };
    onlyVenueActionFilter = new PredicateFilter<ActionObj>() {
      @Override
      public boolean filter(@NotNull ActionObj element) {
        return !element.getVenueIdSet().isEmpty();
      }
    };

    CityListRenderer cityListRenderer = new CityListRenderer();
    cityComboBox.setRenderer(cityListRenderer);
    cityComboBox.setElementToStringConverter(cityListRenderer);
    cityComboBox.setChangeConfirmComponent(this);
    cityComboBox.setChangeConfirmText("Сохранить изменения в названии города?");
    cityComboBox.setOperationListener(new CityOperationListener());
    if (user.getUserType() == UserType.OPERATOR) {
      cityComboBox.listenChanges(cityTextField);
      cityComboBox.enableComponentChanges(saveCityButton);
    }

    VenueListRenderer venueListRenderer = new VenueListRenderer();
    venueComboBox.setRenderer(venueListRenderer);
    venueComboBox.setElementToStringConverter(venueListRenderer);
    venueComboBox.setChangeConfirmComponent(this);
    venueComboBox.setChangeConfirmText("Сохранить изменения в месте проведения?");
    venueComboBox.setOperationListener(new VenueOperationListener());
    if (user.getUserType() == UserType.OPERATOR) {
      venueComboBox.listenChanges(venueNameTextField);
      venueComboBox.listenChanges(addressTextField);
      venueComboBox.listenChanges(venueTypeComboBox);
      venueComboBox.listenChanges(latTextField);
      venueComboBox.listenChanges(lonTextField);
      venueComboBox.listenChanges(venueDescTextArea);
      venueComboBox.enableComponentChanges(saveVenueButton);
      venueBigImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    venueComboBox.linkTo(cityComboBox, new OperationFilter<VenueObj>() {
      @Override
      public boolean filter(@NotNull VenueObj element, @Nullable Object match) {
        return element.pass(match);
      }
    });

    planComboBox.setRenderer(new SeatingPlanListRenderer(30));
    planComboBox.setChangeConfirmComponent(this);
    planComboBox.setChangeConfirmText("Сохранить изменения в схеме зала?");
    planComboBox.setOperationListener(new PlanOperationListener());
    planComboBox.listenChanges(planNameTextField);
    planComboBox.listenChanges(catTableModel);
    planComboBox.enableComponentChanges(savePlanButton);
    planComboBox.linkTo(venueComboBox, new OperationFilter<SeatingPlanObj>() {
      @Override
      public boolean filter(@NotNull SeatingPlanObj element, @Nullable Object match) {
        return element.pass(match);
      }
    });

    ActionListRenderer actionListRenderer = new ActionListRenderer();
    actionListRenderer.setShowOrganizer(user.getUserType() == UserType.OPERATOR);
    actionComboBox.setRenderer(actionListRenderer);
    actionComboBox.setElementToStringConverter(actionListRenderer);
    actionComboBox.setChangeConfirmComponent(this);
    actionComboBox.setChangeConfirmText("Сохранить изменения в представлении?");
    actionComboBox.setOperationListener(new ActionOperationListener());
    actionComboBox.listenChanges(actionAgeComboBox);
    actionComboBox.listenChanges(actionDurationTextField);
    actionComboBox.listenChanges(actionOwnerNameTextField);
    actionComboBox.listenChanges(actionOwnerInnTextField);
    actionComboBox.listenChanges(actionOwnerPhoneTextField);
    actionComboBox.listenChanges(actionNameTextField);
    actionComboBox.listenChanges(actionFullNameTextField);
    actionComboBox.listenChanges(actionRatingComboBox);
    actionComboBox.listenChanges(kdpTextField);
    actionComboBox.listenChanges(chargeTextField);
    actionComboBox.listenChanges(posterNameTextField);
    actionComboBox.listenChanges(posterDescTextArea);
    actionComboBox.listenChanges(genresTableModel);
    actionComboBox.enableComponentChanges(saveActionButton);
    actionComboBox.linkTo(kindComboBox, new OperationFilter<ActionObj>() {
      @Override
      public boolean filter(@NotNull ActionObj element, @Nullable Object match) {
        return element.pass(match);
      }
    });
    actionComboBox.linkTo(venueComboBox, new OperationFilter<ActionObj>() {
      @Override
      public boolean filter(@NotNull ActionObj element, @Nullable Object match) {
        return element.pass(match);
      }
    });
    actionComboBox.setPreferredSize(new Dimension(0, actionComboBox.getPreferredSize().height));

    ActionEventListRenderer actionEventListRenderer = new ActionEventListRenderer();
    actionEventComboBox.setRenderer(actionEventListRenderer);
    actionEventComboBox.setElementToStringConverter(actionEventListRenderer);
    actionEventComboBox.setChangeConfirmComponent(this);
    actionEventComboBox.setChangeConfirmText("Сохранить изменения в сеансе?");
    actionEventComboBox.setOperationListener(new ActionEventOperationListener());
    actionEventComboBox.listenChanges(showDatePicker);
    actionEventComboBox.listenChanges(sellStartDatePicker);
    actionEventComboBox.listenChanges(sellEndDatePicker);
    actionEventComboBox.listenChanges(sellEnabledCheckBox);
    actionEventComboBox.listenChanges(eTicketsCheckBox);
    actionEventComboBox.listenChanges(fullNameRequiredCheckBox);
    actionEventComboBox.listenChanges(phoneRequiredCheckBox);
    actionEventComboBox.listenChanges(ticketRefundAllowedCheckBox);
    actionEventComboBox.listenChanges(ticketReissueAllowedCheckBox);
    actionEventComboBox.listenChanges(longReserveCheckBox);
    actionEventComboBox.listenChanges(maxReserveTimeTextField);
    actionEventComboBox.listenChanges(vatCheckBox);
    actionEventComboBox.listenChanges(vatTextField);
    actionEventComboBox.listenChanges(catPriceTableModel);
    actionEventComboBox.enableComponentChanges(saveActionEventButton);
    actionEventComboBox.linkTo(actionComboBox, new OperationFilter<ActionEventObj>() {
      @Override
      public boolean filter(@NotNull ActionEventObj element, @Nullable Object match) {
        return element.pass(match);
      }
    });

    actionRatingComboBox.setModel(new DefaultComboBoxModel<>(new Integer[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0}));
    actionRatingComboBox.setSelectedIndex(-1);
    actionAgeComboBox.setModel(new DefaultComboBoxModel<>(new ActionObj.Age[]{ActionObj.Age.C_0, ActionObj.Age.C_6, ActionObj.Age.C_12, ActionObj.Age.C_16, ActionObj.Age.C_18}));
    actionAgeComboBox.setSelectedIndex(-1);

    posterNameTextField.setPreferredSize(posterNameTextField.getPreferredSize());
    ebsLabel.setPreferredSize(new Dimension(0, ebsLabel.getPreferredSize().height));

    if (user.getUserType() == UserType.ORGANIZER) {
      addCityButton.setVisible(false);
      delCityButton.setVisible(false);
      cityTextField.setEditable(false);

      addVenueButton.setVisible(false);
      delVenueButton.setVisible(false);
      venueNameTextField.setEditable(false);
      addressTextField.setEditable(false);
      venueTypeComboBox.setEnabled(false);
      latTextField.setEditable(false);
      lonTextField.setEditable(false);
      venueDescTextArea.setEditable(false);
      actionRatingComboBox.setEnabled(false);
    }

    PosterTransferHandler posterTransferHandler = new PosterTransferHandler();
    if (user.getUserType() == UserType.OPERATOR) venueBigImageLabel.setTransferHandler(posterTransferHandler);
    actionBigImageLabel.setTransferHandler(posterTransferHandler);
    actionSmallImageLabel.setTransferHandler(posterTransferHandler);
    venueBigImageLabel.setPosterData(PosterData.VENUE_BIG);
    actionBigImageLabel.setPosterData(PosterData.ACTION_BIG);
    actionSmallImageLabel.setPosterData(PosterData.ACTION_SMALL);
    int screenHeight = this.getGraphicsConfiguration().getBounds().height;
    if (screenHeight < 900) {
      venueDescLabel.setVisible(false);
      posterLabel.setVisible(false);
      venueBigImageLabel.setSize(true);
      actionBigImageLabel.setSize(true);
      actionSmallImageLabel.setSize(true);
      planImagePanel.setSize(true);
    }

    statusBarPanel.setUserType(user.getUserType().getDesc());
    statusBarPanel.setAuthorityName(user.getAuthorityName());
    statusBarPanel.addReloadButtonActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        catTable.resetSortOrder();
        catPriceTable.resetSortOrder();
        if (cityComboBox.canExit() && venueComboBox.canExit() && planComboBox.canExit() && actionComboBox.canExit() && actionEventComboBox.canExit()) {
          net.create("GET_INIT_EDITOR", new Request(null), MainFrame.this, 90000).start();
        }
      }
    });
    net.addPoolListener(statusBarPanel, Network.EventMode.EDT_INVOKE_LATER);
    String imageDir = Env.pref.get("dir.image", null);
    File imageDirFile = (imageDir == null ? null : new File(imageDir));
    FileChoosers.createOpenImageDialog(imageDirFile);
    String svgDir = Env.pref.get("dir.svg", null);
    File svgDirFile = (svgDir == null ? null : new File(svgDir));
    FileChoosers.createOpenSvgDialog(svgDirFile);
    String splDir = Env.pref.get("dir.spl", null);
    File splDirFile = (splDir == null ? null : new File(splDir));
    FileChoosers.setOpenSplDirectory(splDirFile);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

    //обзательно до установки модели в таблицу, чтобы вызывалось после обновления таблицы
    catTableModel.addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        TableUtils.updateRowHeights(catTable);
      }
    });
    catTable.setRowHeightEnabled(true);//индивидуальная высота строк
    catTable.setModel(catTableModel);
    catTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    NumberFormatter initPriceEditorFormatter = new InsensitiveDSNumberFormatter(true);
    initPriceEditorFormatter.setValueClass(BigDecimal.class);
    initPriceEditorFormatter.setMinimum(BigDecimal.ZERO);
    NumberEditorExt initPriceEditor = new NumberEditorExt();
    JFormattedTextField initPriceEditorTextField = (JFormattedTextField) initPriceEditor.getComponent();
    initPriceEditorTextField.setFormatterFactory(new DefaultFormatterFactory(initPriceEditorFormatter));
    catTable.setDefaultEditor(BigDecimal.class, initPriceEditor);

    PositiveIntegerFormatter seatsNumberEditorFormatter = new PositiveIntegerFormatter(Env.MAX_CAT_NUMBER_SEATS);
    seatsNumberEditorFormatter.setNullableValue(false);
    NumberEditorExt seatsNumberEditor = new NumberEditorExt();
    JFormattedTextField seatsNumberEditorTextField = (JFormattedTextField) seatsNumberEditor.getComponent();
    seatsNumberEditorTextField.setFormatterFactory(new DefaultFormatterFactory(seatsNumberEditorFormatter));
    catTable.setDefaultEditor(Integer.class, seatsNumberEditor);
    catTable.addHighlighter(new ColorHighlighter(new CatTableLimitPredicate(catTableModel), new Color(221, 221, 221), Color.BLACK, new Color(150, 173, 195), Color.BLACK));

    catPriceTable.setModel(catPriceTableModel);
    catPriceTable.setDefaultRenderer(Number.class, new NumberCellRenderer());
    NumberFormatter priceEditorFormatter = new InsensitiveDSNumberFormatter(false);
    priceEditorFormatter.setValueClass(BigDecimal.class);
    priceEditorFormatter.setMinimum(BigDecimal.ZERO);
    NumberEditorExt priceEditor = new NumberEditorExt();
    JFormattedTextField priceEditorTextField = (JFormattedTextField) priceEditor.getComponent();
    priceEditorTextField.setFormatterFactory(new DefaultFormatterFactory(priceEditorFormatter));
    catPriceTable.setDefaultEditor(BigDecimal.class, priceEditor);
    catPriceTable.addHighlighter(new ColorHighlighter(new CatPriceTableAvailPredicate(catPriceTableModel), null, Color.LIGHT_GRAY, null, Color.DARK_GRAY));

    pack();
    setLocationRelativeTo(null);
    if (screenHeight < 900) setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
  }

  public void startWork() {
    this.setVisible(true);
    net.create("GET_INIT_EDITOR", new Request(null), this, 90000).start();
  }

  @SuppressWarnings("unchecked")
  private void loadData(@NotNull Object[] data) {
    user = (LoginUser) data[0];
    Object[] initData = (Object[]) data[1];
    kindComboBox.setElementList((List<KindObj>) data[2]);
    venueTypeComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>((List<VenueType>) data[3])));
    Env.planUrl = (String) data[4];
    Env.bookletUrl = (String) data[5];
    Env.gatewayList = Collections.unmodifiableList((List<GatewayObj>) data[6]);
    Env.barcodeFormatList = Collections.unmodifiableList((List<BarcodeFormat>) data[7]);
    Env.quotaFormatList = Collections.unmodifiableList((List<QuotaFormatObj>) data[8]);
    Env.bookletTypeList = Collections.unmodifiableList((List<BookletType>) data[9]);
    Env.genreList = Collections.unmodifiableList((List<GenreObj>) data[10]);

    Env.organizerList = Collections.unmodifiableList((List<EOrganizer>) initData[0]);
    int cityIndex = cityComboBox.getSelectedIndex();
    cityComboBox.setElementList((List<CityObj>) initData[1]);
    if (cityIndex == -1) loadState();//первое получение данных
    venueComboBox.setElementList((List<VenueObj>) initData[2]);
    planComboBox.setElementList((List<SeatingPlanObj>) initData[3]);
    actionComboBox.setElementList((List<ActionObj>) initData[4]);
    actionEventComboBox.setElementList((List<ActionEventObj>) initData[5]);
    int sellingActions = (Integer) initData[6];
    int sellingEvents = (Integer) initData[7];
    L10n.PluralForm actionsForm = new L10n.PluralForm("представления", "представлений", "представлений");
    L10n.PluralForm eventsForm = new L10n.PluralForm("сеанс", "сеанса", "сеансов");
    System.out.println("Загружено " + eventsForm.val(actionEventComboBox.getElementList().size()) + " из " + actionsForm.val(actionComboBox.getElementList().size()));
    System.out.println("В продаже " + eventsForm.val(sellingEvents) + " из " + actionsForm.val(sellingActions));
    actualActionCheckBox.setSelected(true);
    allActionsLoaded = false;
    if (cityIndex != -1) {//повторное получение данных
      cityComboBox.reload();
      venueComboBox.reload();
      planComboBox.reload();
      actionComboBox.reload();
      actionEventComboBox.reload();
    }
    updateChildless();
    Env.loadedArchivalActionIdSet.clear();
    Env.gatewayEventMap.clear();
    for (ActionEventObj actionEventObj : actionEventComboBox.getElementList()) {
      Env.addGatewayEvent(actionEventObj);
    }
  }

  public void saveState() {
    CityObj city = cityComboBox.getSelectedElement();
    KindObj kind = kindComboBox.getSelectedElement();
    if (city != null) Env.pref.put("city", String.valueOf(city.getId()));
    if (kind != null) Env.pref.put("kind", String.valueOf(kind.getId()));
  }

  private void loadState() {
    try {
      int kindId = Integer.parseInt(Env.pref.get("kind", ""));
      KindObj kind = KindObj.getInstance(kindId, "");
      kindComboBox.setSelectedItem(kind);
    } catch (NumberFormatException ignored) {
    }
    venueTypeComboBox.setSelectedIndex(-1);
    try {
      long cityId = Long.parseLong(Env.pref.get("city", ""));
      for (CityObj city : cityComboBox.getElementList()) {
        if (city.getId() == cityId) {
          cityComboBox.setSelectedItem(city);
          break;
        }
      }
    } catch (NumberFormatException ignored) {
    }
  }

  //<editor-fold desc="Listeners">
  private void thisWindowClosing() {
    if (cityComboBox.canExit() && venueComboBox.canExit() && planComboBox.canExit() && actionComboBox.canExit() && actionEventComboBox.canExit()) {
      System.exit(0);
    }
  }

  private void imageLabelMouseClicked(MouseEvent e) {
    if (e.getButton() != MouseEvent.BUTTON1) return;
    Component component = e.getComponent();
    if (user.getUserType() == UserType.ORGANIZER && component == venueBigImageLabel) return;
    FileChooser openImageDialog = FileChoosers.getOpenImageDialog();
    if (openImageDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openImageDialog.getSelectedFile();
      if (component == actionBigImageLabel) loadImage(file, PosterData.ACTION_BIG);
      else if (component == actionSmallImageLabel) loadImage(file, PosterData.ACTION_SMALL);
      else if (component == venueBigImageLabel) loadImage(file, PosterData.VENUE_BIG);
      Env.pref.put("dir.image", openImageDialog.getCurrentDirectory().getAbsolutePath());
    }
  }

  private void planImagePanelMouseClicked(MouseEvent e) {
    if (e.getButton() != MouseEvent.BUTTON1) return;
    SeatingPlanObj plan = planComboBox.getSelectedElement();
    if (plan == null) return;
    String title = "Схема зала: " + plan.getName() + " (" + plan.getVenueName() + ")";
    SVGFrame svgFrame = new SVGFrame(this, title, plan.getName(), plan.isCombined(), plan.getSvgZip(), plan.getCategoryList(), plan.getId());
    svgFrame.setVisible(true);
  }

  private void mapButtonActionPerformed() {
    if (checkGeo(true)) {
      String url = "http://maps.yandex.ru/?ll=" + lonTextField.getText() + "," + latTextField.getText() + "&z=17&pt=" + lonTextField.getText() + "," + latTextField.getText();
      try {
        Desktop.getDesktop().browse(new URI(url));
      } catch (IOException | URISyntaxException ex) {
        ex.printStackTrace();
      }
    }
  }

  private void addCityButtonActionPerformed() {
    new AddCityDialog(this, cityComboBox).setVisible(true);
  }

  private void addVenueButtonActionPerformed() {
    new AddVenueDialog(this, this, venueComboBox, cityComboBox).setVisible(true);
  }

  private void addPlanButtonActionPerformed() {
    new AddSeatingPlanDialog(this, this, planComboBox, venueComboBox).setVisible(true);
  }

  private void addActionButtonActionPerformed() {
    new AddActionDialog(this, actionComboBox, kindComboBox).setVisible(true);
  }

  private void addActionEventButtonActionPerformed() {
    CityObj city = cityComboBox.getSelectedElement();
    if (city == null) return;
    new AddActionEventDialog(this, city.getId(), this, actionEventComboBox, planComboBox, actionComboBox).setVisible(true);
  }

  private void saveCityButtonActionPerformed() {
    cityComboBox.saveChanges();
  }

  private void saveVenueButtonActionPerformed() {
    venueComboBox.saveChanges();
  }

  private void savePlanButtonActionPerformed() {
    planComboBox.saveChanges();
  }

  private void saveActionButtonActionPerformed() {
    actionComboBox.saveChanges();
  }

  private void saveActionEventButtonActionPerformed() {
    actionEventComboBox.saveChanges();
  }

  private void delCityButtonActionPerformed() {
    CityObj city = cityComboBox.getSelectedElement();
    if (city == null) return;
    if (JOptionPane.showConfirmDialog(this, "Удалить город?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("DEL_CITY", new Request(city.getId()), this).start();
    }
  }

  private void delVenueButtonActionPerformed() {
    VenueObj venue = venueComboBox.getSelectedElement();
    if (venue == null) return;
    if (JOptionPane.showConfirmDialog(this, "Удалить место проведения?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("DEL_VENUE", new Request(venue.getId()), this).start();
    }
  }

  private void delPlanButtonActionPerformed() {
    SeatingPlanObj seatingPlan = planComboBox.getSelectedElement();
    if (seatingPlan == null) return;
    String question;
    if (seatingPlan.isChildless()) question = "Схема зала не используется. Удалить схему зала?";
    else question = "Пометить схему зала устаревшей?";
    if (JOptionPane.showConfirmDialog(this, question, "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("DEL_PLAN", new Request(seatingPlan.getId()), this, 60000).start();
    }
  }

  private void delActionButtonActionPerformed() {
    ActionObj action = actionComboBox.getSelectedElement();
    if (action == null) return;
    if (JOptionPane.showConfirmDialog(this, "Удалить представление?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("DEL_ACTION", new Request(action.getId()), this).start();
    }
  }

  private void delActionEventButtonActionPerformed() {
    ActionEventObj actionEvent = actionEventComboBox.getSelectedElement();
    if (actionEvent == null) return;
    if (JOptionPane.showConfirmDialog(this, "Удалить сеанс, если нет продаж?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("DEL_ACTION_EVENT", new Request(actionEvent.getId()), this).start();
    }
  }

  private void loadArchivalButtonActionPerformed() {
    ActionObj action = actionComboBox.getSelectedElement();
    if (action == null) return;
    if (Env.loadedArchivalActionIdSet.contains(action.getId())) return;
    net.create("GET_ARCHIVAL_EVENT_LIST", new Request(action.getId()), this).start();
  }

  private void actualActionCheckBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (actionComboBox.isEdited() || actionEventComboBox.isEdited()) {
        //галочка автоматически снимется
        JOptionPane.showMessageDialog(this, "Есть несохраненные изменения", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      actionComboBox.addFilter(actualActionFilter);
    } else {
      actionComboBox.removeFilter(actualActionFilter);
      if (!allActionsLoaded) net.create("GET_ALL_ACTION_LIST", new Request(null), this, 90000).start();
    }
  }

  private void actionLinkCheckBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (actionComboBox.isEdited() || actionEventComboBox.isEdited()) {
        //галочка автоматически снимется
        JOptionPane.showMessageDialog(this, "Есть несохраненные изменения", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      actionComboBox.linkTo(venueComboBox, new OperationFilter<ActionObj>() {
        @Override
        public boolean filter(@NotNull ActionObj element, @Nullable Object match) {
          return element.pass(match);
        }
      });
    } else {
      actionComboBox.unlinkFrom(venueComboBox);
    }
  }

  private void noVenueCheckBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      actionComboBox.removeFilter(onlyVenueActionFilter);
    } else {
      if (actionComboBox.isEdited() || actionEventComboBox.isEdited()) {
        //галочка автоматически поставится
        JOptionPane.showMessageDialog(this, "Есть несохраненные изменения", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      actionComboBox.addFilter(onlyVenueActionFilter);
    }
  }

  private void genresButtonActionPerformed() {
    if (genresDialog == null) genresDialog = new GenresDialog(this, posterDescTextArea, genresTableModel);
    genresDialog.setVisible(true);
  }

  private void bookletLabelActionPerformed(ActionEvent e) {
    String actionId = e.getActionCommand();
    if (actionId.isEmpty()) return;
    String url = Env.bookletUrl + actionId;
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException ex) {
      ex.printStackTrace();
    }
  }

  private void newBookletLabelActionPerformed() {
    ActionObj action = actionComboBox.getSelectedElement();
    if (action == null) return;
    FileChooser openBookletDialog = FileChoosers.getOpenBookletDialog();
    if (openBookletDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = openBookletDialog.getSelectedFile();
      if (file.length() > 5 * 1024 * 1024) {
        JOptionPane.showMessageDialog(this, "Размер файла превышает 5 Мб", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }
      try {
        BookletFileFilter fileFilter = ((BookletFileFilter) openBookletDialog.getFileFilter());
        BookletType type = BookletType.getInstance(fileFilter.getId(), fileFilter.getDesc(), fileFilter.getDescription(), fileFilter.getExtension());
        byte[] data = Files.readAllBytes(file.toPath());
        net.create("SET_ACTION_BOOKLET", new Request(new Object[]{action.getId(), type, data}), this, true).start();
      } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Ошибка чтения файла:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void controlActionEventButtonActionPerformed() {
    ActionEventObj actionEvent = actionEventComboBox.getSelectedElement();
    if (actionEvent == null) return;
    new EventControlFrame(this, actionEvent, actionEventComboBox, this).startWork();
  }

  private void findPlanLabelActionPerformed() {
    ActionEventObj actionEvent = actionEventComboBox.getSelectedElement();
    if (actionEvent == null) return;
    ActionObj action = actionComboBox.getSelectedElement();
    if (action == null) return;
    long planId = actionEvent.getPlanId();
    boolean found = false;
    for (SeatingPlanObj seatingPlanObj : planComboBox.getElementList()) {
      if (planId != seatingPlanObj.getId()) continue;
      long venueId = seatingPlanObj.getVenueId();
      for (VenueObj venueObj : venueComboBox.getElementList()) {
        if (venueId != venueObj.getId()) continue;
        long cityId = venueObj.getCityId();
        for (CityObj cityObj : cityComboBox.getElementList()) {
          if (cityId != cityObj.getId()) continue;
          cityComboBox.setSelectedItem(cityObj);
          venueComboBox.setSelectedItem(venueObj);
          planComboBox.setSelectedItem(seatingPlanObj);
          actionComboBox.setSelectedItem(action);
          actionEventComboBox.setSelectedItem(actionEvent);
          break;
        }
        break;
      }
      found = true;
      break;
    }
    if (!found) {
      JOptionPane.showMessageDialog(this, "Схемы зала больше не существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void ebsLabelActionPerformed(ActionEvent e) {
    String actionEventId = e.getActionCommand();
    if (actionEventId.isEmpty()) return;
    String random = String.valueOf(System.currentTimeMillis());
    String url = Env.planUrl + actionEventId + "&rnd=" + random;
    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException ex) {
      ex.printStackTrace();
    }
  }

  private void applyEventParamsButtonActionPerformed() {
    ActionEventObj actionEvent = actionEventComboBox.getSelectedElement();
    if (actionEvent == null) return;
    if (actionEventComboBox.isEdited()) {
      JOptionPane.showMessageDialog(this, "Необходимо сначала сохранить изменения", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (JOptionPane.showConfirmDialog(this, "Применить данные настройки ко всем сеансам представления?", "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
      net.create("SET_EVENT_COMMON_PARAMS", new Request(actionEvent), this).start();
    }
  }

  private void subsLabelActionPerformed() {
    if (subsFrame == null) subsFrame = new SubsFrame(this);
    subsFrame.startWork(cityComboBox, actionComboBox);
  }

  private void syncLabelActionPerformed() {
    if (syncFrame == null) syncFrame = new SyncFrame(this, actionEventComboBox);
    syncFrame.startWork();
  }

  private void longReserveCheckBoxItemStateChanged(ItemEvent e) {
    maxReserveTimeTextField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
  }

  private void vatCheckBoxItemStateChanged(ItemEvent e) {
    vatTextField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
  }
  //</editor-fold>

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    statusBarPanel = new StatusBarPanel();
    JPanel mainPanel = new JPanel();
    JPanel panel1 = new JPanel();
    JPanel cityPanel = new JPanel();
    JLabel label2 = new JLabel();
    cityComboBox = new OperationComboBox<>();
    addCityButton = new JButton();
    delCityButton = new JButton();
    JLabel label6 = new JLabel();
    cityTextField = new JTextField();
    saveCityButton = new JButton();
    JPanel venuePanel = new JPanel();
    JLabel label9 = new JLabel();
    venueComboBox = new OperationComboBox<>();
    addVenueButton = new JButton();
    delVenueButton = new JButton();
    saveVenueButton = new JButton();
    JPanel venueNamePanel = new JPanel();
    JLabel label10 = new JLabel();
    venueNameTextField = new JTextField();
    JLabel label11 = new JLabel();
    addressTextField = new JTextField();
    JPanel venueGeoPanel = new JPanel();
    JLabel label28 = new JLabel();
    venueTypeComboBox = new JComboBox<>();
    JLabel label12 = new JLabel();
    latTextField = new JTextField();
    JLabel label13 = new JLabel();
    lonTextField = new JTextField();
    JButton mapButton = new JButton();
    JPanel venueDescPanel = new JPanel();
    venueDescLabel = new JLabel();
    venueBigImageLabel = new PosterLabel();
    JScrollPane scrollPane2 = new JScrollPane();
    venueDescTextArea = new JTextArea();
    JPanel planPanel = new JPanel();
    JLabel label22 = new JLabel();
    planComboBox = new OperationComboBox<>();
    JButton addPlanButton = new JButton();
    delPlanButton = new JButton();
    JLabel label23 = new JLabel();
    planNameTextField = new JTextField();
    savePlanButton = new JButton();
    planDataPanel = new JPanel();
    planImagePanel = new PlanSvgPanel();
    planCatScrollPane = new JScrollPane();
    catTable = new JXTable();
    JPanel extraPanel = new JPanel();
    JXHyperlink subsLabel = new JXHyperlink();
    subsLabel.setClickedColor(subsLabel.getUnclickedColor());
    JXHyperlink syncLabel = new JXHyperlink();
    syncLabel.setClickedColor(syncLabel.getUnclickedColor());
    JPanel panel2 = new JPanel();
    JPanel kindPanel = new JPanel();
    JLabel label27 = new JLabel();
    kindComboBox = new OperationComboBox<>();
    JPanel actionPanel = new JPanel();
    JLabel label1 = new JLabel();
    actionComboBox = new OperationComboBox<>();
    JButton addActionButton = new JButton();
    delActionButton = new JButton();
    saveActionButton = new JButton();
    JPanel actionDurationPanel = new JPanel();
    JLabel label24 = new JLabel();
    actionAgeComboBox = new JComboBox<>();
    JLabel label7 = new JLabel();
    actionDurationTextField = new JFormattedTextField(new PositiveIntegerFormatter(525600));
    JLabel label15 = new JLabel();
    actualActionCheckBox = new JCheckBox();
    JCheckBox actionLinkCheckBox = new JCheckBox();
    JCheckBox noVenueCheckBox = new JCheckBox();
    JPanel actionOwnerPanel = new JPanel();
    JLabel label26 = new JLabel();
    actionOwnerNameTextField = new JTextField();
    JLabel label31 = new JLabel();
    actionOwnerInnTextField = new JTextField();
    JLabel label32 = new JLabel();
    actionOwnerPhoneTextField = new JTextField();
    JPanel actionNamePanel = new JPanel();
    JLabel label3 = new JLabel();
    actionNameTextField = new JTextField();
    genresButton = new JButton();
    JLabel label4 = new JLabel();
    actionFullNameTextField = new JTextField();
    JPanel posterPanel = new JPanel();
    JPanel panel3 = new JPanel();
    posterLabel = new JLabel();
    bookletLabel = new JXHyperlink();
    bookletLabel.setClickedColor(bookletLabel.getUnclickedColor());
    newBookletLabel = new JXHyperlink();
    newBookletLabel.setClickedColor(newBookletLabel.getUnclickedColor());
    JLabel label16 = new JLabel();
    actionRatingComboBox = new JComboBox<>();
    JLabel label25 = new JLabel();
    kdpTextField = new JFormattedTextField(new PositiveIntegerFormatter());
    JLabel label5 = new JLabel();
    chargeTextField = new JFormattedTextField(new PositiveNumberFormatter());
    JLabel label14 = new JLabel();
    JLabel label8 = new JLabel();
    posterNameTextField = new JTextField();
    JScrollPane scrollPane1 = new JScrollPane();
    posterDescTextArea = new JTextArea();
    JPanel actionImagesPanel = new JPanel();
    actionBigImageLabel = new PosterLabel();
    actionSmallImageLabel = new PosterLabel();
    JPanel actionEventPanel = new JPanel();
    JLabel label18 = new JLabel();
    actionEventComboBox = new OperationComboBox<>();
    JButton addActionEventButton = new JButton();
    delActionEventButton = new JButton();
    loadArchivalButton = new JButton();
    controlActionEventButton = new JButton();
    saveActionEventButton = new JButton();
    JLabel label17 = new JLabel();
    ebsLabel = new JXHyperlink();
    ebsLabel.setClickedColor(ebsLabel.getUnclickedColor());
    eventTabbedPane = new FixedTabbedPane();
    JPanel actionEventTimePanel = new JPanel();
    JLabel label19 = new JLabel();
    showDatePicker = new JXDateTimePicker();
    JScrollPane scrollPane3 = new JScrollPane();
    catPriceTable = new JXTable();
    JLabel label20 = new JLabel();
    sellStartDatePicker = new JXDateTimePicker();
    JLabel label21 = new JLabel();
    sellEndDatePicker = new JXDateTimePicker();
    JPanel actionEventTimePanel2 = new JPanel();
    sellEnabledCheckBox = new JCheckBox();
    JXHyperlink findPlanLabel = new JXHyperlink();
    findPlanLabel.setClickedColor(findPlanLabel.getUnclickedColor());
    JPanel actionEventExtPanel = new JPanel();
    fullNameRequiredCheckBox = new JCheckBox();
    eTicketsCheckBox = new JCheckBox();
    phoneRequiredCheckBox = new JCheckBox();
    longReserveCheckBox = new JCheckBox();
    maxReserveTimeTextField = new JFormattedTextField(new PositiveIntegerFormatter());
    JLabel label29 = new JLabel();
    ticketRefundAllowedCheckBox = new JCheckBox();
    vatCheckBox = new JCheckBox();
    vatTextField = new JFormattedTextField(new PositiveNumberFormatter());
    JLabel label30 = new JLabel();
    ticketReissueAllowedCheckBox = new JCheckBox();
    applyEventParamsButton = new JButton();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043f\u043b\u0430\u0442\u0444\u043e\u0440\u043c\u044b BIL24 \u0432\u0435\u0440\u0441\u0438\u044f ");
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        thisWindowClosing();
      }
    });
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(statusBarPanel, BorderLayout.SOUTH);

    //======== mainPanel ========
    {
      mainPanel.setBorder(new EmptyBorder(5, 5, 1, 5));
      mainPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {660, 645, 0};
      ((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {0, 0};
      ((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
      ((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

      //======== panel1 ========
      {
        panel1.setLayout(new GridBagLayout());
        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

        //======== cityPanel ========
        {
          cityPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)cityPanel.getLayout()).columnWidths = new int[] {0, 105, 0, 0, 0, 0, 0, 0};
          ((GridBagLayout)cityPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)cityPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
          ((GridBagLayout)cityPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label2 ----
          label2.setText("\u0413\u043e\u0440\u043e\u0434:");
          cityPanel.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- cityComboBox ----
          cityComboBox.setMaximumRowCount(15);
          cityPanel.add(cityComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- addCityButton ----
          addCityButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addCityButton.setMargin(new Insets(1, 1, 1, 1));
          addCityButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0433\u043e\u0440\u043e\u0434");
          addCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addCityButtonActionPerformed();
            }
          });
          cityPanel.add(addCityButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- delCityButton ----
          delCityButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          delCityButton.setMargin(new Insets(1, 1, 1, 1));
          delCityButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0433\u043e\u0440\u043e\u0434");
          delCityButton.setEnabled(false);
          delCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              delCityButtonActionPerformed();
            }
          });
          cityPanel.add(delCityButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label6 ----
          label6.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
          cityPanel.add(label6, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          cityPanel.add(cityTextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- saveCityButton ----
          saveCityButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
          saveCityButton.setMargin(new Insets(2, 2, 2, 2));
          saveCityButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
          saveCityButton.setEnabled(false);
          saveCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveCityButtonActionPerformed();
            }
          });
          cityPanel.add(saveCityButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(cityPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== venuePanel ========
        {
          venuePanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.black),
            new EmptyBorder(10, 0, 0, 0)));
          venuePanel.setLayout(new GridBagLayout());
          ((GridBagLayout)venuePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
          ((GridBagLayout)venuePanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)venuePanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)venuePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label9 ----
          label9.setText("\u041c\u0435\u0441\u0442\u043e:");
          venuePanel.add(label9, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- venueComboBox ----
          venueComboBox.setMinimumSize(new Dimension(0, 0));
          venueComboBox.setMaximumRowCount(15);
          venuePanel.add(venueComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- addVenueButton ----
          addVenueButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addVenueButton.setMargin(new Insets(1, 1, 1, 1));
          addVenueButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043c\u0435\u0441\u0442\u043e \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u044f");
          addVenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addVenueButtonActionPerformed();
            }
          });
          venuePanel.add(addVenueButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- delVenueButton ----
          delVenueButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          delVenueButton.setMargin(new Insets(1, 1, 1, 1));
          delVenueButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043c\u0435\u0441\u0442\u043e \u043f\u0440\u043e\u0432\u0435\u0434\u0435\u043d\u0438\u044f");
          delVenueButton.setEnabled(false);
          delVenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              delVenueButtonActionPerformed();
            }
          });
          venuePanel.add(delVenueButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- saveVenueButton ----
          saveVenueButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
          saveVenueButton.setMargin(new Insets(2, 2, 2, 2));
          saveVenueButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
          saveVenueButton.setEnabled(false);
          saveVenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveVenueButtonActionPerformed();
            }
          });
          venuePanel.add(saveVenueButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(venuePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(5, 0, 5, 0), 0, 0));

        //======== venueNamePanel ========
        {
          venueNamePanel.setLayout(new GridBagLayout());
          ((GridBagLayout)venueNamePanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)venueNamePanel.getLayout()).rowHeights = new int[] {0, 0, 0};
          ((GridBagLayout)venueNamePanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
          ((GridBagLayout)venueNamePanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

          //---- label10 ----
          label10.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
          venueNamePanel.add(label10, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
          venueNamePanel.add(venueNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- label11 ----
          label11.setText("\u0410\u0434\u0440\u0435\u0441:");
          venueNamePanel.add(label11, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          venueNamePanel.add(addressTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(venueNamePanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== venueGeoPanel ========
        {
          venueGeoPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)venueGeoPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
          ((GridBagLayout)venueGeoPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)venueGeoPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)venueGeoPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label28 ----
          label28.setText("\u0422\u0438\u043f:");
          venueGeoPanel.add(label28, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          venueGeoPanel.add(venueTypeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label12 ----
          label12.setText("\u0428\u0438\u0440\u043e\u0442\u0430:");
          venueGeoPanel.add(label12, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- latTextField ----
          latTextField.setColumns(8);
          latTextField.setMinimumSize(new Dimension(30, 10));
          venueGeoPanel.add(latTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label13 ----
          label13.setText("\u0414\u043e\u043b\u0433\u043e\u0442\u0430:");
          venueGeoPanel.add(label13, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- lonTextField ----
          lonTextField.setColumns(8);
          lonTextField.setMinimumSize(new Dimension(30, 10));
          venueGeoPanel.add(lonTextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- mapButton ----
          mapButton.setIcon(new ImageIcon(getClass().getResource("/resources/map.png")));
          mapButton.setMargin(new Insets(1, 1, 1, 1));
          mapButton.setToolTipText("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043d\u0430 \u043a\u0430\u0440\u0442\u0435");
          mapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              mapButtonActionPerformed();
            }
          });
          venueGeoPanel.add(mapButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(venueGeoPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== venueDescPanel ========
        {
          venueDescPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)venueDescPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)venueDescPanel.getLayout()).rowHeights = new int[] {0, 100, 0};
          ((GridBagLayout)venueDescPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
          ((GridBagLayout)venueDescPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

          //---- venueDescLabel ----
          venueDescLabel.setText("\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435:");
          venueDescPanel.add(venueDescLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- venueBigImageLabel ----
          venueBigImageLabel.setText("<html><center>\u0431\u043e\u043b\u044c\u0448\u043e\u0439 \u043f\u043e\u0441\u0442\u0435\u0440<br>640x670<br><br><small>\u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u0435 \u0444\u0430\u0439\u043b<br>\u0438\u043b\u0438<br>\u043d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c</small></center></html>");
          venueBigImageLabel.setBorder(LineBorder.createBlackLineBorder());
          venueBigImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
          venueBigImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              imageLabelMouseClicked(e);
            }
          });
          venueDescPanel.add(venueBigImageLabel, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

          //======== scrollPane2 ========
          {
            scrollPane2.setPreferredSize(new Dimension(22, 22));

            //---- venueDescTextArea ----
            venueDescTextArea.setMargin(new Insets(1, 3, 1, 3));
            venueDescTextArea.setLineWrap(true);
            venueDescTextArea.setWrapStyleWord(true);
            venueDescTextArea.setFont(UIManager.getFont("TextField.font"));
            scrollPane2.setViewportView(venueDescTextArea);
          }
          venueDescPanel.add(scrollPane2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
        }
        panel1.add(venueDescPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== planPanel ========
        {
          planPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.black),
            new EmptyBorder(10, 0, 0, 0)));
          planPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)planPanel.getLayout()).columnWidths = new int[] {0, 105, 0, 0, 0, 0, 0, 0};
          ((GridBagLayout)planPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)planPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
          ((GridBagLayout)planPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label22 ----
          label22.setText("\u0421\u0445\u0435\u043c\u0430 \u0437\u0430\u043b\u0430:");
          planPanel.add(label22, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- planComboBox ----
          planComboBox.setMaximumRowCount(15);
          planPanel.add(planComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- addPlanButton ----
          addPlanButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addPlanButton.setMargin(new Insets(1, 1, 1, 1));
          addPlanButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0445\u0435\u043c\u0443 \u0437\u0430\u043b\u0430");
          addPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addPlanButtonActionPerformed();
            }
          });
          planPanel.add(addPlanButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- delPlanButton ----
          delPlanButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          delPlanButton.setMargin(new Insets(1, 1, 1, 1));
          delPlanButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0445\u0435\u043c\u0443 \u0437\u0430\u043b\u0430");
          delPlanButton.setEnabled(false);
          delPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              delPlanButtonActionPerformed();
            }
          });
          planPanel.add(delPlanButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label23 ----
          label23.setText("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
          planPanel.add(label23, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          planPanel.add(planNameTextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- savePlanButton ----
          savePlanButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
          savePlanButton.setMargin(new Insets(2, 2, 2, 2));
          savePlanButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
          savePlanButton.setEnabled(false);
          savePlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              savePlanButtonActionPerformed();
            }
          });
          planPanel.add(savePlanButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(planPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== planDataPanel ========
        {
          planDataPanel.setLayout(new BorderLayout());

          //---- planImagePanel ----
          planImagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          planImagePanel.setBorder(new EmptyBorder(0, 0, 5, 0));
          planImagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              planImagePanelMouseClicked(e);
            }
          });
          planDataPanel.add(planImagePanel, BorderLayout.CENTER);

          //======== planCatScrollPane ========
          {
            planCatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            planCatScrollPane.setPreferredSize(new Dimension(246, 75));
            planCatScrollPane.setMinimumSize(new Dimension(246, 75));

            //---- catTable ----
            catTable.setPreferredScrollableViewportSize(new Dimension(0, 10));
            catTable.setAutoCreateRowSorter(false);
            catTable.setSortable(false);
            planCatScrollPane.setViewportView(catTable);
          }
          planDataPanel.add(planCatScrollPane, BorderLayout.SOUTH);
        }
        panel1.add(planDataPanel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== extraPanel ========
        {
          extraPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)extraPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)extraPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)extraPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
          ((GridBagLayout)extraPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- subsLabel ----
          subsLabel.setText("\u041f\u043e\u0434\u043f\u0438\u0441\u043a\u0438");
          subsLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              subsLabelActionPerformed();
            }
          });
          extraPanel.add(subsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 8), 0, 0));

          //---- syncLabel ----
          syncLabel.setText("\u0421\u0438\u043d\u0445. \u0441 \u0412\u0411\u0421");
          syncLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              syncLabelActionPerformed();
            }
          });
          extraPanel.add(syncLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel1.add(extraPanel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 15), 0, 0));

      //======== panel2 ========
      {
        panel2.setLayout(new GridBagLayout());
        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};

        //======== kindPanel ========
        {
          kindPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)kindPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)kindPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)kindPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
          ((GridBagLayout)kindPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label27 ----
          label27.setText("\u0420\u0430\u0437\u0434\u0435\u043b:");
          kindPanel.add(label27, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- kindComboBox ----
          kindComboBox.setSuggest(false);
          kindPanel.add(kindComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(kindPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== actionPanel ========
        {
          actionPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.black),
            new EmptyBorder(10, 0, 0, 0)));
          actionPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
          ((GridBagLayout)actionPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)actionPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label1 ----
          label1.setText("\u041f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435:");
          actionPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- actionComboBox ----
          actionComboBox.setMinimumSize(new Dimension(0, 0));
          actionComboBox.setMaximumRowCount(15);
          actionPanel.add(actionComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- addActionButton ----
          addActionButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addActionButton.setMargin(new Insets(1, 1, 1, 1));
          addActionButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435");
          addActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addActionButtonActionPerformed();
            }
          });
          actionPanel.add(addActionButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- delActionButton ----
          delActionButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          delActionButton.setMargin(new Insets(1, 1, 1, 1));
          delActionButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u0435");
          delActionButton.setEnabled(false);
          delActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              delActionButtonActionPerformed();
            }
          });
          actionPanel.add(delActionButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- saveActionButton ----
          saveActionButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
          saveActionButton.setMargin(new Insets(2, 2, 2, 2));
          saveActionButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
          saveActionButton.setEnabled(false);
          saveActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveActionButtonActionPerformed();
            }
          });
          actionPanel.add(saveActionButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(5, 0, 5, 0), 0, 0));

        //======== actionDurationPanel ========
        {
          actionDurationPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionDurationPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 55, 0, 0, 0, 0, 0};
          ((GridBagLayout)actionDurationPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)actionDurationPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionDurationPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

          //---- label24 ----
          label24.setText("\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435:");
          actionDurationPanel.add(label24, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionDurationPanel.add(actionAgeComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label7 ----
          label7.setText("\u041f\u0440\u043e\u0434\u043e\u043b-\u0442\u044c:");
          actionDurationPanel.add(label7, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionDurationPanel.add(actionDurationTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label15 ----
          label15.setText("\u043c\u0438\u043d.");
          actionDurationPanel.add(label15, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- actualActionCheckBox ----
          actualActionCheckBox.setText("\u0410\u043a\u0442\u0443\u0430\u043b\u044c\u043d\u044b\u0435");
          actualActionCheckBox.setFont(actualActionCheckBox.getFont().deriveFont(actualActionCheckBox.getFont().getStyle() & ~Font.BOLD, actualActionCheckBox.getFont().getSize() - 2f));
          actualActionCheckBox.setSelected(true);
          actualActionCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              actualActionCheckBoxItemStateChanged(e);
            }
          });
          actionDurationPanel.add(actualActionCheckBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- actionLinkCheckBox ----
          actionLinkCheckBox.setText("\u0424\u0438\u043b\u044c\u0442\u0440 \u043f\u043e \u043c\u0435\u0441\u0442\u0443");
          actionLinkCheckBox.setFont(actionLinkCheckBox.getFont().deriveFont(actionLinkCheckBox.getFont().getStyle() & ~Font.BOLD, actionLinkCheckBox.getFont().getSize() - 2f));
          actionLinkCheckBox.setSelected(true);
          actionLinkCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              actionLinkCheckBoxItemStateChanged(e);
            }
          });
          actionDurationPanel.add(actionLinkCheckBox, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- noVenueCheckBox ----
          noVenueCheckBox.setText("\u0411\u0435\u0437 \u0441\u0435\u0430\u043d\u0441\u043e\u0432");
          noVenueCheckBox.setSelected(true);
          noVenueCheckBox.setFont(noVenueCheckBox.getFont().deriveFont(noVenueCheckBox.getFont().getStyle() & ~Font.BOLD, noVenueCheckBox.getFont().getSize() - 2f));
          noVenueCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
              noVenueCheckBoxItemStateChanged(e);
            }
          });
          actionDurationPanel.add(noVenueCheckBox, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionDurationPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== actionOwnerPanel ========
        {
          actionOwnerPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionOwnerPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 105, 0, 100, 0};
          ((GridBagLayout)actionOwnerPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)actionOwnerPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionOwnerPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- label26 ----
          label26.setText("\u0423\u0441\u0442\u0440\u043e\u0438\u0442\u0435\u043b\u044c:");
          actionOwnerPanel.add(label26, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionOwnerPanel.add(actionOwnerNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label31 ----
          label31.setText("\u0418\u041d\u041d:");
          actionOwnerPanel.add(label31, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionOwnerPanel.add(actionOwnerInnTextField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- label32 ----
          label32.setText("\u0422\u0435\u043b.");
          actionOwnerPanel.add(label32, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionOwnerPanel.add(actionOwnerPhoneTextField, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionOwnerPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== actionNamePanel ========
        {
          actionNamePanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionNamePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
          ((GridBagLayout)actionNamePanel.getLayout()).rowHeights = new int[] {0, 0, 0};
          ((GridBagLayout)actionNamePanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionNamePanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

          //---- label3 ----
          label3.setText("\u041a\u0440\u0430\u0442\u043a\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
          actionNamePanel.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
          actionNamePanel.add(actionNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- genresButton ----
          genresButton.setText("\u0416\u0430\u043d\u0440\u044b");
          genresButton.setFont(genresButton.getFont().deriveFont(genresButton.getFont().getStyle() & ~Font.BOLD, genresButton.getFont().getSize() - 2f));
          genresButton.setMargin(new Insets(0, 4, 0, 4));
          genresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              genresButtonActionPerformed();
            }
          });
          actionNamePanel.add(genresButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- label4 ----
          label4.setText("\u041f\u043e\u043b\u043d\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435:");
          actionNamePanel.add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));
          actionNamePanel.add(actionFullNameTextField, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionNamePanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== posterPanel ========
        {
          posterPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)posterPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)posterPanel.getLayout()).rowHeights = new int[] {0, 0, 100, 0};
          ((GridBagLayout)posterPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
          ((GridBagLayout)posterPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};

          //======== panel3 ========
          {
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- posterLabel ----
            posterLabel.setText("\u0411\u0443\u043a\u043b\u0435\u0442:");
            panel3.add(posterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- bookletLabel ----
            bookletLabel.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                bookletLabelActionPerformed(e);
              }
            });
            panel3.add(bookletLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- newBookletLabel ----
            newBookletLabel.setText("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c...");
            newBookletLabel.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                newBookletLabelActionPerformed();
              }
            });
            panel3.add(newBookletLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- label16 ----
            label16.setText("\u0420\u0435\u0439\u0442\u0438\u043d\u0433:");
            label16.setHorizontalAlignment(SwingConstants.RIGHT);
            panel3.add(label16, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- actionRatingComboBox ----
            actionRatingComboBox.setMaximumRowCount(11);
            panel3.add(actionRatingComboBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- label25 ----
            label25.setText("\u041a\u0414\u041f:");
            panel3.add(label25, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- kdpTextField ----
            kdpTextField.setToolTipText("\u041a\u043e\u0434 \u0434\u043e\u0441\u0442\u0443\u043f\u0430 \u043a \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u044e (\u043d\u0435 \u043e\u0431\u044f\u0437\u0430\u0442\u0435\u043b\u044c\u043d\u043e)");
            kdpTextField.setColumns(5);
            panel3.add(kdpTextField, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- label5 ----
            label5.setText("\u041c\u0438\u043d. CC:");
            panel3.add(label5, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- chargeTextField ----
            chargeTextField.setColumns(5);
            panel3.add(chargeTextField, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- label14 ----
            label14.setText("%");
            panel3.add(label14, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 0), 0, 0));
          }
          posterPanel.add(panel3, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- label8 ----
          label8.setText("\u0417\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a:");
          posterPanel.add(label8, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
          posterPanel.add(posterNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //======== scrollPane1 ========
          {
            scrollPane1.setPreferredSize(new Dimension(22, 22));

            //---- posterDescTextArea ----
            posterDescTextArea.setLineWrap(true);
            posterDescTextArea.setWrapStyleWord(true);
            posterDescTextArea.setMargin(new Insets(1, 3, 1, 3));
            posterDescTextArea.setFont(UIManager.getFont("TextField.font"));
            scrollPane1.setViewportView(posterDescTextArea);
          }
          posterPanel.add(scrollPane1, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(posterPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== actionImagesPanel ========
        {
          actionImagesPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionImagesPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
          ((GridBagLayout)actionImagesPanel.getLayout()).rowHeights = new int[] {0, 0};
          ((GridBagLayout)actionImagesPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionImagesPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

          //---- actionBigImageLabel ----
          actionBigImageLabel.setText("<html><center>\u0431\u043e\u043b\u044c\u0448\u043e\u0439 \u043f\u043e\u0441\u0442\u0435\u0440<br>640x670<br><br><small>\u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u0435 \u0444\u0430\u0439\u043b<br>\u0438\u043b\u0438<br>\u043d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c</small></center></html>");
          actionBigImageLabel.setBorder(LineBorder.createBlackLineBorder());
          actionBigImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
          actionBigImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          actionBigImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              imageLabelMouseClicked(e);
            }
          });
          actionImagesPanel.add(actionBigImageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- actionSmallImageLabel ----
          actionSmallImageLabel.setText("<html><center>\u043c\u0430\u043b\u044b\u0439 \u043f\u043e\u0441\u0442\u0435\u0440<br>320x335<br><br><small>\u043f\u0435\u0440\u0435\u0442\u0430\u0449\u0438\u0442\u0435 \u0444\u0430\u0439\u043b<br>\u0438\u043b\u0438<br>\u043d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c</small></center></html>");
          actionSmallImageLabel.setBorder(LineBorder.createBlackLineBorder());
          actionSmallImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
          actionSmallImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          actionSmallImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              imageLabelMouseClicked(e);
            }
          });
          actionImagesPanel.add(actionSmallImageLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionImagesPanel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
          new Insets(0, 0, 5, 0), 0, 0));

        //======== actionEventPanel ========
        {
          actionEventPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, Color.black),
            new EmptyBorder(10, 0, 0, 0)));
          actionEventPanel.setLayout(new GridBagLayout());
          ((GridBagLayout)actionEventPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
          ((GridBagLayout)actionEventPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
          ((GridBagLayout)actionEventPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
          ((GridBagLayout)actionEventPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

          //---- label18 ----
          label18.setText("\u0421\u0435\u0430\u043d\u0441:");
          actionEventPanel.add(label18, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- actionEventComboBox ----
          actionEventComboBox.setMinimumSize(new Dimension(0, 0));
          actionEventPanel.add(actionEventComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- addActionEventButton ----
          addActionEventButton.setIcon(new ImageIcon(getClass().getResource("/resources/plus.png")));
          addActionEventButton.setMargin(new Insets(1, 1, 1, 1));
          addActionEventButton.setToolTipText("\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0435\u0430\u043d\u0441\u044b");
          addActionEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              addActionEventButtonActionPerformed();
            }
          });
          actionEventPanel.add(addActionEventButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- delActionEventButton ----
          delActionEventButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
          delActionEventButton.setMargin(new Insets(1, 1, 1, 1));
          delActionEventButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0435\u0430\u043d\u0441, \u0435\u0441\u043b\u0438 \u043d\u0435\u0442 \u043f\u0440\u043e\u0434\u0430\u0436");
          delActionEventButton.setEnabled(false);
          delActionEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              delActionEventButtonActionPerformed();
            }
          });
          actionEventPanel.add(delActionEventButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- loadArchivalButton ----
          loadArchivalButton.setIcon(new ImageIcon(getClass().getResource("/resources/down.png")));
          loadArchivalButton.setMargin(new Insets(1, 1, 1, 1));
          loadArchivalButton.setToolTipText("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0430\u0440\u0445\u0438\u0432\u043d\u044b\u0435 \u0441\u0435\u0430\u043d\u0441\u044b");
          loadArchivalButton.setEnabled(false);
          loadArchivalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              loadArchivalButtonActionPerformed();
            }
          });
          actionEventPanel.add(loadArchivalButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- controlActionEventButton ----
          controlActionEventButton.setMargin(new Insets(1, 1, 1, 1));
          controlActionEventButton.setToolTipText("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0435\u0441\u0442\u0430\u043c\u0438");
          controlActionEventButton.setIcon(new ImageIcon(getClass().getResource("/resources/control.png")));
          controlActionEventButton.setEnabled(false);
          controlActionEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              controlActionEventButtonActionPerformed();
            }
          });
          actionEventPanel.add(controlActionEventButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

          //---- saveActionEventButton ----
          saveActionEventButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
          saveActionEventButton.setMargin(new Insets(2, 2, 2, 2));
          saveActionEventButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
          saveActionEventButton.setEnabled(false);
          saveActionEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              saveActionEventButtonActionPerformed();
            }
          });
          actionEventPanel.add(saveActionEventButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

          //---- label17 ----
          label17.setText("\u0412\u0411\u0421:");
          label17.setHorizontalAlignment(SwingConstants.RIGHT);
          actionEventPanel.add(label17, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

          //---- ebsLabel ----
          ebsLabel.setMinimumSize(new Dimension(0, 0));
          ebsLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              ebsLabelActionPerformed(e);
            }
          });
          actionEventPanel.add(ebsLabel, new GridBagConstraints(1, 1, 6, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        }
        panel2.add(actionEventPanel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(5, 0, 5, 0), 0, 0));

        //======== eventTabbedPane ========
        {
          eventTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
          eventTabbedPane.setTabPlacement(SwingConstants.BOTTOM);

          //======== actionEventTimePanel ========
          {
            actionEventTimePanel.setBorder(new EmptyBorder(0, 0, 5, 0));
            actionEventTimePanel.setLayout(new GridBagLayout());
            ((GridBagLayout)actionEventTimePanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout)actionEventTimePanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout)actionEventTimePanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout)actionEventTimePanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- label19 ----
            label19.setText("\u041d\u0430\u0447\u0430\u043b\u043e \u0441\u0435\u0430\u043d\u0441\u0430:");
            actionEventTimePanel.add(label19, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));
            actionEventTimePanel.add(showDatePicker, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));

            //======== scrollPane3 ========
            {
              scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

              //---- catPriceTable ----
              catPriceTable.setPreferredScrollableViewportSize(new Dimension(0, 10));
              scrollPane3.setViewportView(catPriceTable);
            }
            actionEventTimePanel.add(scrollPane3, new GridBagConstraints(2, 0, 1, 4, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 0), 0, 0));

            //---- label20 ----
            label20.setText("\u041d\u0430\u0447\u0430\u043b\u043e \u043f\u0440\u043e\u0434\u0430\u0436:");
            actionEventTimePanel.add(label20, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));
            actionEventTimePanel.add(sellStartDatePicker, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- label21 ----
            label21.setText("\u041a\u043e\u043d\u0435\u0446 \u043f\u0440\u043e\u0434\u0430\u0436:");
            actionEventTimePanel.add(label21, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));
            actionEventTimePanel.add(sellEndDatePicker, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));

            //======== actionEventTimePanel2 ========
            {
              actionEventTimePanel2.setLayout(new GridBagLayout());
              ((GridBagLayout)actionEventTimePanel2.getLayout()).columnWidths = new int[] {0, 0, 0};
              ((GridBagLayout)actionEventTimePanel2.getLayout()).rowHeights = new int[] {0, 0};
              ((GridBagLayout)actionEventTimePanel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
              ((GridBagLayout)actionEventTimePanel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

              //---- sellEnabledCheckBox ----
              sellEnabledCheckBox.setText("\u041f\u0440\u043e\u0434\u0430\u0436\u0430 \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d\u0430");
              actionEventTimePanel2.add(sellEnabledCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

              //---- findPlanLabel ----
              findPlanLabel.setText("\u041d\u0430\u0439\u0442\u0438 \u0441\u0445\u0435\u043c\u0443");
              findPlanLabel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  findPlanLabelActionPerformed();
                }
              });
              actionEventTimePanel2.add(findPlanLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
            }
            actionEventTimePanel.add(actionEventTimePanel2, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 0, 5), 0, 0));
          }
          eventTabbedPane.addTab("\u041e\u0441\u043d\u043e\u0432\u043d\u044b\u0435", actionEventTimePanel);

          //======== actionEventExtPanel ========
          {
            actionEventExtPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
            actionEventExtPanel.setLayout(new GridBagLayout());
            ((GridBagLayout)actionEventExtPanel.getLayout()).columnWidths = new int[] {0, 0, 55, 0, 0};
            ((GridBagLayout)actionEventExtPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout)actionEventExtPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout)actionEventExtPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- fullNameRequiredCheckBox ----
            fullNameRequiredCheckBox.setText("\u0424\u0418 \u043d\u0430 \u0431\u0438\u043b\u0435\u0442\u0435 \u043e\u0431\u044f\u0437\u0430\u0442\u0435\u043b\u044c\u043d\u044b");
            actionEventExtPanel.add(fullNameRequiredCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- eTicketsCheckBox ----
            eTicketsCheckBox.setText("\u041c\u043e\u0431\u0438\u043b\u044c\u043d\u044b\u0439 \u044d\u043b\u0435\u043a\u0442\u0440\u043e\u043d\u043d\u044b\u0439 \u0431\u0438\u043b\u0435\u0442");
            actionEventExtPanel.add(eTicketsCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- phoneRequiredCheckBox ----
            phoneRequiredCheckBox.setText("\u041d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430 \u043e\u0431\u044f\u0437\u0430\u0442\u0435\u043b\u0435\u043d");
            actionEventExtPanel.add(phoneRequiredCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- longReserveCheckBox ----
            longReserveCheckBox.setText("\u0414\u043e\u043b\u0433\u043e\u0432\u0440\u0435\u043c\u0435\u043d\u043d\u043e\u0435 \u0431\u0440\u043e\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435:");
            longReserveCheckBox.addItemListener(new ItemListener() {
              @Override
              public void itemStateChanged(ItemEvent e) {
                longReserveCheckBoxItemStateChanged(e);
              }
            });
            actionEventExtPanel.add(longReserveCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- maxReserveTimeTextField ----
            maxReserveTimeTextField.setToolTipText("\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0432\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0432 \u043c\u0438\u043d\u0443\u0442\u0430\u0445");
            maxReserveTimeTextField.setEnabled(false);
            actionEventExtPanel.add(maxReserveTimeTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- label29 ----
            label29.setText("\u043c\u0438\u043d.");
            actionEventExtPanel.add(label29, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 0), 0, 0));

            //---- ticketRefundAllowedCheckBox ----
            ticketRefundAllowedCheckBox.setText("\u0412\u043e\u0437\u0432\u0440\u0430\u0442 \u0431\u0438\u043b\u0435\u0442\u0430 \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d");
            actionEventExtPanel.add(ticketRefundAllowedCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- vatCheckBox ----
            vatCheckBox.setText("\u041d\u0414\u0421");
            vatCheckBox.addItemListener(new ItemListener() {
              @Override
              public void itemStateChanged(ItemEvent e) {
                vatCheckBoxItemStateChanged(e);
              }
            });
            actionEventExtPanel.add(vatCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- vatTextField ----
            vatTextField.setToolTipText("\u041d\u0414\u0421");
            vatTextField.setEnabled(false);
            actionEventExtPanel.add(vatTextField, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 5), 0, 0));

            //---- label30 ----
            label30.setText("%");
            actionEventExtPanel.add(label30, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
              GridBagConstraints.CENTER, GridBagConstraints.BOTH,
              new Insets(0, 0, 5, 0), 0, 0));

            //---- ticketReissueAllowedCheckBox ----
            ticketReissueAllowedCheckBox.setText("\u041f\u0435\u0440\u0435\u043f\u0435\u0447\u0430\u0442\u044c \u0440\u0430\u0437\u0440\u0435\u0448\u0435\u043d\u0430");
            actionEventExtPanel.add(ticketReissueAllowedCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
              new Insets(0, 0, 0, 5), 0, 0));

            //---- applyEventParamsButton ----
            applyEventParamsButton.setText("\u041f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u043e \u0432\u0441\u0435\u043c \u0441\u0435\u0430\u043d\u0441\u0430\u043c");
            applyEventParamsButton.setMargin(new Insets(1, 5, 1, 5));
            applyEventParamsButton.setFont(applyEventParamsButton.getFont().deriveFont(applyEventParamsButton.getFont().getStyle() & ~Font.BOLD, applyEventParamsButton.getFont().getSize() - 1f));
            applyEventParamsButton.setEnabled(false);
            applyEventParamsButton.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                applyEventParamsButtonActionPerformed();
              }
            });
            actionEventExtPanel.add(applyEventParamsButton, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0,
              GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
              new Insets(0, 0, 0, 0), 0, 0));
          }
          eventTabbedPane.addTab("\u0420\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u043d\u044b\u0435", actionEventExtPanel);
        }
        panel2.add(eventTabbedPane, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(panel2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(mainPanel, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private void loadImage(File file, PosterData posterData) {
    try {
      BufferedImage image = ImageIO.read(file);
      if (posterData == PosterData.ACTION_BIG || posterData == PosterData.VENUE_BIG) {
        if (image.getWidth() != 640 || image.getHeight() != 670) {
          JOptionPane.showMessageDialog(this, "Изображение должно быть размером 640x670", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      if (posterData == PosterData.ACTION_SMALL) {
        if (image.getWidth() != 320 || image.getHeight() != 335) {
          JOptionPane.showMessageDialog(this, "Изображение должно быть размером 320x335", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      // Recreate the BufferedImage to fix channel issues
      BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
      newImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
      image.flush();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(newImage, "jpg", baos);//конвертируем в jpg
      baos.flush();
      posterData.setType("jpg");
      posterData.setImg(baos.toByteArray());
      byte[] icon = posterData.getImg();
      switch (posterData) {
        case ACTION_BIG:
          actionBigImageLabel.setPoster(icon);
          actionComboBox.markEdited();
          break;
        case ACTION_SMALL:
          actionSmallImageLabel.setPoster(icon);
          actionComboBox.markEdited();
          break;
        case VENUE_BIG:
          venueBigImageLabel.setPoster(icon);
          venueComboBox.markEdited();
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Не удалось загрузить изображение", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean checkGeo(boolean alert) {
    String latStr = latTextField.getText();
    String lonStr = lonTextField.getText();
    if (latStr.isEmpty() && lonStr.isEmpty()) return false;

    BigDecimal lat = null;
    try {
      lat = new BigDecimal(latStr);
    } catch (Exception ignored) {
    }
    if (lat == null || lat.compareTo(Utils.minLat) < 0 || lat.compareTo(Utils.maxLat) > 0) {
      if (alert) {
        latTextField.requestFocus();
        JOptionPane.showMessageDialog(this, "Широта задана неверно", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      return false;
    }

    BigDecimal lon = null;
    try {
      lon = new BigDecimal(lonStr);
    } catch (Exception ignored) {
    }
    if (lon == null || lon.compareTo(Utils.minLon) < 0 || lon.compareTo(Utils.maxLon) > 0) {
      if (alert) {
        lonTextField.requestFocus();
        JOptionPane.showMessageDialog(this, "Долгота задана неверно", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      return false;
    }
    return true;
  }

  @Override
  public void updateChildless() {
    Set<Long> uniSet = new HashSet<>();
    List<CityObj> cityList = cityComboBox.getElementList();
    List<VenueObj> venueList = venueComboBox.getElementList();
    List<SeatingPlanObj> planList = planComboBox.getElementList();
    List<ActionObj> actionList = actionComboBox.getElementList();
    List<ActionEventObj> actionEventList = actionEventComboBox.getElementList();

    for (VenueObj venueObj : venueList) uniSet.add(venueObj.getCityId());
    for (CityObj cityObj : cityList) {
      cityObj.setChildless(!uniSet.contains(cityObj.getId()));
    }
    uniSet.clear();
    for (SeatingPlanObj planObj : planList) uniSet.add(planObj.getVenueId());
    for (VenueObj venueObj : venueList) {
      venueObj.setChildless(!uniSet.contains(venueObj.getId()));
    }
    uniSet.clear();
    for (ActionEventObj actionEventObj : actionEventList) uniSet.add(actionEventObj.getPlanId());
    for (SeatingPlanObj planObj : planList) {
      planObj.setChildless(!uniSet.contains(planObj.getId()));
    }
    uniSet.clear();
    for (ActionEventObj actionEventObj : actionEventList) uniSet.add(actionEventObj.getActionId());
    for (ActionObj actionObj : actionList) {
      actionObj.setChildless(!uniSet.contains(actionObj.getId()));
    }

    CityObj cityObj = cityComboBox.getSelectedElement();
    if (cityObj != null) delCityButton.setEnabled(cityObj.isChildless());
    VenueObj venueObj = venueComboBox.getSelectedElement();
    if (venueObj != null) delVenueButton.setEnabled(venueObj.isChildless());
    ActionObj actionObj = actionComboBox.getSelectedElement();
    if (actionObj != null) delActionButton.setEnabled(actionObj.isChildless());
  }

  @Override
  public void syncComplete(@NotNull ActionEventSyncEvent event) {
    if (syncFrame != null) syncFrame.syncComplete(event);
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

    if (result.getCommand().equals("GET_INIT_EDITOR")) {
      Object[] data = (Object[]) result.getResponse().getData();
      loadData(data);
    } else if (result.getCommand().equals("GET_ALL_ACTION_LIST")) {
      List<ActionObj> actionList = (List<ActionObj>) result.getResponse().getData();
      actionComboBox.setElementList(actionList);
      System.out.println("Загружено " + L10n.pluralVal(actionList.size(), "представление", "представления", "представлений"));
      allActionsLoaded = true;
      updateChildless();
    } else if (result.getCommand().equals("GET_ARCHIVAL_EVENT_LIST")) {
      long actionId = (Long) result.getRequest().getData();
      List<ActionEventObj> archivalEventList = (List<ActionEventObj>) result.getResponse().getData();
      List<ActionEventObj> eventList = actionEventComboBox.getElementList();
      ArrayList<ActionEventObj> newEventList = new ArrayList<>(archivalEventList.size() + eventList.size());
      newEventList.addAll(eventList);
      newEventList.addAll(archivalEventList);
      actionEventComboBox.setElementList(newEventList);
      Env.loadedArchivalActionIdSet.add(actionId);
      ActionObj action = actionComboBox.getSelectedElement();
      if (action != null && action.getId() == actionId) actionComboBox.reload();
      updateChildless();
    } else if (result.getCommand().equals("SET_ACTION_BOOKLET")) {
      Object[] request = (Object[]) result.getRequest().getData();
      long actionId = (Long) request[0];
      BookletType type = (BookletType) request[1];
      for (ActionObj actionObj : actionComboBox.getElementList()) {
        if (actionObj.getId() == actionId) {
          actionObj.setBookletType(type);
          if (actionObj.equals(actionComboBox.getSelectedElement())) actionComboBox.reload();
          break;
        }
      }
    } else if (result.getCommand().equals("SET_EVENT_COMMON_PARAMS")) {
      ActionEventObj srcActionEvent = (ActionEventObj) result.getRequest().getData();
      for (ActionEventObj actionEventObj : actionEventComboBox.getElementList()) {
        if (srcActionEvent.getActionId() == actionEventObj.getActionId()) {
          actionEventObj.setETickets(srcActionEvent.isETickets());
          actionEventObj.setFullNameRequired(srcActionEvent.isFullNameRequired());
          actionEventObj.setPhoneRequired(srcActionEvent.isPhoneRequired());
          actionEventObj.setTicketRefundAllowed(srcActionEvent.isTicketRefundAllowed());
          actionEventObj.setTicketReissueAllowed(srcActionEvent.isTicketReissueAllowed());
          actionEventObj.setMaxReserveTime(srcActionEvent.getMaxReserveTime());
          actionEventObj.setVat(srcActionEvent.getVat());
        }
      }
    } else if (result.getCommand().startsWith("SAVE")) {
      saveResult = true;
      waitingDialog.setVisible(false);
    } else if (result.getCommand().equals("DEL_CITY")) {
      if ((Boolean) result.getResponse().getData()) {
        long cityId = (Long) result.getRequest().getData();
        for (CityObj cityObj : cityComboBox.getElementList()) {
          if (cityObj.getId() == cityId) {
            cityComboBox.removeElement(cityObj);
            break;
          }
        }
      } else {
        JOptionPane.showMessageDialog(this, "Невозможно удалить данный город", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    } else if (result.getCommand().equals("DEL_VENUE")) {
      if ((Boolean) result.getResponse().getData()) {
        long venueId = (Long) result.getRequest().getData();
        for (VenueObj venueObj : venueComboBox.getElementList()) {
          if (venueObj.getId() == venueId) {
            venueComboBox.removeElement(venueObj);
            break;
          }
        }
        updateChildless();
      } else {
        JOptionPane.showMessageDialog(this, "Невозможно удалить данное место проведения", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    } else if (result.getCommand().equals("DEL_PLAN")) {
      if ((Boolean) result.getResponse().getData()) {
        long planId = (Long) result.getRequest().getData();
        for (SeatingPlanObj planObj : planComboBox.getElementList()) {
          if (planObj.getId() == planId) {
            planComboBox.removeElement(planObj);
            break;
          }
        }
        updateChildless();
      } else {
        JOptionPane.showMessageDialog(this, "Невозможно удалить данную схему зала", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    } else if (result.getCommand().equals("DEL_ACTION")) {
      if ((Boolean) result.getResponse().getData()) {
        long actionId = (Long) result.getRequest().getData();
        for (ActionObj actionObj : actionComboBox.getElementList()) {
          if (actionObj.getId() == actionId) {
            actionComboBox.removeElement(actionObj);
            break;
          }
        }
        updateChildless();
      } else {
        JOptionPane.showMessageDialog(this, "Невозможно удалить данное представление", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    } else if (result.getCommand().equals("DEL_ACTION_EVENT")) {
      if ((Boolean) result.getResponse().getData()) {
        long actionEventId = (Long) result.getRequest().getData();
        for (ActionEventObj actionEventObj : actionEventComboBox.getElementList()) {
          if (actionEventObj.getId() == actionEventId) {
            actionEventComboBox.removeElement(actionEventObj);
            break;
          }
        }
        updateChildless();
      } else {
        JOptionPane.showMessageDialog(this, "Невозможно удалить данный сеанс", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response > error) {
    if (error.getCommand().startsWith("GET")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().startsWith("SAVE")) {
      saveResult = false;
      waitingDialog.setVisible(false);
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось сохранить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().startsWith("DEL")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось удалить элемент", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    if (error.getCommand().equals("SET_ACTION_BOOKLET")) {
      JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  //<editor-fold desc="OperationListeners">
  private class CityOperationListener implements OperationListener<CityObj> {
    @Override
    public void clear() {
      cityTextField.setText("");
      delCityButton.setEnabled(false);
    }

    @Override
    public boolean check() {
      if (cityTextField.getText().trim().isEmpty()) {
        cityTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Название города не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    @Override
    public void load(@NotNull CityObj city) {
      cityTextField.setText(city.getName());
      delCityButton.setEnabled(city.isChildless());
    }

    @Override
    public boolean save(@NotNull CityObj city) {
      city.setName(cityTextField.getText().trim());
      Network<Request, Response> network = net.create("SAVE_CITY", new Request(city), MainFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }

  private class VenueOperationListener implements OperationListener<VenueObj> {
    @Override
    public void clear() {
      venueNameTextField.setText("");
      addressTextField.setText("");
      venueTypeComboBox.setSelectedIndex(-1);
      latTextField.setText("");
      lonTextField.setText("");
      venueDescTextArea.setText("");
      venueBigImageLabel.setPoster(null);
      PosterData.VENUE_BIG.setImg(null);
      PosterData.VENUE_BIG.setType(null);
      delVenueButton.setEnabled(false);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean check() {
      if (venueNameTextField.getText().trim().isEmpty()) {
        venueNameTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Название не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (!latTextField.getText().isEmpty() || !lonTextField.getText().isEmpty()) {
        if (!checkGeo(true)) return false;
      }
      return true;
    }

    @Override
    public void load(@NotNull VenueObj venue) {
      venueNameTextField.setText(venue.getName());
      addressTextField.setText(venue.getAddress());
      venueTypeComboBox.setSelectedItem(venue.getType());
      latTextField.setText(venue.getGeoLat());
      lonTextField.setText(venue.getGeoLon());
      venueDescTextArea.setText(venue.getDescription());
      ImageObj bigImage = venue.getBigImage();
      venueBigImageLabel.setPoster(bigImage, venue.getId());
      if (bigImage == null) {
        PosterData.VENUE_BIG.setImg(null);
        PosterData.VENUE_BIG.setType(null);
      } else {
        PosterData.VENUE_BIG.setImg(bigImage.getImg());
        PosterData.VENUE_BIG.setType(bigImage.getType());
      }
      delVenueButton.setEnabled(venue.isChildless());
    }

    @Override
    public boolean save(@NotNull VenueObj venue) {
      venue.setName(venueNameTextField.getText().trim());
      venue.setAddress(addressTextField.getText().trim());
      venue.setType((VenueType) venueTypeComboBox.getSelectedItem());
      venue.setGeoLat(latTextField.getText());
      venue.setGeoLon(lonTextField.getText());
      venue.setDescription(venueDescTextArea.getText());
      PosterData posterData = PosterData.VENUE_BIG;
      if (posterData.getType() == null) venue.setBigImage(null);
      else venue.setBigImage(new ImageObj(posterData.getImg(), posterData.getType()));
      Network<Request, Response> network = net.create("SAVE_VENUE", new Request(venue), MainFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }

  private class PlanOperationListener implements OperationListener<SeatingPlanObj> {
    @Override
    public void clear() {
      planNameTextField.setText("");
      planImagePanel.clear();
      catTableModel.clear();
      delPlanButton.setEnabled(false);
    }

    @Override
    public boolean check() {
      if (planNameTextField.getText().trim().isEmpty()) {
        planNameTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Название схемы зала не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      int totalSeats = 0;
      for (int i = 0; i < catTableModel.getRowCount(); i++) {
        if (catTableModel.isRowLimit(i)) break;
        totalSeats += ((Integer) catTableModel.getValueAt(i, 1));
      }
      if (totalSeats > Env.MAX_PLAN_NUMBER_SEATS) {
        JOptionPane.showMessageDialog(MainFrame.this, "Схема содержит " + totalSeats + " мест (больше " + Env.MAX_PLAN_NUMBER_SEATS + ")", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    @Override
    public void load(@NotNull SeatingPlanObj plan) {
      planNameTextField.setText(plan.getName());
      planNameTextField.setEditable(plan.isOwner());
      if (plan.isPlacement()) {
        planImagePanel.setPlanName(plan.getName());
        planImagePanel.setVenueSvgZip(plan.isCombined(), plan.getSvgZip(), plan.getCategoryList(), plan.getId());
      }
      catTableModel.setData(plan.getCategoryList(), plan.getCategoryLimitList());
      catTableModel.setReadOnly(!plan.isOwner());
      Boolean componentPlacement = null;
      if (planDataPanel.getComponentCount() == 2) componentPlacement = Boolean.TRUE;
      else if (planDataPanel.getComponentCount() == 1) componentPlacement = Boolean.FALSE;
      if (componentPlacement == null || componentPlacement != plan.isPlacement()) {
        planDataPanel.removeAll();
        if (plan.isPlacement()) {
          planDataPanel.add(planImagePanel, BorderLayout.CENTER);
          planDataPanel.add(planCatScrollPane, BorderLayout.SOUTH);
        } else {
          planDataPanel.add(planCatScrollPane, BorderLayout.CENTER);
        }
        planDataPanel.revalidate();
      }
      catTable.packAll();
      delPlanButton.setEnabled(plan.isOwner());
    }

    @Override
    public boolean save(@NotNull SeatingPlanObj plan) {
      plan.setName(planNameTextField.getText().trim());
      List<CategoryObj> categoryList = plan.getCategoryList();
      for (int i = 0; i < categoryList.size(); i++) {
        CategoryObj categoryObj = categoryList.get(i);
        categoryObj.setName((String) catTableModel.getValueAt(i, 0));
        if (!categoryObj.isPlacement()) categoryObj.setSeatsNumber((Integer) catTableModel.getValueAt(i, 1));
        categoryObj.setInitPrice((BigDecimal) catTableModel.getValueAt(i, 2));
      }
      List<CategoryLimitObj> categoryLimitList = plan.getCategoryLimitList();
      int dy = categoryList.size();
      for (int i = 0; i < categoryLimitList.size(); i++) {
        CategoryLimitObj categoryLimitObj = categoryLimitList.get(i);
        categoryLimitObj.setLimit((Integer) catTableModel.getValueAt(dy + i, 1));
      }
      Network<Request, Response> network = net.create("SAVE_PLAN", new Request(plan), MainFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }

  private class ActionOperationListener implements OperationListener<ActionObj> {
    @Override
    public void clear() {
      actionAgeComboBox.setSelectedIndex(-1);
      actionDurationTextField.setValue(null);
      actionOwnerNameTextField.setText("");
      actionOwnerInnTextField.setText("");
      actionOwnerPhoneTextField.setText("");
      actionNameTextField.setText("");
      actionFullNameTextField.setText("");
      bookletLabel.setText("");
      bookletLabel.setActionCommand("");
      newBookletLabel.setEnabled(false);
      actionRatingComboBox.setSelectedIndex(-1);
      kdpTextField.setValue(null);
      chargeTextField.setText("");
      genresButton.setEnabled(false);
      genresTableModel.setData(Env.genreList);
      posterNameTextField.setText("");
      posterDescTextArea.setText("");
      actionBigImageLabel.setPoster(null);
      PosterData.ACTION_BIG.setImg(null);
      PosterData.ACTION_BIG.setType(null);
      actionSmallImageLabel.setPoster(null);
      PosterData.ACTION_SMALL.setImg(null);
      PosterData.ACTION_SMALL.setType(null);
      delActionButton.setEnabled(false);
      loadArchivalButton.setEnabled(false);
    }

    @Override
    public boolean check() {
      if (actionAgeComboBox.getSelectedIndex() == -1) {
        actionAgeComboBox.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Возрастное ограничение не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      Integer duration;
      try {
        actionDurationTextField.commitEdit();
        duration = (Integer) actionDurationTextField.getValue();
      } catch (ParseException e) {
        duration = null;
      }
      if (duration == null) {
        actionDurationTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Продолжительность не задана", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (actionOwnerNameTextField.getText().trim().isEmpty()) {
        actionOwnerNameTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Устроитель не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (actionOwnerInnTextField.getText().trim().isEmpty()) {
        actionOwnerInnTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "ИНН не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (!actionOwnerInnTextField.getText().trim().matches("\\d{10}|\\d{12}")) {
        actionOwnerInnTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "ИНН задан неверно", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (actionNameTextField.getText().trim().isEmpty()) {
        actionNameTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Краткое название не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (actionFullNameTextField.getText().trim().isEmpty()) {
        actionFullNameTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Полное название не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      try {
        kdpTextField.commitEdit();
      } catch (ParseException e) {
        kdpTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "КДП задан неверно", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      BigDecimal minCharge;
      try {
        chargeTextField.commitEdit();
        minCharge = (BigDecimal) chargeTextField.getValue();
      } catch (ParseException e) {
        minCharge = null;
      }
      if (minCharge == null) {
        chargeTextField.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Минимальный сервисный сбор не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      boolean big = (PosterData.ACTION_BIG.getType() != null);
      boolean small = (PosterData.ACTION_SMALL.getType() != null);
      if (big ^ small) {
        JOptionPane.showMessageDialog(MainFrame.this, "Загружено только одно изображение из двух", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return true;
    }

    @Override
    public void load(@NotNull ActionObj action) {
      if (action.getAge() == ActionObj.Age.UNKNOWN) actionAgeComboBox.setSelectedIndex(-1);
      else actionAgeComboBox.setSelectedItem(action.getAge());
      actionDurationTextField.setValue(action.getDuration());
      actionOwnerNameTextField.setText(action.getLegalOwner());
      actionOwnerInnTextField.setText(action.getLegalOwnerInn());
      actionOwnerPhoneTextField.setText(action.getLegalOwnerPhone());
      actionNameTextField.setText(action.getName());
      actionFullNameTextField.setText(action.getFullName());
      bookletLabel.setText(action.getBookletType() == null ? "" : action.getBookletType().getDesc());
      bookletLabel.setActionCommand(action.getBookletType() == null ? "" : String.valueOf(action.getId()));
      newBookletLabel.setEnabled(true);
      actionRatingComboBox.setSelectedItem(action.getRating());
      kdpTextField.setValue(action.getKdp());
      chargeTextField.setValue(action.getMinChargePercent());
      genresButton.setEnabled(true);
      genresTableModel.setData(Env.genreList, action.getGenreSet());
      posterNameTextField.setText(action.getPosterName());
      posterDescTextArea.setText(action.getPosterDesc());
      ImageObj bigImage = action.getBigImage();
      actionBigImageLabel.setPoster(bigImage, action.getId());
      if (bigImage == null) {
        PosterData.ACTION_BIG.setImg(null);
        PosterData.ACTION_BIG.setType(null);
      } else {
        PosterData.ACTION_BIG.setImg(bigImage.getImg());
        PosterData.ACTION_BIG.setType(bigImage.getType());
      }
      ImageObj smallImage = action.getSmallImage();
      actionSmallImageLabel.setPoster(smallImage, action.getId());
      if (smallImage == null) {
        PosterData.ACTION_SMALL.setImg(null);
        PosterData.ACTION_SMALL.setType(null);
      } else {
        PosterData.ACTION_SMALL.setImg(smallImage.getImg());
        PosterData.ACTION_SMALL.setType(smallImage.getType());
      }
      delActionButton.setEnabled(action.isChildless());
      loadArchivalButton.setEnabled(!action.getCityIdSet().isEmpty() && !Env.loadedArchivalActionIdSet.contains(action.getId()));
    }

    @Override
    public boolean save(@NotNull ActionObj action) {
      action.setAge((ActionObj.Age) actionAgeComboBox.getSelectedItem());
      action.setDuration((Integer) actionDurationTextField.getValue());
      action.setLegalOwner(actionOwnerNameTextField.getText().trim());
      action.setLegalOwnerInn(actionOwnerInnTextField.getText().trim());
      action.setLegalOwnerPhone(actionOwnerPhoneTextField.getText().trim());
      action.setName(actionNameTextField.getText().trim());
      action.setFullName(actionFullNameTextField.getText().trim());
      action.setRating((Integer) actionRatingComboBox.getSelectedItem());
      action.setKdp((Integer) kdpTextField.getValue());
      action.setMinChargePercent((BigDecimal) chargeTextField.getValue());
      action.setGenreSet(genresTableModel.getSelectedGenreSet());
      action.setPosterName(posterNameTextField.getText().trim());
      action.setPosterDesc(posterDescTextArea.getText());
      PosterData posterData = PosterData.ACTION_BIG;
      if (posterData.getType() == null) action.setBigImage(null);
      else action.setBigImage(new ImageObj(posterData.getImg(), posterData.getType()));
      posterData = PosterData.ACTION_SMALL;
      if (posterData.getType() == null) action.setSmallImage(null);
      else action.setSmallImage(new ImageObj(posterData.getImg(), posterData.getType()));
      Network<Request, Response> network = net.create("SAVE_ACTION", new Request(action), MainFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }

  private class ActionEventOperationListener implements OperationListener<ActionEventObj> {
    @Override
    public void clear() {
      showDatePicker.setDate(null);
      sellStartDatePicker.setDate(null);
      sellEndDatePicker.setDate(null);
      sellEnabledCheckBox.setSelected(true);
      eTicketsCheckBox.setSelected(true);
      fullNameRequiredCheckBox.setSelected(false);
      phoneRequiredCheckBox.setSelected(false);
      ticketRefundAllowedCheckBox.setSelected(true);
      ticketReissueAllowedCheckBox.setSelected(false);
      longReserveCheckBox.setSelected(false);
      maxReserveTimeTextField.setValue(null);
      vatCheckBox.setSelected(false);
      vatTextField.setValue(null);
      applyEventParamsButton.setEnabled(false);
      catPriceTableModel.clear();
      delActionEventButton.setEnabled(false);
      controlActionEventButton.setEnabled(false);
      ebsLabel.setText("");
      ebsLabel.setActionCommand("");
    }

    @Override
    public boolean check() {
      if (showDatePicker.getDate() == null) {
        eventTabbedPane.setSelectedIndex(0);
        showDatePicker.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Начало сеанса не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (sellStartDatePicker.getDate() == null) {
        eventTabbedPane.setSelectedIndex(0);
        sellStartDatePicker.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Начало продаж не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (sellEndDatePicker.getDate() == null) {
        eventTabbedPane.setSelectedIndex(0);
        sellEndDatePicker.requestFocus();
        JOptionPane.showMessageDialog(MainFrame.this, "Конец продаж не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (longReserveCheckBox.isSelected()) {
        Integer maxReserveTime;
        try {
          maxReserveTimeTextField.commitEdit();
          maxReserveTime = (Integer) maxReserveTimeTextField.getValue();
        } catch (ParseException e) {
          maxReserveTime = null;
        }
        if (maxReserveTime == null || maxReserveTime < 1) {
          eventTabbedPane.setSelectedIndex(1);
          maxReserveTimeTextField.requestFocus();
          JOptionPane.showMessageDialog(MainFrame.this, "Время долговременного бронирования не задано", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }
      if (vatCheckBox.isSelected()) {
        BigDecimal vat;
        try {
          vatTextField.commitEdit();
          vat = (BigDecimal) vatTextField.getValue();
        } catch (ParseException e) {
          vat = null;
        }
        if (vat == null) {
          eventTabbedPane.setSelectedIndex(1);
          vatTextField.requestFocus();
          JOptionPane.showMessageDialog(MainFrame.this, "НДС не задан", "Ошибка", JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }
      return true;
    }

    @Override
    public void load(@NotNull ActionEventObj actionEvent) {
      try {
        showDatePicker.setDateFormatted(actionEvent.getShowTime());
        sellStartDatePicker.setDateFormatted(actionEvent.getSellStartTime());
        sellEndDatePicker.setDateFormatted(actionEvent.getSellEndTime());
      } catch (ParseException e) {
        JOptionPane.showMessageDialog(MainFrame.this, "Ошибка формата даты", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
      sellEnabledCheckBox.setSelected(actionEvent.isSellEnabled());
      eTicketsCheckBox.setSelected(actionEvent.isETickets());
      fullNameRequiredCheckBox.setSelected(actionEvent.isFullNameRequired());
      phoneRequiredCheckBox.setSelected(actionEvent.isPhoneRequired());
      ticketRefundAllowedCheckBox.setSelected(actionEvent.isTicketRefundAllowed());
      ticketReissueAllowedCheckBox.setSelected(actionEvent.isTicketReissueAllowed());
      longReserveCheckBox.setSelected(actionEvent.getMaxReserveTime() != null);
      maxReserveTimeTextField.setValue(actionEvent.getMaxReserveTime());
      vatCheckBox.setSelected(actionEvent.getVat().compareTo(BigDecimal.ZERO) != 0);
      vatTextField.setValue(actionEvent.getVat());
      applyEventParamsButton.setEnabled(true);
      catPriceTableModel.setData(actionEvent.getPriceList(), actionEvent.isQuota());
      catPriceTable.packAll();
      delActionEventButton.setEnabled(true);
      controlActionEventButton.setEnabled(!actionEvent.isArchival());
      GatewayEventObj gatewayEvent = actionEvent.getGatewayEvent();
      GatewayObj gateway = gatewayEvent.getGateway();
      StringBuilder gatewayName = new StringBuilder();
      if (gateway.getId() != 0) gatewayName.append("[").append(gateway.getId()).append("]");
      gatewayName.append("[").append(gateway.getName()).append("] ");
      ebsLabel.setText(gatewayName.toString() + gatewayEvent + (actionEvent.isQuota() ? "Квота" : ""));
      if (actionEvent.isPlacementPlan()) {
        ebsLabel.setEnabled(true);
        ebsLabel.setActionCommand(String.valueOf(actionEvent.getId()));
      } else {
        ebsLabel.setEnabled(false);
        ebsLabel.setActionCommand("");
      }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean save(@NotNull ActionEventObj actionEvent) {
      if (actionEvent.isArchival()) {
        JOptionPane.showMessageDialog(MainFrame.this, "Невозможно изменить архивный сеанс", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      if (!actionEvent.getShowTime().equals(showDatePicker.getDateFormatted())) {
        int res = JOptionPane.showConfirmDialog(MainFrame.this,
            "Вы собираетесь изменить дату/время начала сеанса.\nМенять начало сеанса имеет смысл, только если реально произошли изменения в расписании.\nПродолжить?",
            "Вопрос", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return false;
      }
      actionEvent.setShowTime(showDatePicker.getDateFormatted());
      actionEvent.setSellStartTime(sellStartDatePicker.getDateFormatted());
      actionEvent.setSellEndTime(sellEndDatePicker.getDateFormatted());
      actionEvent.setSellEnabled(sellEnabledCheckBox.isSelected());
      actionEvent.setETickets(eTicketsCheckBox.isSelected());
      actionEvent.setFullNameRequired(fullNameRequiredCheckBox.isSelected());
      actionEvent.setPhoneRequired(phoneRequiredCheckBox.isSelected());
      actionEvent.setTicketRefundAllowed(ticketRefundAllowedCheckBox.isSelected());
      actionEvent.setTicketReissueAllowed(ticketReissueAllowedCheckBox.isSelected());
      if (longReserveCheckBox.isSelected()) actionEvent.setMaxReserveTime((Integer) maxReserveTimeTextField.getValue());
      else actionEvent.setMaxReserveTime(null);
      if (vatCheckBox.isSelected()) actionEvent.setVat((BigDecimal) vatTextField.getValue());
      else actionEvent.setVat(BigDecimal.ZERO);
      List<CategoryPriceObj> priceList = actionEvent.getPriceList();
      for (int i = 0; i < priceList.size(); i++) {
        CategoryPriceObj categoryPriceObj = priceList.get(i);
        categoryPriceObj.setName((String) catPriceTableModel.getValueAt(i, 0));
        categoryPriceObj.setPrice((BigDecimal) catPriceTableModel.getValueAt(i, 1));
      }
      Network<Request, Response> network = net.create("SAVE_ACTION_EVENT", new Request(actionEvent), MainFrame.this);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);
      return saveResult;
    }
  }
  //</editor-fold>

  private class PosterTransferHandler extends TransferHandler {
    private final FileNameExtensionFilter imageFileFilter = new FileNameExtensionFilter("Изображения", ImageIO.getReaderFileSuffixes());

    @Override
    public boolean canImport(TransferSupport support) {
      if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        Transferable transferable = support.getTransferable();
        try {
          @SuppressWarnings("unchecked")
          List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
          if (files.size() == 1) {
            File file = files.get(0);
            if (imageFileFilter.accept(file)) {
              dragFile = file;
              return true;
            }
          }
        } catch (InvalidDnDOperationException ex) {
          if (dragFile != null) return true;
        } catch (Exception ignored) {
        }
      }
      dragFile = null;
      return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
      Component component = support.getComponent();
      if (component == actionBigImageLabel) loadImage(dragFile, PosterData.ACTION_BIG);
      else if (component == actionSmallImageLabel) loadImage(dragFile, PosterData.ACTION_SMALL);
      else if (component == venueBigImageLabel) loadImage(dragFile, PosterData.VENUE_BIG);
      return true;
    }
  }

}
