package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 30.03.2017.
 */
public class ReportSaleInfo implements Serializable {
  private static final long serialVersionUID = -2801045104487889632L;
  @NotNull
  private String actionName;
  @NotNull
  private String actionLegalOwner;
  @NotNull
  private String cityNames;
  @NotNull
  private String venueNames;
  @NotNull
  private List<EventInfo> eventInfoList;

  public ReportSaleInfo(@NotNull String actionName, @NotNull String actionLegalOwner, @NotNull String cityNames, @NotNull String venueNames, @NotNull List<EventInfo> eventInfoList) {
    this.actionName = actionName;
    this.actionLegalOwner = actionLegalOwner;
    this.cityNames = cityNames;
    this.venueNames = venueNames;
    this.eventInfoList = eventInfoList;
  }

  @NotNull
  public String getActionName() {
    return actionName;
  }

  @NotNull
  public String getActionLegalOwner() {
    return actionLegalOwner;
  }

  @NotNull
  public String getCityNames() {
    return cityNames;
  }

  @NotNull
  public String getVenueNames() {
    return venueNames;
  }

  @NotNull
  public List<EventInfo> getEventInfoList() {
    return eventInfoList;
  }

  public static class EventInfo implements Serializable {
    private static final long serialVersionUID = 987836251334978328L;
    @NotNull
    private String showTime;
    @NotNull
    private String cityName;
    @NotNull
    private String venueName;
    @NotNull
    private Map<BigDecimal, Info> categoryInfoMap;

    public EventInfo(@NotNull String showTime, @NotNull String cityName, @NotNull String venueName, @NotNull Map<BigDecimal, Info> categoryInfoMap) {
      this.showTime = showTime;
      this.cityName = cityName;
      this.venueName = venueName;
      this.categoryInfoMap = categoryInfoMap;
    }

    @NotNull
    public String getShowTime() {
      return showTime;
    }

    @NotNull
    public String getCityName() {
      return cityName;
    }

    @NotNull
    public String getVenueName() {
      return venueName;
    }

    @NotNull
    public Map<BigDecimal, Info> getCategoryInfoMap() {
      return categoryInfoMap;
    }
  }

  public static class Info implements Serializable {
    private static final long serialVersionUID = -1842160750104498531L;
    private int quotaInCount;//Получено мест (квота)
    @NotNull
    private BigDecimal quotaInSum = BigDecimal.ZERO;//Получено мест (квота) на сумму
    private int quotaOutCount;//Возвращено мест (квота)
    @NotNull
    private BigDecimal quotaOutSum = BigDecimal.ZERO;//Возвращено мест (квота) на сумму
    private int quotaReservedCount;//Забронировано мест
    @NotNull
    private BigDecimal quotaReservedSum = BigDecimal.ZERO;//Забронировано мест на сумму
    private int quotaOccupiedCount;//Продано мест
    @NotNull
    private BigDecimal quotaOccupiedSum = BigDecimal.ZERO;//Продано мест, сумма номинал минус скидка
    private int quotaAvailableCount;//Свободно мест
    @NotNull
    private BigDecimal quotaAvailableSum = BigDecimal.ZERO;//Свободно мест на сумму

    private int ticketCount;//Продано билетов всего (вместе с возвращенными)
    @NotNull
    private BigDecimal ticketSum = BigDecimal.ZERO;//Продано билетов всего (вместе с возвращенными), сумма номинал минус скидка
    private int ticketDiscountCount;//Продано билетов всего (вместе с возвращенными) по скидке
    @NotNull
    private BigDecimal ticketDiscountSum = BigDecimal.ZERO;//Продано билетов всего (вместе с возвращенными), сумма скидки
    private int ticketRefundCount;//Возвращено билетов
    @NotNull
    private BigDecimal ticketRefundSum = BigDecimal.ZERO;//Возвращено билетов, сумма номинал минус скидка
    private int ticketOccupiedCount;//Продано билетов (кроме возвращенных)
    @NotNull
    private BigDecimal ticketOccupiedSum = BigDecimal.ZERO;//Продано билетов (кроме возвращенных), сумма номинал минус скидка

    public int getQuotaInCount() {
      return quotaInCount;
    }

    @NotNull
    public BigDecimal getQuotaInSum() {
      return quotaInSum;
    }

    public void incQuotaInCount(@NotNull BigDecimal quotaInPrice) {
      this.quotaInCount++;
      quotaInSum = quotaInSum.add(quotaInPrice);
    }

    public int getQuotaOutCount() {
      return quotaOutCount;
    }

    @NotNull
    public BigDecimal getQuotaOutSum() {
      return quotaOutSum;
    }

    public void incQuotaOutCount(@NotNull BigDecimal quotaOutPrice) {
      this.quotaOutCount++;
      quotaOutSum = quotaOutSum.add(quotaOutPrice);
    }

    public int getQuotaReservedCount() {
      return quotaReservedCount;
    }

    @NotNull
    public BigDecimal getQuotaReservedSum() {
      return quotaReservedSum;
    }

    public void incQuotaReservedCount(@NotNull BigDecimal quotaReservedPrice) {
      this.quotaReservedCount++;
      quotaReservedSum = quotaReservedSum.add(quotaReservedPrice);
    }

    public int getQuotaOccupiedCount() {
      return quotaOccupiedCount;
    }

    @NotNull
    public BigDecimal getQuotaOccupiedSum() {
      return quotaOccupiedSum;
    }

    public void incQuotaOccupiedCount(@NotNull BigDecimal quotaOccupiedPrice) {
      this.quotaOccupiedCount++;
      quotaOccupiedSum = quotaOccupiedSum.add(quotaOccupiedPrice);
    }

    public int getQuotaAvailableCount() {
      return quotaAvailableCount;
    }

    @NotNull
    public BigDecimal getQuotaAvailableSum() {
      return quotaAvailableSum;
    }

    public void incQuotaAvailableCount(@NotNull BigDecimal quotaAvailablePrice) {
      this.quotaAvailableCount++;
      quotaAvailableSum = quotaAvailableSum.add(quotaAvailablePrice);
    }


    public int getTicketCount() {
      return ticketCount;
    }

    @NotNull
    public BigDecimal getTicketSum() {
      return ticketSum;
    }

    public void incTicketCount(@NotNull BigDecimal ticketPrice) {
      this.ticketCount++;
      ticketSum = ticketSum.add(ticketPrice);
    }

    public int getTicketDiscountCount() {
      return ticketDiscountCount;
    }

    @NotNull
    public BigDecimal getTicketDiscountSum() {
      return ticketDiscountSum;
    }

    public void incTicketDiscountCount(@NotNull BigDecimal ticketDiscountPrice) {
      this.ticketDiscountCount++;
      ticketDiscountSum = ticketDiscountSum.add(ticketDiscountPrice);
    }

    public int getTicketRefundCount() {
      return ticketRefundCount;
    }

    @NotNull
    public BigDecimal getTicketRefundSum() {
      return ticketRefundSum;
    }

    public void incTicketRefundCount(@NotNull BigDecimal ticketRefundPrice) {
      this.ticketRefundCount++;
      ticketRefundSum = ticketRefundSum.add(ticketRefundPrice);
    }

    public int getTicketOccupiedCount() {
      return ticketOccupiedCount;
    }

    @NotNull
    public BigDecimal getTicketOccupiedSum() {
      return ticketOccupiedSum;
    }

    public void incTicketOccupiedCount(@NotNull BigDecimal ticketOccupiedPrice) {
      this.ticketOccupiedCount++;
      ticketOccupiedSum = ticketOccupiedSum.add(ticketOccupiedPrice);
    }

    public boolean isEmpty() {
      return quotaInCount == 0 && quotaOutCount == 0;
    }
  }
}
