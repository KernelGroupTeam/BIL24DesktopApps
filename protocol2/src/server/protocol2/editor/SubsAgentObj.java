package server.protocol2.editor;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;
import server.protocol2.common.SubsActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.03.16
 */
public class SubsAgentObj implements Filterable, Serializable {
  private static final long serialVersionUID = 8098402790486228978L;
  private long id;
  @NotNull
  private String name;
  @NotNull
  private List<SubsActionObj> subsActionList = Collections.emptyList();
  private boolean onlySubscriptions;

  public SubsAgentObj(long id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public List<SubsActionObj> getSubsActionList() {
    return subsActionList;
  }

  public void setSubsActionList(@NotNull List<SubsActionObj> subsActionList) {
    this.subsActionList = subsActionList;
  }

  public boolean isOnlySubscriptions() {
    return onlySubscriptions;
  }

  public void setOnlySubscriptions(boolean onlySubscriptions) {
    this.onlySubscriptions = onlySubscriptions;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SubsAgentObj)) return false;
    SubsAgentObj that = (SubsAgentObj) o;
    return id == that.id;
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
