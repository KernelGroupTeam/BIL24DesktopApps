package client.component.auth;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.16
 */
public enum AuthZone {
  REAL, TEST;

  AuthZone opposite() {
    if (this == REAL) return TEST;
    else return REAL;
  }
}
