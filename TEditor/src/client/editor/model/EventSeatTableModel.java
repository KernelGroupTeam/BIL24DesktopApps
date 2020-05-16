package client.editor.model;

import java.math.BigDecimal;
import java.util.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.jetbrains.annotations.*;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.07.15
 */
public class EventSeatTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"ID", "Сектор", "Ряд", "Место", "Категория", "Цена", "Штрих-код", "Состояние"};
  private static final Class<?>[] columnClasses = {Long.class, String.class, String.class, String.class, String.class, BigDecimal.class, String.class, String.class};
  private static final String separator = "|";
  private static final String EBS_INACCESSIBLE = "Не продается";
  private Object[][] data = new Object[0][0];
  @NotNull
  private final List<EventSeatObj> eventSeatList = new ArrayList<>();
  @NotNull
  private final List<CategoryPriceObj> categoryPriceList = new ArrayList<>();
  @NotNull
  private final List<CategoryPriceObj> categoryPricePlacementList = new ArrayList<>();
  @NotNull
  private final List<CategoryPriceObj> categoryPriceNonPlacementList = new ArrayList<>();
  @NotNull
  private final Set<Long> ebsNotAvailIdSet = new HashSet<>();
  @NotNull
  private final Map<Long, EventSeatObj> eventSeatMap = new HashMap<>();//seatId -> EventSeat (местовые и безместовые)
  @NotNull
  private final Map<Long, Integer> seatIndexMap = new HashMap<>();//seatId -> rowIndex (только местовые)
  @NotNull
  private final Map<String, List<Integer>> rowIndexMap = new HashMap<>();//sector|row -> List<rowIndex> (только местовые)
  @NotNull
  private final Map<String, List<Integer>> sectorIndexMap = new HashMap<>();//sector -> List<rowIndex> (только местовые)
  private boolean ebsView = true;
  private boolean barcodeUsed;

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return data[rowIndex][columnIndex];
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnClasses[columnIndex];
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  public void setData(@NotNull List<EventSeatObj> eventSeatList, @NotNull List<CategoryPriceObj> categoryPriceList, @Nullable Set<Long> ebsNotAvailIdSet) {
    this.eventSeatList.clear();
    this.eventSeatList.addAll(eventSeatList);
    this.categoryPriceList.clear();
    categoryPricePlacementList.clear();
    categoryPriceNonPlacementList.clear();
    for (CategoryPriceObj categoryPrice : categoryPriceList) {
      this.categoryPriceList.add(categoryPrice);
      if (categoryPrice.isPlacement()) categoryPricePlacementList.add(categoryPrice);
      else categoryPriceNonPlacementList.add(categoryPrice);
    }
    this.ebsNotAvailIdSet.clear();
    if (ebsNotAvailIdSet != null) this.ebsNotAvailIdSet.addAll(ebsNotAvailIdSet);
    else ebsView = false;
    eventSeatMap.clear();
    seatIndexMap.clear();
    rowIndexMap.clear();
    sectorIndexMap.clear();

    Object[][] buffer = new Object[eventSeatList.size() + 1][columnNames.length];
    BigDecimal sum = BigDecimal.ZERO;
    barcodeUsed = false;
    for (int i = 0; i < eventSeatList.size(); i++) {
      EventSeatObj element = eventSeatList.get(i);
      eventSeatMap.put(element.getId(), element);

      SeatLocationObj location = element.getSeatLocation();
      if (location != null) {
        seatIndexMap.put(element.getId(), i);

        String row = location.getSector() + separator + location.getRow();
        List<Integer> rowList = rowIndexMap.get(row);
        if (rowList == null) {
          rowList = new ArrayList<>();
          rowIndexMap.put(row, rowList);
        }
        rowList.add(i);

        String sector = location.getSector();
        List<Integer> sectorList = sectorIndexMap.get(sector);
        if (sectorList == null) {
          sectorList = new ArrayList<>();
          sectorIndexMap.put(sector, sectorList);
        }
        sectorList.add(i);
      }

      if (!element.isUndefinedCategory()) sum = sum.add(element.getCategoryPrice().getPrice());
      if (!barcodeUsed && element.getBarcode() != null) barcodeUsed = true;
      buffer[i][0] = element.getId();
      SeatLocationObj seatLocation = element.getSeatLocation();
      if (seatLocation != null) {
        buffer[i][1] = seatLocation.getSector();
        buffer[i][2] = seatLocation.getRow();
        buffer[i][3] = seatLocation.getNumber();
      } else {
        buffer[i][1] = "";
        buffer[i][2] = "";
        buffer[i][3] = "";
      }
      buffer[i][4] = (element.isUndefinedCategory() ? "" : element.getCategoryPrice().getName());
      buffer[i][5] = (element.isUndefinedCategory() ? null : element.getCategoryPrice().getPrice());
      buffer[i][6] = (element.getBarcode() == null ? "" : element.getBarcode());
      if (ebsView && this.ebsNotAvailIdSet.contains(element.getId())) {
        buffer[i][7] = EBS_INACCESSIBLE;
      } else {
        buffer[i][7] = element.getState().getDesc();
      }
    }
    int lastRow = buffer.length - 1;
    buffer[lastRow][3] = "Итого: " + eventSeatList.size();
    buffer[lastRow][5] = sum;
    boolean fullUpdate = (data.length != buffer.length);
    if (eventSeatList.isEmpty()) data = new Object[0][0];
    else data = buffer;
    if (fullUpdate) fireTableDataChanged();
    else fireTableRowsUpdated(0, data.length - 1);
  }

  public boolean setEbsView(boolean ebsView) {
    if (this.ebsView == ebsView) return false;
    this.ebsView = ebsView;
    for (int i = 0; i < eventSeatList.size(); i++) {
      EventSeatObj element = eventSeatList.get(i);
      if (ebsView && ebsNotAvailIdSet.contains(element.getId())) {
        data[i][7] = EBS_INACCESSIBLE;
      } else {
        data[i][7] = element.getState().getDesc();
      }
    }
    fireTableChanged(new TableModelEvent(this, 0, data.length - 1, 7, TableModelEvent.UPDATE));
    return true;
  }

  public boolean isBarcodeUsed() {
    return barcodeUsed;
  }

  @Nullable
  public EventSeatObj.State getWithEbsState(int row) {
    if (row < eventSeatList.size()) {
      EventSeatObj element = eventSeatList.get(row);
      if (ebsView && ebsNotAvailIdSet.contains(element.getId())) return EventSeatObj.State.INACCESSIBLE;
      else return element.getState();
    }
    return null;
  }

  @Nullable
  public Boolean isUndefinedCategory(int row) {
    if (row < eventSeatList.size()) return eventSeatList.get(row).isUndefinedCategory();
    return null;
  }

  @Nullable
  public EventSeatObj.State getCommonState(int[] rows) {//если все строки одного статуса, возвращаем его, иначе null
    EventSeatObj.State result = null;
    int size = eventSeatList.size();
    for (int row : rows) {
      if (row < size) {
        EventSeatObj eventSeat = eventSeatList.get(row);
        EventSeatObj.State state = eventSeat.getState();
        if (result == null) result = state;
        else if (result != state) return null;
      }
    }
    return result;
  }

  @Nullable
  public Boolean getCommonPlacement(int[] rows) {//если все строки одного placement, возвращаем его, иначе null
    Boolean result = null;
    int size = eventSeatList.size();
    for (int row : rows) {
      if (row < size) {
        EventSeatObj eventSeat = eventSeatList.get(row);
        boolean placement = eventSeat.getCategoryPrice().isPlacement();
        if (result == null) result = placement;
        else if (result != placement) return null;
      }
    }
    return result;
  }

  @Nullable
  public Long getEventSeatId(int row) {
    if (row < eventSeatList.size()) return eventSeatList.get(row).getId();
    return null;
  }

  @NotNull
  public List<EventSeatObj> getEventSeatList(int[] rows) {
    List<EventSeatObj> result = new ArrayList<>();
    int size = eventSeatList.size();
    for (int row : rows) {
      if (row < size) result.add(eventSeatList.get(row));
    }
    return result;
  }

  public void setEventSeatIds(int[] rows, @NotNull Set<Long> placementSelection, @NotNull Set<Long> allSelection) {
    placementSelection.clear();
    allSelection.clear();
    int size = eventSeatList.size();
    for (int row : rows) {
      if (row < size) {
        EventSeatObj eventSeat = eventSeatList.get(row);
        if (eventSeat.getCategoryPrice().isPlacement()) placementSelection.add(eventSeat.getId());
        allSelection.add(eventSeat.getId());
      }
    }
  }

  @NotNull
  public List<CategoryPriceObj> getCategoryPriceList() {
    return Collections.unmodifiableList(categoryPriceList);
  }

  @NotNull
  public List<CategoryPriceObj> getCategoryPricePlacementList() {
    return Collections.unmodifiableList(categoryPricePlacementList);
  }

  @NotNull
  public List<CategoryPriceObj> getCategoryPriceNonPlacementList() {
    return Collections.unmodifiableList(categoryPriceNonPlacementList);
  }

  @NotNull
  public List<SeatLocation> getSeatLocationList(@NotNull Collection<Long> seatIdCollection) {
    List<SeatLocation> result = new ArrayList<>();
    for (Long seatId : seatIdCollection) {
      EventSeatObj eventSeat = eventSeatMap.get(seatId);
      if (eventSeat == null) throw new IllegalArgumentException();
      SeatLocationObj seatLocation = eventSeat.getSeatLocation();
      result.add(seatLocation == null ? new SeatLocation(eventSeat.getId()) : new SeatLocation(seatLocation));
    }
    Collections.sort(result);
    return result;
  }

  @NotNull
  public Integer getSeatIndex(long seatId) {
    Integer seatIndex = seatIndexMap.get(seatId);
    if (seatIndex == null) throw new IllegalArgumentException();
    return seatIndex;
  }

  @NotNull
  public List<Integer> getRowIndexList(long seatId) {
    SeatLocationObj location = getLocation(seatId);
    List<Integer> result = rowIndexMap.get(location.getSector() + separator + location.getRow());
    if (result == null) throw new IllegalArgumentException();
    return result;
  }

  @NotNull
  public List<Integer> getSectorIndexList(long seatId) {
    SeatLocationObj location = getLocation(seatId);
    List<Integer> result = sectorIndexMap.get(location.getSector());
    if (result == null) throw new IllegalArgumentException();
    return result;
  }

  @NotNull
  public List<Integer> getAllIndexList() {
    return new ArrayList<>(seatIndexMap.values());
  }

  @NotNull
  private SeatLocationObj getLocation(long seatId) {
    EventSeatObj eventSeat = eventSeatMap.get(seatId);
    if (eventSeat == null) throw new IllegalArgumentException();
    SeatLocationObj location = eventSeat.getSeatLocation();
    if (location == null) throw new IllegalArgumentException();
    return location;
  }
}
