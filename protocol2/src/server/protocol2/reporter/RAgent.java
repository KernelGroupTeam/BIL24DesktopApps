package server.protocol2.reporter;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.10.15
 */
public class RAgent implements Serializable {
  private static final long serialVersionUID = 2456284845404665907L;
  private long id;
  @NotNull
  private String name;

  public RAgent(long id, @NotNull String name) {
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
    if (!(o instanceof RAgent)) return false;
    RAgent rAgent = (RAgent) o;
    return id == rAgent.id;
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
