package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.11.15
 */
public class NetErrorEvent<Req, Res> extends NetEvent<Req, Res> {
  private final boolean dataSent;
  private final Exception exception;

  NetErrorEvent(Network<Req, Res> source, String command, Req request, boolean dataSent, Exception exception) {
    super(source, command, request);
    this.dataSent = dataSent;
    this.exception = exception;
  }

  public boolean isDataSent() {
    return dataSent;
  }

  public Exception getException() {
    return exception;
  }
}
