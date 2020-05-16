package client.editor.component.filter;

import client.component.filter.IdFileFilter;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.05.17
 */
public class BookletFileFilter extends IdFileFilter<Integer> {
  @NotNull
  private final String desc;
  @NotNull
  private final String extension;

  public BookletFileFilter(int id, @NotNull String desc, String description, @NotNull String extension) {
    super(id, description, extension);
    this.desc = desc;
    this.extension = extension;
  }

  @NotNull
  public String getDesc() {
    return desc;
  }

  @NotNull
  public String getExtension() {
    return extension;
  }
}
