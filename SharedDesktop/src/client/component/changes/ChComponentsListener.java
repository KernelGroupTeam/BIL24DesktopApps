package client.component.changes;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 19.11.17
 */
public interface ChComponentsListener extends EventListener {

  void chStatusChanged(boolean edited);
}
