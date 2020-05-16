package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public abstract class NetAdapter<Req, Res> implements NetListener<Req, Res> {
  @Override
  public void netState(NetEvent<Req, Res> event, Network.State state) {

  }

  @Override
  public void netResult(NetResultEvent<Req, Res> result) {

  }

  @Override
  public void netError(NetErrorEvent<Req, Res> error) {

  }
}
