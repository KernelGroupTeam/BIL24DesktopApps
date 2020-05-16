package client.net;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.12.15
 */
class EDTWaitNetListener<Req, Res> implements NetListener<Req, Res> {
  private final NetListener<Req, Res> overriddenListener;

  public EDTWaitNetListener(NetListener<Req, Res> overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netState(final NetEvent<Req, Res> event, final Network.State state) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netState(event, state);
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
  public void netResult(final NetResultEvent<Req, Res> result) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netResult(result);
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
  public void netError(final NetErrorEvent<Req, Res> error) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          overriddenListener.netError(error);
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
