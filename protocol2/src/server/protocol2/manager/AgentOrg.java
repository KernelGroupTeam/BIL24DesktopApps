package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import server.protocol2.common.AcquiringObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.04.16
 */
public class AgentOrg implements Serializable {
  private static final long serialVersionUID = 214300148308776507L;
  private long id;
  @NotNull
  private AuthorityObj.Entity entity;
  @NotNull
  private String name;
  @NotNull
  private String inn = "";
  @NotNull
  private AcquiringObj acquiring = AcquiringObj.getNone();
  private boolean disabled = false;

  public AgentOrg(long id, @NotNull AuthorityObj.Entity entity, @NotNull String name) {
    this.id = id;
    this.entity = entity;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public AuthorityObj.Entity getEntity() {
    return entity;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getInn() {
    return inn;
  }

  public void setInn(@NotNull String inn) {
    this.inn = inn;
  }

  @NotNull
  public AcquiringObj getAcquiring() {
    return acquiring;
  }

  public void setAcquiring(@NotNull AcquiringObj acquiring) {
    this.acquiring = acquiring;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AgentOrg)) return false;
    AgentOrg agentOrg = (AgentOrg) o;
    return id == agentOrg.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name + (disabled ? " [Отключен]" : "");
  }
}
