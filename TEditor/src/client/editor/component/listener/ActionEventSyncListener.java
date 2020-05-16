package client.editor.component.listener;

import java.util.EventListener;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public interface ActionEventSyncListener extends EventListener {

  void syncComplete(@NotNull ActionEventSyncEvent event);
}
