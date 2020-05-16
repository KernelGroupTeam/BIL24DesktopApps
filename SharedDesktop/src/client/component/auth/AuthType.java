package client.component.auth;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.16
 */
public class AuthType {
  private final int id;
  @NotNull
  private final String name;

  public AuthType(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthType)) return false;
    AuthType authType = (AuthType) o;
    return id == authType.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
