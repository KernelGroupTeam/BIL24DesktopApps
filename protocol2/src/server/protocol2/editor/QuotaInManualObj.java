package server.protocol2.editor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.BarcodeFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.01.17
 */
public class QuotaInManualObj extends QuotaInObj {
  private static final long serialVersionUID = 4188695350746749097L;
  @NotNull
  private BarcodeFormat format;
  @NotNull
  private Map<Long, PriceCat> seatIdPriceCatMap = new HashMap<>();
  @NotNull
  private final transient Date tempDate;
  @NotNull
  private final transient List<Data> dataList = new ArrayList<>();
  @Nullable
  private final transient Integer totalQty;
  @Nullable
  private final transient BigDecimal totalSum;
  private transient boolean inputComplete;

  public QuotaInManualObj(long actionEventId, @NotNull String number, @NotNull Date date, @NotNull BarcodeFormat format) {
    this(actionEventId, number, date, format, null, null, false);
  }

  public QuotaInManualObj(long actionEventId, @NotNull String number, @NotNull Date date, @NotNull BarcodeFormat format, int totalQty, @NotNull BigDecimal totalSum) {
    this(actionEventId, number, date, format, totalQty, totalSum, false);
  }

  private QuotaInManualObj(long actionEventId, @NotNull String number, @NotNull Date date, @NotNull BarcodeFormat format,
                           @Nullable Integer totalQty, @Nullable BigDecimal totalSum, @SuppressWarnings("UnusedParameters") boolean foo) {
    super(actionEventId, number, date);
    this.format = format;
    this.tempDate = date;
    this.totalQty = totalQty;
    this.totalSum = totalSum;
    this.inputComplete = false;
  }

  @NotNull
  public BarcodeFormat getFormat() {
    return format;
  }

  @NotNull
  public Map<Long, PriceCat> getSeatIdPriceCatMap() {
    return seatIdPriceCatMap;
  }

  @NotNull
  public Date getTempDate() {
    return tempDate;
  }

  @NotNull
  public List<Data> getDataList() {
    return dataList;
  }

  public void addData(@NotNull Set<Long> seatIdSet, @NotNull BigDecimal price, @Nullable CategoryPriceObj prefCategory) {
    if (inputComplete) throw new IllegalStateException("input complete");
    dataList.add(new Data(seatIdSet, price, prefCategory));
  }

  public void removeData(@NotNull Data data) {
    if (inputComplete) throw new IllegalStateException("input complete");
    boolean result = dataList.remove(data);
    if (!result) throw new IllegalArgumentException();
    for (Long seatId : data.getSeatIdList()) {
      seatIdPriceCatMap.remove(seatId);
    }
  }

  public boolean checkIntersection(@NotNull Set<Long> seatIdSet) {
    for (Long seatId : seatIdSet) {
      if (seatIdPriceCatMap.containsKey(seatId)) return true;
    }
    return false;
  }

  @Nullable
  public Integer getTotalQty() {
    return totalQty;
  }

  @Nullable
  public BigDecimal getTotalSum() {
    return totalSum;
  }

  public boolean isInputComplete() {
    return inputComplete;
  }

  public void setInputComplete() {
    if (!checkSum()) throw new IllegalStateException("check sum");
    inputComplete = true;
  }

  @SuppressWarnings("RedundantIfStatement")
  private boolean checkSum() {
    if (totalQty == null || totalSum == null) return true;

    int quantity = 0;
    for (Data data : dataList) {
      quantity += data.getQuantity();
    }
    if (quantity != totalQty) return false;

    BigDecimal sum = BigDecimal.ZERO;
    for (Data data : dataList) {
      sum = sum.add(data.getSum());
    }
    if (sum.compareTo(totalSum) != 0) return false;

    return true;
  }

  public class PriceCat implements Serializable {
    @NotNull
    private BigDecimal price;
    @Nullable
    private Long prefCategoryId;

    public PriceCat(@NotNull BigDecimal price, @Nullable Long prefCategoryId) {
      this.price = price;
      this.prefCategoryId = prefCategoryId;
    }

    @NotNull
    public BigDecimal getPrice() {
      return price;
    }

    @Nullable
    public Long getPrefCategoryId() {
      return prefCategoryId;
    }
  }

  public class Data {
    @NotNull
    private final List<Long> seatIdList;
    @NotNull
    private BigDecimal price;
    @NotNull
    private BigDecimal sum;
    @Nullable
    private CategoryPriceObj prefCategory;

    Data(@NotNull Set<Long> seatIdSet, @NotNull BigDecimal price, @Nullable CategoryPriceObj prefCategory) {
      if (checkIntersection(seatIdSet)) throw new IllegalArgumentException();
      for (Long seatId : seatIdSet) {
        seatIdPriceCatMap.put(seatId, new PriceCat(price, prefCategory == null ? null : prefCategory.getId()));
      }
      this.seatIdList = new ArrayList<>(seatIdSet);
      this.price = price;
      this.sum = price.multiply(new BigDecimal(seatIdList.size()));
      this.prefCategory = prefCategory;
    }

    @NotNull
    public List<Long> getSeatIdList() {
      return Collections.unmodifiableList(seatIdList);
    }

    public int getQuantity() {
      return seatIdList.size();
    }

    @NotNull
    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(@NotNull BigDecimal price) {
      for (Long seatId : seatIdList) {
        seatIdPriceCatMap.put(seatId, new PriceCat(price, prefCategory == null ? null : prefCategory.getId()));
      }
      this.price = price;
      this.sum = price.multiply(new BigDecimal(seatIdList.size()));
    }

    @NotNull
    public BigDecimal getSum() {
      return sum;
    }

    @Nullable
    public CategoryPriceObj getPrefCategory() {
      return prefCategory;
    }

    public void setPrefCategory(@Nullable CategoryPriceObj prefCategory) {
      for (Long seatId : seatIdList) {
        seatIdPriceCatMap.put(seatId, new PriceCat(price, prefCategory == null ? null : prefCategory.getId()));
      }
      this.prefCategory = prefCategory;
    }
  }
}
