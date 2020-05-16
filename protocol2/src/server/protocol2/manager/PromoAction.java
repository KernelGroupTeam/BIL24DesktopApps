package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.03.19
 */
public class PromoAction implements PromoScope, Serializable {
  private static final long serialVersionUID = -7772122095517983735L;
  private long id;
  @NotNull
  private String name;

  public PromoAction(long id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PromoAction)) return false;
    PromoAction that = (PromoAction) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
