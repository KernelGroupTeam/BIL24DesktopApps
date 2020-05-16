package client.editor;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 16.06.15
 */
@SuppressWarnings("NonFinalFieldInEnum")
public enum PosterData {
  VENUE_BIG("GET_VENUE_BIG_IMAGE"),
  ACTION_BIG("GET_ACTION_BIG_IMAGE"),
  ACTION_SMALL("GET_ACTION_SMALL_IMAGE");

  private static final byte[] EMPTY_BYTES = new byte[0];
  private final String command;
  @NotNull
  private byte[] img = new byte[0];//пустой массив означает, что картинку не меняем
  @Nullable
  private String type = null;//null означает, что изображения нет

  PosterData(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

  @NotNull
  byte[] getImg() {
    return img;
  }

  void setImg(@Nullable byte[] img) {
    if (img == null) this.img = EMPTY_BYTES;
    else this.img = img;
  }

  @Nullable
  String getType() {
    return type;
  }

  void setType(@Nullable String type) {
    this.type = type;
  }
}
