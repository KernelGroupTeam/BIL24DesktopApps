package server.protocol2.common;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 18.07.15
 */
public class CityObj implements Serializable {
  private static final long serialVersionUID = -5063606474196597949L;
  private long id;
  @NotNull
  private String name = "";
  private transient boolean childless;

  public CityObj(long id) {
    this.id = id;
  }

  public CityObj(long id, @NotNull String name) {
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

  public void setName(@NotNull String name) {
    this.name = name;
  }

  public boolean isChildless() {
    return childless;
  }

  public void setChildless(boolean childless) {
    this.childless = childless;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CityObj)) return false;
    CityObj cityObj = (CityObj) o;
    return id == cityObj.id;
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
