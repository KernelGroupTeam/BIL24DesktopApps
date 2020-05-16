package server.protocol2.editor;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.03.17
 */
public class ActionEventData implements Serializable {
  private static final long serialVersionUID = 4867134747144507392L;
  private long id;
  @NotNull
  private List<EventSeatObj> eventSeatList = Collections.emptyList();
  @NotNull
  private List<CategoryPriceObj> categoryPriceList = Collections.emptyList();
  @Nullable
  private byte[] svgData = null;
  @Nullable
  private Set<Long> ebsNotAvailIdSet;
  private boolean splExists;

  public ActionEventData(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public List<EventSeatObj> getEventSeatList() {
    return eventSeatList;
  }

  public void setEventSeatList(@NotNull List<EventSeatObj> eventSeatList) {
    this.eventSeatList = eventSeatList;
  }

  @NotNull
  public List<CategoryPriceObj> getCategoryPriceList() {
    return categoryPriceList;
  }

  public void setCategoryPriceList(@NotNull List<CategoryPriceObj> categoryPriceList) {
    this.categoryPriceList = categoryPriceList;
  }

  @Nullable
  public byte[] getSvgData() {
    return svgData;
  }

  public void setSvgData(@Nullable byte[] svgData) {
    this.svgData = svgData;
  }

  public boolean isEbsIdsPresent() {
    return ebsNotAvailIdSet != null;
  }

  @Nullable
  public Set<Long> getEbsNotAvailIdSet() {
    return ebsNotAvailIdSet;
  }

  public void setEbsNotAvailIdSet(@Nullable Set<Long> ebsNotAvailIdSet) {
    this.ebsNotAvailIdSet = ebsNotAvailIdSet;
  }

  public boolean isSplExists() {
    return splExists;
  }

  public void setSplExists(boolean splExists) {
    this.splExists = splExists;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActionEventData)) return false;
    ActionEventData that = (ActionEventData) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
