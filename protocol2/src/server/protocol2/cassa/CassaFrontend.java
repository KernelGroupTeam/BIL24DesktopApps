package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 18.07.2018
 */
public class CassaFrontend implements Serializable {
  private static final long serialVersionUID = -1166615241188936164L;
  private long id;//Идентификатор фронтенда(интерфейса)
  @NotNull
  private String name;//Название фронтенда(интерфейса)
  @NotNull
  private String token;//Токен фронтенда(интерфейса)

  public CassaFrontend(long id, @NotNull String name, @NotNull String token) {
    this.id = id;
    this.name = name;
    this.token = token;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getToken() {
    return token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CassaFrontend that = (CassaFrontend) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaFrontend{id=" + id + ", name=" + name + ", token=" + token + '}';
  }
}
