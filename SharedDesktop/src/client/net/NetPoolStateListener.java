package client.net;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public interface NetPoolStateListener extends EventListener {

  void netPoolStarted();

  void netPoolFinished();
}
