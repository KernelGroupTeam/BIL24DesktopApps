package client.editor.cache.entity;

import com.sleepycat.persist.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.01.16
 */
@Persistent
public abstract class Image {
  static final byte[] EMPTY_BYTES = new byte[0];
  @PrimaryKey
  @NotNull
  private Long id = 0L;
  @NotNull
  private String hash = "";
  @NotNull
  private byte[] data = EMPTY_BYTES;

  Image() {
  }

  public Image(long id, @NotNull String hash, @NotNull byte[] data) {
    this.id = id;
    this.hash = hash;
    this.data = data;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getHash() {
    return hash;
  }

  @NotNull
  public byte[] getData() {
    return data;
  }
}
