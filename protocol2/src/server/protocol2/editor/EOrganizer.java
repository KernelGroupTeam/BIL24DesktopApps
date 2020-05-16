package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.10.15
 */
public class EOrganizer implements Serializable {
  private static final long serialVersionUID = 2375215792725741834L;
  private long id;
  @NotNull
  private String name;

  public EOrganizer(long id, @NotNull String name) {
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
    if (!(o instanceof EOrganizer)) return false;
    EOrganizer that = (EOrganizer) o;
    return id == that.id;
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
