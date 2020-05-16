package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 13.10.2018
 */
public class CassaCity implements Serializable {
  private static final long serialVersionUID = 8098262471487337444L;
  private long id;//Идентификатор города
  @NotNull
  private String name;//Название города

  public CassaCity(long id, @NotNull String name) {
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
    if (o == null || getClass() != o.getClass()) return false;
    CassaCity that = (CassaCity) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaCity{id=" + id + ", name=" + name + '}';
  }
}
