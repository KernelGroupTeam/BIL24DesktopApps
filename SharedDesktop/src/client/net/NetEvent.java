package client.net;

import java.util.EventObject;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public class NetEvent<Req, Res> extends EventObject {
  private final Network<Req, Res> source;
  private final String command;
  private final Req request;

  NetEvent(Network<Req, Res> source, String command, Req request) {
    super(source);
    this.source = source;
    this.command = command;
    this.request = request;
  }

  @Override
  public Network<Req, Res> getSource() {
    return source;
  }

  public String getCommand() {
    return command;
  }

  public Req getRequest() {
    return request;
  }
}
