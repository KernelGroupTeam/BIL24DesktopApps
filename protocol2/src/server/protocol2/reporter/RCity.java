package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 24.11.2016.
 */
public class RCity implements Serializable {
  private static final long serialVersionUID = -4529790655575711489L;
  private long id;
  @NotNull
  private String name;

  public RCity(long id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RCity)) return false;
    RCity rCity = (RCity) o;
    return id == rCity.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name;
  }
}
