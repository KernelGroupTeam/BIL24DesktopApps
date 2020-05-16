package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.11.17
 */
public class ROrganizer implements Serializable {
  private static final long serialVersionUID = 4554789644061241101L;
  private long id;
  @NotNull
  private String name;

  public ROrganizer(long id, @NotNull String name) {
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
    if (!(o instanceof ROrganizer)) return false;
    ROrganizer rOrganizer = (ROrganizer) o;
    return id == rOrganizer.id;
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
