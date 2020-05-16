package server.protocol2.editor;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import server.protocol2.NoLogging;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.11.16
 */
public class QuotaFormatObj implements Serializable {
  private static final long serialVersionUID = 1478619328261313039L;
  private int id;
  @NotNull
  private String desc;
  @NoLogging
  @NotNull
  private String fileFilterDesc;
  @NoLogging
  @NotNull
  private String[] fileFilterExtensions;

  public QuotaFormatObj(int id, @NotNull String desc, @NotNull String fileFilterDesc, @NotNull String[] fileFilterExtensions) {
    this.id = id;
    this.desc = desc;
    this.fileFilterDesc = fileFilterDesc;
    this.fileFilterExtensions = fileFilterExtensions;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getDesc() {
    return desc;
  }

  @NotNull
  public String getFileFilterDesc() {
    return fileFilterDesc;
  }

  @NotNull
  public String[] getFileFilterExtensions() {
    return fileFilterExtensions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof QuotaFormatObj)) return false;
    QuotaFormatObj that = (QuotaFormatObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return desc;
  }
}
