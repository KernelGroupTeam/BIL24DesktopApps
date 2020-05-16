package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 18.10.2018
 */
public class CassaSeatLocation implements Serializable {
  private static final long serialVersionUID = 485499701435476005L;
  @NotNull
  private String sector;//Сектор
  @NotNull
  private String row;//Ряд
  @NotNull
  private String number;//Место

  public CassaSeatLocation(@NotNull String sector, @NotNull String row, @NotNull String number) {
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

  @Override
  public String toString() {
    return "CassaSeatLocation{sector=" + sector + ", row=" + row + ", number=" + number + '}';
  }
}
