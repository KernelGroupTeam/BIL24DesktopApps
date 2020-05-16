package client.editor.model;

import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.common.BarcodeFormat;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 16.04.19
 */
public class ManualQuotaData {
  @NotNull
  private final String number;
  @NotNull
  private final Date date;
  @NotNull
  private final BarcodeFormat barcodeFormat;
  @Nullable
  private final Integer totalQty;
  @Nullable
  private final BigDecimal totalSum;
  @NotNull
  private final List<Data> dataList;

  public ManualQuotaData(@NotNull QuotaInManualObj quota, @NotNull EventSeatTableModel tableModel) {
    if (!quota.isInputComplete()) throw new IllegalArgumentException("input not complete");
    number = quota.getNumber();
    date = quota.getTempDate();
    barcodeFormat = quota.getFormat();
    totalQty = quota.getTotalQty();
    totalSum = quota.getTotalSum();
    dataList = new ArrayList<>(quota.getDataList().size());
    for (QuotaInManualObj.Data data : quota.getDataList()) {
      List<SeatLocation> seatLocationList = tableModel.getSeatLocationList(data.getSeatIdList());
      String prefCategory = (data.getPrefCategory() == null ? null : data.getPrefCategory().getName());
      dataList.add(new Data(seatLocationList, data.getPrice(), prefCategory));
    }
  }

  @Nullable
  public QuotaInManualObj createQuota(long actionEventId, @NotNull List<EventSeatObj> eventSeatList, @NotNull List<CategoryPriceObj> categoryPriceList) {
    QuotaInManualObj quota;
    if (totalQty == null || totalSum == null) {
      quota = new QuotaInManualObj(actionEventId, number, date, barcodeFormat);
    } else {
      quota = new QuotaInManualObj(actionEventId, number, date, barcodeFormat, totalQty, totalSum);
    }
    List<EventSeatObj> nonPlacementCandidateList = null;
    for (Data data : dataList) {
      Set<Long> seatIdSet = new HashSet<>(data.seatList.size());

      for (SeatLocation quotaSeat : data.seatList) {
        boolean found = false;
        if (quotaSeat.isPlacement()) {//местовое
          for (EventSeatObj eventSeat : eventSeatList) {
            SeatLocationObj seatLocation = eventSeat.getSeatLocation();
            if (seatLocation == null) continue;
            if (!quotaSeat.getNum().equals(seatLocation.getNumber())) continue;
            if (!quotaSeat.getRow().equals(seatLocation.getRow())) continue;
            if (!quotaSeat.getSector().equals(seatLocation.getSector())) continue;
            seatIdSet.add(eventSeat.getId());
            found = true;
            break;
          }
        } else {//безместовое
          if (nonPlacementCandidateList == null)
            nonPlacementCandidateList = createNonPlacementCandidateList(eventSeatList);
          if (!nonPlacementCandidateList.isEmpty()) {
            Iterator<EventSeatObj> iterator = nonPlacementCandidateList.iterator();
            EventSeatObj eventSeat = iterator.next();
            iterator.remove();
            seatIdSet.add(eventSeat.getId());
            found = true;
          }
        }
        if (!found) return null;
      }

      CategoryPriceObj prefCategory = null;
      if (data.prefCategory != null) {
        boolean found = false;
        for (CategoryPriceObj categoryPrice : categoryPriceList) {
          if (categoryPrice.getName().equals(data.prefCategory)) {
            prefCategory = categoryPrice;
            found = true;
            break;
          }
        }
        if (!found) return null;
      }
      quota.addData(seatIdSet, data.price, prefCategory);
    }
    quota.setInputComplete();
    return quota;
  }

  @NotNull
  private static List<EventSeatObj> createNonPlacementCandidateList(@NotNull List<EventSeatObj> eventSeatList) {
    List<EventSeatObj> result = new ArrayList<>();
    for (EventSeatObj eventSeat : eventSeatList) {
      if (eventSeat.getSeatLocation() != null) continue;
      if ((eventSeat.getState() == EventSeatObj.State.INACCESSIBLE && eventSeat.getBarcode() == null) || eventSeat.getState() == EventSeatObj.State.REFUND) {
        result.add(eventSeat);
      }
    }
    return result;
  }

  private static class Data {
    @NotNull
    private final List<SeatLocation> seatList;
    @NotNull
    private final BigDecimal price;
    @Nullable
    private final String prefCategory;

    public Data(@NotNull List<SeatLocation> seatList, @NotNull BigDecimal price, @Nullable String prefCategory) {
      this.seatList = seatList;
      this.price = price;
      this.prefCategory = prefCategory;
    }
  }
}
