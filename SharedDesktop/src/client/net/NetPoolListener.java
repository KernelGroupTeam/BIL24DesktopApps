package client.net;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public interface NetPoolListener extends EventListener {

  void netState(Network<?, ?> network, NetEvent<?, ?> event, Network.State state);

  void netResult(Network<?, ?> network, NetResultEvent<?, ?> result);

  void netError(Network<?, ?> network, NetErrorEvent<?, ?> error);
}
