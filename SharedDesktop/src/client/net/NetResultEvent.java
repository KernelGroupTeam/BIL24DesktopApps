package client.net;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.11.15
 */
public class NetResultEvent<Req, Res> extends NetEvent<Req, Res> {
  private final String answer;
  private final Res response;
  private final NetTelemetry telemetry;

  NetResultEvent(Network<Req, Res> source, String command, Req request, String answer, Res response, NetTelemetry telemetry) {
    super(source, command, request);
    this.answer = answer;
    this.response = response;
    this.telemetry = telemetry;
  }

  public String getAnswer() {
    return answer;
  }

  public Res getResponse() {
    return response;
  }

  public NetTelemetry getTelemetry() {
    return telemetry;
  }
}
