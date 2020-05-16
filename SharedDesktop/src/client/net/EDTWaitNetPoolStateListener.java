package client.net;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTWaitNetPoolStateListener implements NetPoolStateListenerOver {
  private final NetPoolStateListener overriddenListener;

  public EDTWaitNetPoolStateListener(NetPoolStateListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netPoolStarted() {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netPoolStarted();
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void netPoolFinished() {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netPoolFinished();
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public NetPoolStateListener getOverridden() {
    return overriddenListener;
  }
}
