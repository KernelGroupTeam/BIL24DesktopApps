package report.reporter.enums;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 19.01.2018
 */
public enum EPeriodType {
  SALES(1, "продаж"),
  SHOWS(2, "сеансов"),
  ;

  private final int id;
  @NotNull
  private final String name;

  EPeriodType(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Nullable
  public static EPeriodType getPeriodTypeById(int id) {
    for (EPeriodType periodType : EPeriodType.values()) {
      if (periodType.getId() == id) return periodType;
    }
    return null;
  }
}
