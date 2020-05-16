package server.protocol2.editor;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import server.protocol2.NoLogging;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.01.17
 */
public class QuotaInFileObj extends QuotaInObj {
  private static final long serialVersionUID = -7369066290347909706L;
  @NotNull
  private QuotaFormatObj format;
  @NotNull
  private String fileName;
  @NoLogging
  @NotNull
  private byte[] data;

  public QuotaInFileObj(long actionEventId, @NotNull String number, @NotNull Date date,
                        @NotNull QuotaFormatObj format, @NotNull String fileName, @NotNull byte[] data) {
    super(actionEventId, number, date);
    this.format = format;
    this.fileName = fileName;
    this.data = data;
  }

  @NotNull
  public QuotaFormatObj getFormat() {
    return format;
  }

  @NotNull
  public String getFileName() {
    return fileName;
  }

  @NotNull
  public byte[] getData() {
    return data;
  }
}
