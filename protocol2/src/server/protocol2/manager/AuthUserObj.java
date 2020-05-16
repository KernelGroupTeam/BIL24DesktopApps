package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.04.16
 */
public class AuthUserObj implements Filterable, Serializable {
  private static final long serialVersionUID = -8481281876934538363L;
  private long id;
  private long authorityId;
  @NotNull
  private String authorityName = "";
  @NotNull
  private String email = "";
  @NotNull
  private String name = "";
  @NotNull
  private String surname = "";
  @NoLogging
  private boolean activated;
  private boolean disabled;
  @NotNull
  private AuthTypeObj type;

  public AuthUserObj(long id, @NotNull AuthTypeObj type) {
    this.id = id;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public long getAuthorityId() {
    return authorityId;
  }

  public void setAuthorityId(long authorityId) {
    this.authorityId = authorityId;
  }

  @NotNull
  public String getAuthorityName() {
    return authorityName;
  }

  public void setAuthorityName(@NotNull String authorityName) {
    this.authorityName = authorityName;
  }

  @NotNull
  public String getEmail() {
    return email;
  }

  public void setEmail(@NotNull String email) {
    this.email = email;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getSurname() {
    return surname;
  }

  public void setSurname(@NotNull String surname) {
    this.surname = surname;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @NotNull
  public AuthTypeObj getType() {
    return type;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof AuthorityObj)) return false;
    AuthorityObj authority = (AuthorityObj) filter;
    return authority.getId() == getAuthorityId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthUserObj)) return false;
    AuthUserObj that = (AuthUserObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name + " " + surname + (disabled ? " [Отключен]" : "") + (!activated ? " [Не активирован]" : "");
  }
}
