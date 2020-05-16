package client.component.filter;

import java.io.File;
import javax.swing.filechooser.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.05.17
 */
public class IdFileFilter<ID> extends FileFilter {
  @NotNull
  private final ID id;
  @NotNull
  private final FileNameExtensionFilter delegate;

  public IdFileFilter(@NotNull ID id, String description, @NotNull String... extensions) {
    this.id = id;
    this.delegate = new FileNameExtensionFilter(description, extensions);
  }

  @NotNull
  public ID getId() {
    return id;
  }

  @Override
  public boolean accept(File f) {
    return delegate.accept(f);
  }

  @Override
  public String getDescription() {
    return delegate.getDescription();
  }

  public String[] getExtensions() {
    return delegate.getExtensions();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
