package server.protocol2.cassa;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 29.06.2018.
 */
public class CTicketTemplateData extends CTicketTemplate {
  private static final long serialVersionUID = 6508220429154865936L;
  @Nullable
  private byte[] bytes;//Байт массив файла. Если null, значит на сервере нет шаблона

  public CTicketTemplateData(@Nullable String blank, @NotNull String name, @NotNull byte[] checksum, @Nullable byte[] bytes) {
    super(blank, name, checksum);
    this.bytes = bytes;
  }

  @Nullable
  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return "CTicketTemplateData{id=" + getId() + ", blank=" + getBlank() + ", name=" + getName() + ", bytes=" + (bytes == null ? null : bytes.length) + '}';
  }
}
