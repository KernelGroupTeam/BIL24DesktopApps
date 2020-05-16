package server.protocol2.common;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import server.protocol2.UserType;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.04.16
 */
public class LoginUser implements Serializable {
  private static final long serialVersionUID = 8096926472835142433L;
  private final long id;
  private final long authorityId;
  @NotNull
  private final String email;
  @NotNull
  private final String name;
  @NotNull
  private final String surname;
  @NotNull
  private final String authorityName;
  @NotNull
  private final UserType userType;

  public LoginUser(long id, long authorityId, @NotNull String email, @NotNull String name, @NotNull String surname,
                   @NotNull String authorityName, @NotNull UserType userType) {
    this.id = id;
    this.authorityId = authorityId;
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.authorityName = authorityName;
    this.userType = userType;
  }

  public long getId() {
    return id;
  }

  public long getAuthorityId() {
    return authorityId;
  }

  @NotNull
  public String getEmail() {
    return email;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getSurname() {
    return surname;
  }

  @NotNull
  public String getAuthorityName() {
    return authorityName;
  }

  @NotNull
  public UserType getUserType() {
    return userType;
  }
}
