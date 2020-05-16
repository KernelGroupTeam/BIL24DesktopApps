package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 08.06.2018
 */
public class CassaUser implements Serializable {
  private static final long serialVersionUID = -1581380117168937145L;
  @NotNull
  private CassaFrontend frontend;//Фронтенд(интерфейс)
  private long userId;//Идентификатор пользователя
  @NotNull
  private String sessionId;//Идентификатор сессии

  public CassaUser(@NotNull CassaFrontend frontend, long userId, @NotNull String sessionId) {
    this.frontend = frontend;
    this.userId = userId;
    this.sessionId = sessionId;
  }

  @NotNull
  public CassaFrontend getFrontend() {
    return frontend;
  }

  public long getUserId() {
    return userId;
  }

  @NotNull
  public String getSessionId() {
    return sessionId;
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CassaUser)) return false;
    CassaUser that = (CassaUser) o;
    if (userId != that.userId) return false;
    return frontend.equals(that.frontend);
  }

  @Override
  public int hashCode() {
    int result = frontend.hashCode();
    result = 31 * result + (int) (userId ^ (userId >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "CassaUser{frontend=" + frontend + ", userId=" + userId + ", sessionId=" + sessionId + '}';
  }
}
