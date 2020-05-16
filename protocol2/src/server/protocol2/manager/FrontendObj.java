package server.protocol2.manager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.common.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.01.16
 */
public class FrontendObj implements Filterable, Serializable {
  private static final long serialVersionUID = -361374005279563999L;
  private long id;
  private long agentId;
  @NotNull
  private String agentName = "";
  @NotNull
  private FrontendType type;
  @NotNull
  private String name = "";
  @NoLogging
  @NotNull
  private String token = "";
  @Nullable
  private BigDecimal chargePercent;
  private boolean ignoreAllCharge = false;
  @NoLogging
  @NotNull
  private List<SubsActionObj> subsActionList = Collections.emptyList();
  @NoLogging
  @NotNull
  private Set<Long> allowedOrganizerSet = Collections.emptySet();//todo temp
  @NoLogging
  @NotNull
  private Map<Long, String> allowedOrganizerMap = Collections.emptyMap();
  private boolean onlySubscriptions;
  private boolean disabled = false;

  public FrontendObj(long id, @NotNull FrontendType type) {
    this.id = id;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public long getAgentId() {
    return agentId;
  }

  public void setAgentId(long agentId) {
    this.agentId = agentId;
  }

  @NotNull
  public String getAgentName() {
    return agentName;
  }

  public void setAgentName(@NotNull String agentName) {
    this.agentName = agentName;
  }

  @NotNull
  public FrontendType getType() {
    return type;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getToken() {
    return token;
  }

  public void setToken(@NotNull String token) {
    this.token = token;
  }

  @Nullable
  public BigDecimal getChargePercent() {
    return chargePercent;
  }

  public void setChargePercent(@Nullable BigDecimal chargePercent) {
    this.chargePercent = chargePercent;
  }

  public boolean isIgnoreAllCharge() {
    return ignoreAllCharge;
  }

  public void setIgnoreAllCharge(boolean ignoreAllCharge) {
    this.ignoreAllCharge = ignoreAllCharge;
  }

  @NotNull
  public List<SubsActionObj> getSubsActionList() {
    return subsActionList;
  }

  public void setSubsActionList(@NotNull List<SubsActionObj> subsActionList) {
    this.subsActionList = subsActionList;
  }

  @NotNull
  public Set<Long> getAllowedOrganizerSet() {
    return allowedOrganizerMap.keySet();
  }

  public void setAllowedOrganizerSet(@NotNull Set<Long> allowedOrganizerSet) {
    this.allowedOrganizerSet = new HashSet<>(allowedOrganizerSet);
  }

  @NotNull
  public Map<Long, String> getAllowedOrganizerMap() {
    return allowedOrganizerMap;
  }

  public void setAllowedOrganizerMap(@NotNull Map<Long, String> allowedOrganizerMap) {
    this.allowedOrganizerMap = allowedOrganizerMap;
  }

  public boolean isOnlySubscriptions() {
    return onlySubscriptions;
  }

  public void setOnlySubscriptions(boolean onlySubscriptions) {
    this.onlySubscriptions = onlySubscriptions;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof AuthorityObj)) return false;
    AuthorityObj authority = (AuthorityObj) filter;
    return authority.getId() == getAgentId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FrontendObj)) return false;
    FrontendObj frontendObj = (FrontendObj) o;
    return id == frontendObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "[" + id + "][" + type.getName() + "] " + name + (disabled ? " [Отключен]" : "");
  }
}
