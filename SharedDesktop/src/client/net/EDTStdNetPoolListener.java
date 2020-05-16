package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.12.15
 */
class EDTStdNetPoolListener implements NetPoolListenerOver {
  private final NetPoolListener overriddenListener;

  public EDTStdNetPoolListener(NetPoolListener overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netState(Network<?, ?> network, NetEvent<?, ?> event, Network.State state) {
    overriddenListener.netState(network, event, state);
  }

  @Override
  public void netResult(Network<?, ?> network, NetResultEvent<?, ?> result) {
    overriddenListener.netResult(network, result);
  }

  @Override
  public void netError(Network<?, ?> network, NetErrorEvent<?, ?> error) {
    overriddenListener.netError(network, error);
  }

  @Override
  public NetPoolListener getOverridden() {
    return overriddenListener;
  }
}
