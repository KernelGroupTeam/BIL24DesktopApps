package server.protocol2.cassa;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 19.10.2018
 */
public class CassaActionEventActual extends CassaActionEvent {
  private static final long serialVersionUID = 6686467094894172820L;
  @Nullable
  private String placementUrl;//Ссылка на местовую схему зала. Если null значит схема безместовая
  private boolean isCombinedPlan;//true только для комбинированной схемы

  public CassaActionEventActual(long id, long showTime, @NotNull CassaVenue venue, boolean fullNameRequired, boolean phoneRequired,
                                @Nullable String placementUrl, boolean isCombinedPlan) {
    super(id, showTime, venue, fullNameRequired, phoneRequired);
    this.placementUrl = placementUrl;
    this.isCombinedPlan = isCombinedPlan;
  }

  @Nullable
  public String getPlacementUrl() {
    return placementUrl;
  }

  public boolean isCombinedPlan() {
    return isCombinedPlan;
  }

  @Override
  public String toString() {
    return "CassaActionEventActual{id=" + getId() + ", day=" + getDay() + ", time=" + getTime() + ", venue=" + getVenue() +
        ", fullNameRequired=" + isFullNameRequired() + ", phoneRequired=" + isPhoneRequired() +
        ", placementUrl=" + placementUrl + ", isCombinedPlan=" + isCombinedPlan + '}';
  }
}
