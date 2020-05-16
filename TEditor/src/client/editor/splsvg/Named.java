package client.editor.splsvg;

import java.util.*;


/**
 * General information about sector, row, seats
 */
public abstract class Named {
  private final String name;

  public Named(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Named named = (Named) o;

    return name.equals(named.name);
  }

  public int hashCode() {
    return name.hashCode();
  }

  public static final class Sector extends Named {
    private final Map<String, Row> rows;

    public Sector(String name, Map<String, Row> rows) {
      super(name);
      this.rows = rows;
    }

    public Map<String, Row> getRows() {
      return rows;
    }
  }

  public static final class Row extends Named {
    private final Set<String> seats;

    public Row(String name, Set<String> seats) {
      super(name);
      this.seats = seats;
    }

    public Set<String> getSeats() {
      return seats;
    }
  }
}
