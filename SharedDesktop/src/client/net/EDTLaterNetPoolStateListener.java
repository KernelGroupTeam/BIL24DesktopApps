package client.net;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTLaterNetPoolStateListener implements NetPoolStateListenerOver {
  private final NetPoolStateListener overriddenListener;

  public EDTLaterNetPoolStateListener(NetPoolStateListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netPoolStarted() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netPoolStarted();
      }
    });
  }

  @Override
  public void netPoolFinished() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netPoolFinished();
      }
    });
  }

  @Override
  public NetPoolStateListener getOverridden() {
    return overriddenListener;
  }
}
