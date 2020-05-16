/*
 * Created by JFormDesigner on Wed Jul 22 01:29:46 MSK 2015
 */

package client.editor.component;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import client.component.JXDateTimePicker;
import client.component.suggestion.SuggestionComboBox;
import client.editor.component.listener.*;
import client.editor.component.renderer.GatewayEventListRenderer;
import client.utils.Utils;
import org.jetbrains.annotations.*;
import server.protocol2.common.GatewayObj;
import server.protocol2.editor.GatewayEventObj;

/**
 * @author Maksim
 */
public class ActionEventItemPanel extends JPanel implements CanResizeComponent {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXDateTimePicker showDatePicker;
  private JXDateTimePicker sellStartDatePicker;
  private JXDateTimePicker sellEndDatePicker;
  private JLabel label4;
  private SuggestionComboBox<GatewayEventObj> gatewayEventComboBox;
  private JButton infoButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final GatewayEventObj noneGatewayEvent = new GatewayEventObj(GatewayObj.getNone(), 0, "Нет");
  private static final GatewayEventListRenderer gatewayEventListRenderer = new GatewayEventListRenderer();
  private final Border border = new EmptyBorder(3, 0, 3, 0);
  private final Border ebsBorder = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.black), new EmptyBorder(5, 0, 5, 0));
  @Nullable
  private ActionListener infoButtonActionListener;
  @Nullable
  private ActionEventItemPanel lastPanel;
  private boolean ebs = false;

  public ActionEventItemPanel() {
    initComponents();
    //особенность JXDatePicker, если вызвать первый раз после установки значения, BasicDatePickerUI.DefaultEditor.getPreferredSize() считает по-другому
    showDatePicker.getPreferredSize();
    sellStartDatePicker.getPreferredSize();
    sellEndDatePicker.getPreferredSize();
    gatewayEventComboBox.setRenderer(gatewayEventListRenderer);
    gatewayEventComboBox.setElementToStringConverter(gatewayEventListRenderer);
    gatewayEventComboBox.addItem(noneGatewayEvent);
    label4.setVisible(false);
    gatewayEventComboBox.setVisible(false);
    infoButton.setVisible(false);
    showDatePicker.getEditor().addPropertyChangeListener("value", new ChDatePicker());
  }

  public void init(@Nullable ActionEventItemPanel lastPanel) {
    this.lastPanel = lastPanel;
    if (lastPanel == null) {
      showDatePicker.setDate(null);
      sellStartDatePicker.setDate(new Date(Utils.todayMidnight));
    } else {
      showDatePicker.setDate(lastPanel.showDatePicker.getDate());
      sellStartDatePicker.setDate(lastPanel.sellStartDatePicker.getDate());
    }
    sellEndDatePicker.setDate(null);
  }

  public void setGatewayEventList(@Nullable List<GatewayEventObj> gatewayEventList) {
    ebs = (gatewayEventList != null);
    this.setBorder(ebs ? ebsBorder : border);
    label4.setVisible(ebs);
    gatewayEventComboBox.setVisible(ebs);
    gatewayEventComboBox.removeAllItems();
    gatewayEventComboBox.addItem(noneGatewayEvent);
    if (gatewayEventList != null) {
      for (GatewayEventObj gatewayEvent : gatewayEventList) {
        gatewayEventComboBox.addItem(gatewayEvent);
      }
    }
    infoButton.setVisible(ebs);
    ResizeListener[] listeners = listenerList.getListeners(ResizeListener.class);
    ResizeEvent resizeEvent = new ResizeEvent(this, ResizeEvent.Dimension.BOTH);
    for (ResizeListener listener : listeners) {
      listener.needResize(resizeEvent);
    }
  }

  @Nullable
  public Date getShowDate() {
    return showDatePicker.getDate();
  }

  @Nullable
  public Date getSellStartDate() {
    return sellStartDatePicker.getDate();
  }

  @Nullable
  public Date getSellEndDate() {
    return sellEndDatePicker.getDate();
  }

  @Nullable
  public String getShowDateFormatted() {
    return showDatePicker.getDateFormatted();
  }

  @Nullable
  public String getSellStartDateFormatted() {
    return sellStartDatePicker.getDateFormatted();
  }

  @Nullable
  public String getSellEndDateFormatted() {
    return sellEndDatePicker.getDateFormatted();
  }

  @Nullable
  public GatewayEventObj getGatewayEvent() {
    if (!ebs) return null;
    GatewayEventObj gatewayEvent = gatewayEventComboBox.getItemAt(gatewayEventComboBox.getSelectedIndex());
    if (gatewayEvent == noneGatewayEvent) return null;
    return gatewayEvent;
  }

  public void setGatewayEvent(@NotNull GatewayEventObj gatewayEvent) {
    gatewayEventComboBox.setSelectedItem(gatewayEvent);
  }

  public void requestFocusShowDate() {
    showDatePicker.requestFocus();
  }

  public void requestFocusSellStartDate() {
    sellStartDatePicker.requestFocus();
  }

  public void requestFocusSellEndDate() {
    sellEndDatePicker.requestFocus();
  }

  public void requestFocusGatewayEvent() {
    gatewayEventComboBox.requestFocus();
  }

  public void setEnabledInfoButton(boolean b) {
    if (b) {
      GatewayEventObj gatewayEvent = getGatewayEvent();
      if (gatewayEvent == null) return;
    }
    infoButton.setEnabled(b);
  }

  private void gatewayEventComboBoxItemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      GatewayEventObj gatewayEvent = getGatewayEvent();
      infoButton.setEnabled(gatewayEvent != null);
      if (gatewayEvent != null) {
        Date gatewayDate = GatewayEventObj.parseFormat(gatewayEvent.getDate());
        if (gatewayDate != null) showDatePicker.setDate(gatewayDate);
      }
    }
  }

  private void removeButtonActionPerformed() {
    Container parent = getParent();
    if (parent != null) {
      parent.remove(this);
      parent.revalidate();
    }
    ResizeListener[] listeners = listenerList.getListeners(ResizeListener.class);
    ResizeEvent resizeEvent = new ResizeEvent(this, ResizeEvent.Dimension.BOTH);
    for (ResizeListener listener : listeners) {
      listener.needResize(resizeEvent);
    }
  }

  private void infoButtonActionPerformed(ActionEvent e) {
    e.setSource(this);
    if (infoButtonActionListener != null) infoButtonActionListener.actionPerformed(e);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JLabel label1 = new JLabel();
    showDatePicker = new JXDateTimePicker();
    JLabel label2 = new JLabel();
    sellStartDatePicker = new JXDateTimePicker();
    JLabel label3 = new JLabel();
    sellEndDatePicker = new JXDateTimePicker();
    JButton removeButton = new JButton();
    label4 = new JLabel();
    gatewayEventComboBox = new SuggestionComboBox<>();
    infoButton = new JButton();

    //======== this ========
    setLayout(new GridBagLayout());
    ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
    ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0};
    ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};
    ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

    //---- label1 ----
    label1.setText("\u0421\u0435\u0430\u043d\u0441:");
    add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));
    add(showDatePicker, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));

    //---- label2 ----
    label2.setText("\u041d\u0430\u0447\u0430\u043b\u043e \u043f\u0440\u043e\u0434\u0430\u0436:");
    add(label2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));
    add(sellStartDatePicker, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));

    //---- label3 ----
    label3.setText("\u041a\u043e\u043d\u0435\u0446 \u043f\u0440\u043e\u0434\u0430\u0436:");
    add(label3, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));
    add(sellEndDatePicker, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 5), 0, 0));

    //---- removeButton ----
    removeButton.setIcon(new ImageIcon(getClass().getResource("/resources/minus.png")));
    removeButton.setMargin(new Insets(1, 1, 1, 1));
    removeButton.setToolTipText("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0435\u0430\u043d\u0441");
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeButtonActionPerformed();
      }
    });
    add(removeButton, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- label4 ----
    label4.setText("\u0421\u0432\u044f\u0437\u044c:");
    add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(3, 0, 0, 5), 0, 0));

    //---- gatewayEventComboBox ----
    gatewayEventComboBox.setMaximumRowCount(15);
    gatewayEventComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        gatewayEventComboBoxItemStateChanged(e);
      }
    });
    add(gatewayEventComboBox, new GridBagConstraints(1, 1, 6, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(3, 0, 0, 5), 0, 0));

    //---- infoButton ----
    infoButton.setIcon(new ImageIcon(getClass().getResource("/resources/info.png")));
    infoButton.setMargin(new Insets(1, 1, 1, 1));
    infoButton.setToolTipText("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e\u0442 \u0412\u0411\u0421");
    infoButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        infoButtonActionPerformed(e);
      }
    });
    add(infoButton, new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(3, 0, 0, 0), 0, 0));
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  public void setInfoButtonActionListener(@Nullable ActionListener listener) {
    this.infoButtonActionListener = listener;
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

  @NotNull
  public static String stringValue(GatewayEventObj value) {
    return gatewayEventListRenderer.stringValue(value);
  }

  public static boolean isExists(@NotNull GatewayEventObj value) {
    return gatewayEventListRenderer.isExists(value);
  }

  private class ChDatePicker implements PropertyChangeListener {
    private Date oldDate = null;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      Date newDate = (Date) evt.getNewValue();
      if (newDate == null) return;
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(newDate);
//      if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) return;//не помню зачем, возможно для компонента без времени

      Date sellEnd = sellEndDatePicker.getDate();
      if (isSync(oldDate, sellEnd)) sellEndDatePicker.setDate(new Date(newDate.getTime() - getDelta()));
      oldDate = newDate;
    }

    //метод проверяет синхронизированы ли showDate и sellEndDate с учетом delta
    //если true, sellEndDate поменяется соответственно showDate с учетом delta
    private boolean isSync(@Nullable Date showDate, @Nullable Date sellEndDate) {
      return showDate == null || sellEndDate == null || showDate.getTime() - sellEndDate.getTime() == getDelta();
    }

    private long getDelta() {
      if (lastPanel == null) return 0L;
      Date showDate = lastPanel.showDatePicker.getDate();
      Date sellEndDate = lastPanel.sellEndDatePicker.getDate();
      if (showDate == null || sellEndDate == null) return 0L;
      return showDate.getTime() - sellEndDate.getTime();
    }
  }
}
