package server.protocol2.cassa;

import java.io.Serializable;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 06.03.2018.
 * Переменные blank и name обеспечивают уникальность объекта. Мы знаем что в одной папке не может быть 2-х файлов с одним и тем же названием
 * checksum нужен для сверки файлов клиента и сервера
 */
public class CTicketTemplate implements Serializable {
  private static final long serialVersionUID = -9116173736893072720L;
  @NotNull
  private String id;//Идентификатор. Составной из названии бланка и названии файла
  @Nullable
  private String blank;//Название бланка. Для печати в файл это значение null. Для термальных принтеров должно быть отличным от null
  @NotNull
  private String name;//Название файла
  @NotNull
  private byte[] checksum;//Контрольная сумма файла

  public CTicketTemplate(@Nullable String blank, @NotNull String name, @NotNull byte[] checksum) {
    this.id = (blank == null ? "" : blank) + name;
    this.blank = blank;
    this.name = name;
    this.checksum = checksum;
  }

  @NotNull
  public final String getId() {
    return id;
  }

  @Nullable
  public final String getBlank() {
    return blank;
  }

  @NotNull
  public final String getName() {
    return name;
  }

  @NotNull
  public final byte[] getChecksum() {
    return checksum;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CTicketTemplate)) return false;
    CTicketTemplate that = (CTicketTemplate) o;
    return id.equals(that.id);
  }

  @Override
  public final int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "CTicketTemplate{id=" + id + ", blank=" + blank + ", name=" + name + '}';
  }
}
