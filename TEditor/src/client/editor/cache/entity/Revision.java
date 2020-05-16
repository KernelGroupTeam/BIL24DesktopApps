package client.editor.cache.entity;

import com.sleepycat.persist.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.16
 */
@Entity
public class Revision {
  @PrimaryKey
  @NotNull
  private Integer id = 0;
  private int revision;

  @SuppressWarnings("unused")
  Revision() {
  }

  public Revision(int revision) {
    this.revision = revision;
  }

  public int getRevision() {
    return revision;
  }
}
