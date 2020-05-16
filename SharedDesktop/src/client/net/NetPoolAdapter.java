package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public abstract class NetPoolAdapter implements NetPoolListener {
  @Override
  public void netState(Network<?, ?> network, NetEvent<?, ?> event, Network.State state) {

  }

  @Override
  public void netResult(Network<?, ?> network, NetResultEvent<?, ?> result) {

  }

  @Override
  public void netError(Network<?, ?> network, NetErrorEvent<?, ?> error) {

  }
}
