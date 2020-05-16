package client.reporter;

import client.net.*;
import server.protocol2.Request;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 05.04.16
 */
public class NetRequestProcessor implements NetPoolRequestProcessor {
  @Override
  public void processRequest(Network<?, ?> network, String command, Object request) {
    ((Request) request).setCommand(command);
  }
}
