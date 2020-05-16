package eventim.spl.models;

import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 12.12.2016.
 */
public class EventimString {
  private static final Charset charset = Charset.forName("cp1251");
  @NotNull
  private final byte[] bytes;
  @NotNull
  private final String text;

  public EventimString(@NotNull byte[] bytes) {
    this.bytes = bytes;
    this.text = createText(bytes);
  }

  @NotNull
  private static String createText(@NotNull byte[] bytes) {
    int i;
    for (i = 0; i < bytes.length; i++) {
      if (bytes[i] == 0) break;
    }
    return new String(bytes, 0, i, charset);
  }

  @NotNull
  public byte[] getBytes() {
    return bytes;
  }

  @NotNull
  public String getText() {
    return text;
  }
}
