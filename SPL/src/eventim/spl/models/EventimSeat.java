package eventim.spl.models;

import eventim.spl.blocks.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 14.11.2016.
 */
public class EventimSeat {
  @NotNull
  private final String sector;
  @NotNull
  private final String row;
  @NotNull
  private final String seat;
  @NotNull
  private final Sitz2 sitz2;

  public EventimSeat(@NotNull Bereich bereich, @NotNull Reihe reihe, @NotNull Sitz2 sitz2) {
    this.sector = bereich.getSectorName() == null ? "" : bereich.getSectorName();
    this.row = reihe.getRowName() == null ? "" : reihe.getRowName();
    this.seat = isNumber(reihe.getRowFirstNumber()) ?
        String.valueOf(Integer.parseInt(reihe.getRowFirstNumber()) + sitz2.getSeatNumber()) :
        sitz2.getSeatName();
    this.sitz2 = sitz2;
  }

  public long getSeatId() {
    return sitz2.getSeatId();
  }

  @NotNull
  public String getSector() {
    return sector;
  }

  @NotNull
  public String getRow() {
    return row;
  }

  @NotNull
  public String getSeat() {
    return seat;
  }

  public void replaceSeatId(long newSeatId) {
    sitz2.setSeatId(newSeatId);
  }

  private static boolean isNumber(@NotNull String text) {
    for (int i = 0; i < text.length(); i++) {
      if (!Character.isDigit(text.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return sector + "|" + row + "|" + seat;
  }
}
