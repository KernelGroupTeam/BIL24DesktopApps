package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 08.12.15
 */
public interface NetRequestProcessor<Req, Res> {

  void processRequest(Network<Req, Res> network, String command, Req request);
}
