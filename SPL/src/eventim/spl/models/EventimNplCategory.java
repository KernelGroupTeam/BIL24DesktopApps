package eventim.spl.models;

import eventim.spl.blocks.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 18.10.2017.
 */
public class EventimNplCategory {
  @NotNull
  private final String sector;
  private final int seatCount;
  @NotNull
  private final Stehplatz stehplatz;

  public EventimNplCategory(@NotNull Bereich bereich, @NotNull Stehplatz stehplatz) {
    this.sector = bereich.getSectorName() == null ? "" : bereich.getSectorName();
    this.seatCount = (int) stehplatz.getSeatCount();
    this.stehplatz = stehplatz;
  }

  public long getCategoryId() {
    return stehplatz.getId();
  }

  @NotNull
  public String getSector() {
    return sector;
  }

  public int getSeatCount() {
    return seatCount;
  }

  public void replaceCategoryId(long newId) {
    stehplatz.setId(newId);
  }

  @Override
  public String toString() {
    return sector + "|" + seatCount;
  }
}
