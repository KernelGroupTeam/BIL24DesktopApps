package client.editor.cache.entity;

import com.sleepycat.persist.model.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.01.16
 */
@Entity
public class ActionSmallImage extends Image {
  @SuppressWarnings("unused")
  private ActionSmallImage() {
  }

  public ActionSmallImage(long id, @NotNull String hash, @NotNull byte[] data) {
    super(id, hash, data);
  }
}
