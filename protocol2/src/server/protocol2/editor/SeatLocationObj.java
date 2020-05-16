package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.10.15
 */
public class SeatLocationObj implements Serializable {
  private static final long serialVersionUID = -99084253860974039L;
  @NotNull
  private String sector;
  @NotNull
  private String row;
  @NotNull
  private String number;

  public SeatLocationObj(@NotNull String sector, @NotNull String row, @NotNull String number) {
    this.sector = sector;
    this.row = row;
    this.number = number;
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
  public String getNumber() {
    return number;
  }
}
