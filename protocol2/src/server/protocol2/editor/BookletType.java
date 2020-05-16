package server.protocol2.editor;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.11.16
 */
public class BookletType implements Serializable {
  private static final long serialVersionUID = -2503969928059476995L;
  private static final ConcurrentHashMap<Integer, BookletType> cache = new ConcurrentHashMap<>();
  private int id;
  @NotNull
  private String desc;
  @NotNull
  private String fileFilterDesc;
  @NotNull
  private String extension;

  private BookletType(int id, @NotNull String desc, @NotNull String fileFilterDesc, @NotNull String extension) {
    this.id = id;
    this.desc = desc;
    this.fileFilterDesc = fileFilterDesc;
    this.extension = extension;
  }

  @NotNull
  public static BookletType getInstance(int id, @NotNull String desc, @NotNull String fileFilterDesc, @NotNull String extension) {
    BookletType result = cache.get(id);
    if (result != null) return result;
    BookletType bookletType = new BookletType(id, desc, fileFilterDesc, extension);
    result = cache.putIfAbsent(bookletType.getId(), bookletType);
    if (result == null) result = bookletType;
    return result;
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
  public String getExtension() {
    return extension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BookletType)) return false;
    BookletType that = (BookletType) o;
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
