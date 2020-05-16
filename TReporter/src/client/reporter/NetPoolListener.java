package client.reporter;

import client.net.*;
import server.protocol2.Response;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 03.12.15
 */
public class NetPoolListener extends NetPoolAdapter {
  @Override
  public void netResult(Network<?, ?> network, NetResultEvent<?, ?> result) {
    try {
      Response response = (Response) result.getResponse();
      System.out.println(result.getTelemetry() + " Execution time: " + response.getExecutionTime() + " ms");
      if (!response.isSuccess()) {
        String exceptionStackTrace = response.getExceptionStackTrace();
        if (exceptionStackTrace != null) System.err.print(exceptionStackTrace);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void netError(Network<?, ?> network, NetErrorEvent<?, ?> error) {
    error.getException().printStackTrace();
  }
}
