package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 08.12.15
 */
public interface NetResponseProcessor<Req, Res> {

  void processResponse(Network<Req, Res> network, String answer, Res response);
}
