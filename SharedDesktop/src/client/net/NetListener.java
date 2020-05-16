package client.net;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public interface NetListener<Req, Res> extends EventListener {

  void netState(NetEvent<Req, Res> event, Network.State state);

  void netResult(NetResultEvent<Req, Res> result);

  void netError(NetErrorEvent<Req, Res> error);
}
