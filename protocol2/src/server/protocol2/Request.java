package server.protocol2;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.12.15
 */
public class Request implements Serializable {
  private static final long serialVersionUID = -43594862442696985L;
  private static Header defHeader;
  private final long timestamp = System.currentTimeMillis();
  @NotNull
  private final Header header;
  private final Object data;
  @NotNull
  private String command = "";

  public Request(Object data) {
    if (defHeader == null) throw new IllegalStateException("default header wasn't set");
    this.header = defHeader;
    this.data = data;
  }

  public Request(@NotNull App app, @NotNull String version, int build, @NotNull String login, @NotNull UserType userType, Object data) {
    this(new Header(app, version, build, login, userType), data);
  }

  public Request(@NotNull Header header, Object data) {
    this.header = header;
    this.data = data;
  }

  public long getTimestamp() {
    return timestamp;
  }

  @NotNull
  public Header getHeader() {
    return header;
  }

  public Object getData() {
    return data;
  }

  @NotNull
  public String getCommand() {
    return command;
  }

  public void setCommand(@NotNull String command) {
    this.command = command;
  }

  public static void setDefHeader(Header defHeader) {
    Request.defHeader = defHeader;
  }

  public static void setDefHeader(App app, String version, int build, String login, @NotNull UserType userType) {
    Request.defHeader = new Header(app, version, build, login, userType);
  }

  public static class Header implements Serializable {
    private static final long serialVersionUID = 3413336827449238854L;
    @NotNull
    private final App app;
    @NotNull
    private final String version;
    private final int build;
    @NotNull
    private final String login;
    @NotNull
    private final UserType userType;

    public Header(@NotNull App app, @NotNull String version, int build, @NotNull String login, @NotNull UserType userType) {
      this.app = app;
      this.version = version;
      this.build = build;
      this.login = login;
      this.userType = userType;
    }

    @NotNull
    public App getApp() {
      return app;
    }

    @NotNull
    public String getVersion() {
      return version;
    }

    public int getBuild() {
      return build;
    }

    @NotNull
    public String getLogin() {
      return login;
    }

    @NotNull
    public UserType getUserType() {
      return userType;
    }
  }
}
