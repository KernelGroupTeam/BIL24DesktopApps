package client.net;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTWaitNetPoolListener implements NetPoolListenerOver {
  private final NetPoolListener overriddenListener;

  public EDTWaitNetPoolListener(NetPoolListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netState(final Network<?, ?> network, final NetEvent<?, ?> event, final Network.State state) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netState(network, event, state);
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
  public void netResult(final Network<?, ?> network, final NetResultEvent<?, ?> result) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netResult(network, result);
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
  public void netError(final Network<?, ?> network, final NetErrorEvent<?, ?> error) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netError(network, error);
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
  public NetPoolListener getOverridden() {
    return overriddenListener;
  }
}
