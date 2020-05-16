package server.protocol2.manager;

import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.04.16
 */
public class OrganizerObj extends AuthorityObj {
  private static final long serialVersionUID = -5352162191294433846L;
  @Nullable
  private AgentOrg acqAgent;
  @NotNull
  private List<AgentOrg> agentList = Collections.emptyList();
  @NotNull
  private List<PromoAction> actionList = Collections.emptyList();

  public OrganizerObj(long id, @NotNull Entity entity) {
    super(id, AuthTypeObj.ORGANIZER, entity);
  }

  @Nullable
  public AgentOrg getAcqAgent() {
    return acqAgent;
  }

  public void setAcqAgent(@Nullable AgentOrg acqAgent) {
    this.acqAgent = acqAgent;
  }

  @NotNull
  public List<AgentOrg> getAgentList() {
    return agentList;
  }

  public void setAgentList(@NotNull List<AgentOrg> agentList) {
    this.agentList = agentList;
  }

  @NotNull
  public List<PromoAction> getActionList() {
    return actionList;
  }

  public void setActionList(@NotNull List<PromoAction> actionList) {
    this.actionList = actionList;
  }
}
