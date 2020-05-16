package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 08.12.15
 */
public interface NetPoolRequestProcessor {

  void processRequest(Network<?, ?> network, String command, Object request);
}
