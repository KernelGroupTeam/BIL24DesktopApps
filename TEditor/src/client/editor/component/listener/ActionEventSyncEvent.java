package client.editor.component.listener;

import java.util.EventObject;

import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.ActionEventData;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ActionEventSyncEvent extends EventObject {
  @NotNull
  private final ActionEventData actionEventData;

  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  public ActionEventSyncEvent(@NotNull Object source, @NotNull ActionEventData actionEventData) {
    super(source);
    this.actionEventData = actionEventData;
  }

  @NotNull
  public ActionEventData getActionEventData() {
    return actionEventData;
  }
}
