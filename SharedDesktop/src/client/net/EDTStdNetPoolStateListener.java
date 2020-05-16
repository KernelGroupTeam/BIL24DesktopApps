package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTStdNetPoolStateListener implements NetPoolStateListenerOver {
  private final NetPoolStateListener overriddenListener;

  public EDTStdNetPoolStateListener(NetPoolStateListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netPoolStarted() {
    overriddenListener.netPoolStarted();
  }

  @Override
  public void netPoolFinished() {
    overriddenListener.netPoolFinished();
  }

  @Override
  public NetPoolStateListener getOverridden() {
    return overriddenListener;
  }
}
