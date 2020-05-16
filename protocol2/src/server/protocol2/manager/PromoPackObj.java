package server.protocol2.manager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.03.19
 */
public class PromoPackObj implements Filterable, Serializable {
  private static final long serialVersionUID = 8539435998834191027L;
  private static final DateFormat formatDDMMYYYYHHmm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  private long id;
  private long organizerId;
  @NotNull
  private String name = "";
  @NotNull
  private String description = "";
  @NotNull
  private String startTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private String endTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private BigDecimal discountPercent = BigDecimal.ZERO;
  @Nullable
  private BigDecimal maxDiscountSum;
  @Nullable
  private Integer maxTickets;
  @Nullable
  private BigDecimal minPrice;
  @NotNull
  private List<PromoAction> actionList = Collections.emptyList();
  @NotNull
  private List<PromoActionEvent> actionEventList = Collections.emptyList();
  @NoLogging
  @NotNull
  private List<PromoCodeObj> promoCodeList = Collections.emptyList();
  private boolean completed;

  public PromoPackObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public long getOrganizerId() {
    return organizerId;
  }

  public void setOrganizerId(long organizerId) {
    this.organizerId = organizerId;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(@NotNull String description) {
    this.description = description;
  }

  @NotNull
  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(@NotNull String startTime) {
    this.startTime = startTime;
  }

  @NotNull
  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(@NotNull String endTime) {
    this.endTime = endTime;
  }

  @NotNull
  public BigDecimal getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(@NotNull BigDecimal discountPercent) {
    this.discountPercent = discountPercent;
  }

  @Nullable
  public BigDecimal getMaxDiscountSum() {
    return maxDiscountSum;
  }

  public void setMaxDiscountSum(@Nullable BigDecimal maxDiscountSum) {
    this.maxDiscountSum = maxDiscountSum;
  }

  @Nullable
  public Integer getMaxTickets() {
    return maxTickets;
  }

  public void setMaxTickets(@Nullable Integer maxTickets) {
    this.maxTickets = maxTickets;
  }

  @Nullable
  public BigDecimal getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(@Nullable BigDecimal minPrice) {
    this.minPrice = minPrice;
  }

  @NotNull
  public List<PromoAction> getActionList() {
    return actionList;
  }

  public void setActionList(@NotNull List<PromoAction> actionList) {
    this.actionList = actionList;
  }

  @NotNull
  public List<PromoActionEvent> getActionEventList() {
    return actionEventList;
  }

  public void setActionEventList(@NotNull List<PromoActionEvent> actionEventList) {
    this.actionEventList = actionEventList;
  }

  @NotNull
  public List<PromoCodeObj> getPromoCodeList() {
    return promoCodeList;
  }

  public void addPromoCodeList(@NotNull List<PromoCodeObj> promoCodeList) {
    List<PromoCodeObj> newPromoCodeList = new ArrayList<>(this.promoCodeList.size() + promoCodeList.size());
    newPromoCodeList.addAll(this.promoCodeList);
    newPromoCodeList.addAll(promoCodeList);
    this.promoCodeList = newPromoCodeList;
  }

  public void setPromoCodeList(@NotNull List<PromoCodeObj> promoCodeList) {
    this.promoCodeList = promoCodeList;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    if (filter == null) return false;
    if (!(filter instanceof AuthorityObj)) return false;
    AuthorityObj authority = (AuthorityObj) filter;
    return authority.getId() == getOrganizerId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PromoPackObj)) return false;
    PromoPackObj that = (PromoPackObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name + (!completed ? "" : " [Завершена]");
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmm.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmm.parse(date);
  }
}
