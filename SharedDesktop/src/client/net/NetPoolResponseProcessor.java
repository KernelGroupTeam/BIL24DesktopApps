package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 08.12.15
 */
public interface NetPoolResponseProcessor {

  void processResponse(Network<?, ?> network, String answer, Object response);
}
