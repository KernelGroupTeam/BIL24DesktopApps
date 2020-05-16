package server.protocol2.editor;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.08.18
 */
public class GenreObj implements Serializable {
  private static final long serialVersionUID = -8901863731171797610L;
  private static final ConcurrentHashMap<Integer, GenreObj> cache = new ConcurrentHashMap<>();
  private final int id;
  @NotNull
  private final String name;

  private GenreObj(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  @NotNull
  public static GenreObj getInstance(int id, @NotNull String name) {
    GenreObj result = cache.get(id);
    if (result != null) return result;
    GenreObj genre = new GenreObj(id, name);
    result = cache.putIfAbsent(genre.getId(), genre);
    if (result == null) result = genre;
    return result;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GenreObj)) return false;
    GenreObj that = (GenreObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public String toString() {
    return name;
  }
}
