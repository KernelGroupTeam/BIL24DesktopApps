package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;
import server.protocol2.common.FrontendType;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.01.16
 */
public class RFrontend implements Filterable, Serializable {
  private static final long serialVersionUID = -5514894114121298124L;
  private long id;
  private transient String idStr;
  private long agentId;
  @NotNull
  private String name;
  @NotNull
  private FrontendType type;

  public RFrontend(long id, long agentId, @NotNull String name, @NotNull FrontendType type) {
    this.id = id;
    this.agentId = agentId;
    this.name = name;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getIdStr() {
    if (idStr != null) return idStr;
    idStr = String.valueOf(id);
    return idStr;
  }

  public long getAgentId() {
    return agentId;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public FrontendType getType() {
    return type;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof RAgent)) return false;
    RAgent agent = (RAgent) filter;
    return agent.getId() == 0 || agent.getId() == getAgentId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RFrontend)) return false;
    RFrontend rFrontend = (RFrontend) o;
    return id == rFrontend.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name;
  }
}
