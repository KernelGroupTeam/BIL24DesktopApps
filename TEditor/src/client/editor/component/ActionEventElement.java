package client.editor.component;

import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.*;
import server.protocol2.editor.ActionEventObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ActionEventElement implements Comparable<ActionEventElement> {
  public static final String defaultDateFormat = "EE dd.MM.yyyy HH:mm";
  @NotNull
  private final ActionEventObj actionEvent;
  @NotNull
  private DateFormat dateFormat;
  @Nullable
  private Date showTime;
  private String representation;

  public ActionEventElement(@NotNull ActionEventObj actionEvent) {
    this(actionEvent, null);
  }

  public ActionEventElement(@NotNull ActionEventObj actionEvent, @Nullable DateFormat dateFormat) {
    this.actionEvent = actionEvent;
    if (dateFormat != null) this.dateFormat = dateFormat;
    else this.dateFormat = new SimpleDateFormat(defaultDateFormat);
    createRepresentation();
  }

  @NotNull
  public ActionEventObj getActionEvent() {
    return actionEvent;
  }

  @NotNull
  public DateFormat getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(@NotNull DateFormat dateFormat) {
    this.dateFormat = dateFormat;
    createRepresentation();
  }

  private void createRepresentation() {
    String showTimeStr = actionEvent.getShowTime();
    try {
      showTime = ActionEventObj.parseFormat(showTimeStr);
      representation = dateFormat.format(showTime);
    } catch (ParseException e) {
      representation = showTimeStr;
    }
  }

  @Override
  public String toString() {
    return representation;
  }

  @Override
  public int compareTo(@NotNull ActionEventElement o) {
    if (showTime == null || o.showTime == null) return 0;
    return showTime.compareTo(o.showTime);
  }
}
