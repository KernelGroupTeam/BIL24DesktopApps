package client.component;

import java.awt.*;
import java.beans.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jetbrains.annotations.Nullable;

public class JXDateTimePicker extends JXDatePicker {
  private JSpinner timeSpinner;
  private JPanel timePanel;
  private DateFormat timeFormat = new SimpleDateFormat("HH:mm");
  private DateFormat completeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

  public JXDateTimePicker() {
    super();
    setFormats(completeFormat);
    getMonthView().setSelectionModel(new SingleDaySelectionModel());
    getEditor().addPropertyChangeListener("value", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        Date newDate = (Date) evt.getNewValue();
        if (newDate != null && timeSpinner != null) {
          GregorianCalendar calendar = new GregorianCalendar();
          calendar.setTime(newDate);
          if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.MINUTE) != 0) {//время установлено в текстовом поле вручную, при 0:00 не работает
            timeSpinner.setValue(newDate);
          }
        }
      }
    });
  }

  public JXDateTimePicker(Date d) {
    this();
    setDate(d);
  }

  @Override
  public void commitEdit() throws ParseException {
    commitTime();
    super.commitEdit();
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setTimeSpinners();
  }

  @Override
  public JPanel getLinkPanel() {
    super.getLinkPanel();
    if (timePanel == null) {
      timePanel = createTimePanel();
    }
    setTimeSpinners();
    return timePanel;
  }

  private JPanel createTimePanel() {
    JPanel newPanel = new JPanel();
    newPanel.setLayout(new FlowLayout());
//    newPanel.add(panelOriginal);

    SpinnerDateModel dateModel = new SpinnerDateModel();
    timeSpinner = new JSpinner(dateModel);
    if (timeFormat == null) timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    updateTextFieldFormat();
    newPanel.add(new JLabel("время:"));
    newPanel.add(timeSpinner);
//    newPanel.setBackground(Color.WHITE);
    return newPanel;
  }

  private void updateTextFieldFormat() {
    if (timeSpinner == null) return;
    JFormattedTextField tf = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
    DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
    DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
    // Change the date format to only show the hours
    formatter.setFormat(timeFormat);
  }

  private void commitTime() {
    Date date = getDate();
    if (date != null) {
      Date time = (Date) timeSpinner.getValue();
      GregorianCalendar timeCalendar = new GregorianCalendar();
      timeCalendar.setTime(time);

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
      calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      Date newDate = calendar.getTime();
      setDate(newDate);
    }
  }

  private void setTimeSpinners() {
    Date date = getDate();
    if (date != null) {
      timeSpinner.setValue(date);
    }
  }

  public DateFormat getTimeFormat() {
    return timeFormat;
  }

  public void setTimeFormat(DateFormat timeFormat) {
    this.timeFormat = timeFormat;
    updateTextFieldFormat();
  }

  public DateFormat getFormat() {
    return completeFormat;
  }

  public void setFormat(DateFormat format) {
    setFormats(completeFormat);
    completeFormat = format;
  }

  public void setDateFormatted(@Nullable String date) throws ParseException {
    if (date == null) setDate(null);
    else setDate(completeFormat.parse(date));
  }

  @Nullable
  public String getDateFormatted() {
    Date date = getDate();
    if (date == null) return null;
    return completeFormat.format(date);
  }
}
