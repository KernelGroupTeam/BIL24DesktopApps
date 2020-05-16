package server.protocol2.manager;

import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import server.protocol2.NoLogging;
import server.protocol2.common.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.04.16
 */
public class AgentObj extends AuthorityObj {
  private static final long serialVersionUID = 66168217069543857L;
  @NotNull
  private Set<FrontendType> frontendTypeSet = Collections.emptySet();
  @NotNull
  private BigDecimal chargePercent = BigDecimal.ZERO;
  @NoLogging
  @NotNull
  private List<SubsActionObj> subsActionList = Collections.emptyList();
  @NotNull
  private AcquiringObj acquiring = AcquiringObj.getNone();
  @NotNull
  private VatValue nominalVat = VatValue.getUnknown();
  @NotNull
  private VatValue chargeVat = VatValue.getUnknown();

  public AgentObj(long id, @NotNull Entity entity) {
    super(id, AuthTypeObj.AGENT, entity);
  }

  @NotNull
  public Set<FrontendType> getFrontendTypeSet() {
    return frontendTypeSet;
  }

  public void setFrontendTypeSet(@NotNull Set<FrontendType> frontendTypeSet) {
    this.frontendTypeSet = frontendTypeSet;
  }

  @NotNull
  public BigDecimal getChargePercent() {
    return chargePercent;
  }

  public void setChargePercent(@NotNull BigDecimal chargePercent) {
    this.chargePercent = chargePercent;
  }

  @NotNull
  public List<SubsActionObj> getSubsActionList() {
    return subsActionList;
  }

  public void setSubsActionList(@NotNull List<SubsActionObj> subsActionList) {
    this.subsActionList = subsActionList;
  }

  @NotNull
  public AcquiringObj getAcquiring() {
    return acquiring;
  }

  public void setAcquiring(@NotNull AcquiringObj acquiring) {
    this.acquiring = acquiring;
  }

  @NotNull
  public VatValue getNominalVat() {
    return nominalVat;
  }

  public void setNominalVat(@NotNull VatValue nominalVat) {
    this.nominalVat = nominalVat;
  }

  @NotNull
  public VatValue getChargeVat() {
    return chargeVat;
  }

  public void setChargeVat(@NotNull VatValue chargeVat) {
    this.chargeVat = chargeVat;
  }
}
