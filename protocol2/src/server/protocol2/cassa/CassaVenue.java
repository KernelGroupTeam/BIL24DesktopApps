package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 13.10.2018
 */
public class CassaVenue implements Serializable {
  private static final long serialVersionUID = 8874101461934102338L;
  private long id;//Идентификатор площадки(место проведения)
  @NotNull
  private String name;//Название площадки(место проведения)
  @NotNull
  private String address;//Адрес площадки(место проведения)
  @NotNull
  private CassaCity city;//Город, в котором находится площадка(место проведения)

  public CassaVenue(long id, @NotNull String name, @NotNull String address, @NotNull CassaCity city) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.city = city;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getAddress() {
    return address;
  }

  @NotNull
  public CassaCity getCity() {
    return city;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CassaVenue that = (CassaVenue) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaVenue{id=" + id + ", name=" + name + ", address=" + address + ", city=" + city + '}';
  }
}
