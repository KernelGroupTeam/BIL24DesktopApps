package client.net;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTLaterNetPoolListener implements NetPoolListenerOver {
  private final NetPoolListener overriddenListener;

  public EDTLaterNetPoolListener(NetPoolListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netState(final Network<?, ?> network, final NetEvent<?, ?> event, final Network.State state) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netState(network, event, state);
      }
    });
  }

  @Override
  public void netResult(final Network<?, ?> network, final NetResultEvent<?, ?> result) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netResult(network, result);
      }
    });
  }

  @Override
  public void netError(final Network<?, ?> network, final NetErrorEvent<?, ?> error) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netError(network, error);
      }
    });
  }

  @Override
  public NetPoolListener getOverridden() {
    return overriddenListener;
  }
}
