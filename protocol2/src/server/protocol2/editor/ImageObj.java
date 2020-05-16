package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import server.protocol2.NoLogging;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.06.15
 */
public class ImageObj implements Serializable {
  private static final long serialVersionUID = -6901722419796178280L;
  private static final byte[] empty = new byte[0];
  //на сервер приходит пустой (не менять картинку) либо полный (менять картинку, если hash изменился)
  //на клиент приходит пустой (подгрузить картинку позже) либо полный
  @NotNull
  @NoLogging
  private byte[] img = empty;
  @NotNull
  private String type;
  @NotNull
  private String hash = "";

  public ImageObj(@NotNull byte[] img, @NotNull String type) {// -> server
    this.img = img;
    this.type = type;
  }

  public ImageObj(@NotNull String type, @NotNull String hash) {// -> client hollow
    this.type = type;
    this.hash = hash;
  }

  public ImageObj(@NotNull byte[] img, @NotNull String type, @NotNull String hash) {// -> client solid
    this.img = img;
    this.type = type;
    this.hash = hash;
  }

  @NotNull
  public byte[] getImg() {
    return img;
  }

  @NotNull
  public String getType() {
    return type;
  }

  @NotNull
  public String getHash() {
    return hash;
  }

  public void setHash(@NotNull String hash) {
    this.hash = hash;
  }
}
