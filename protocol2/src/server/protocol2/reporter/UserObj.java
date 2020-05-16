package server.protocol2.reporter;

import java.io.Serializable;
import java.text.*;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.10.15
 */
public class UserObj implements Serializable {
  private static final long serialVersionUID = 183380859787681141L;
  private static final DateFormat formatDDMMYYYYHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private long id;
  @NotNull
  private String sessionId = "";
  @NotNull
  private String email = "";

  public UserObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(@NotNull String sessionId) {
    this.sessionId = sessionId;
  }

  @NotNull
  public String getEmail() {
    return email;
  }

  public void setEmail(@NotNull String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserObj)) return false;
    UserObj userObj = (UserObj) o;
    return id == userObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmmss.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmmss.parse(date);
  }
}
