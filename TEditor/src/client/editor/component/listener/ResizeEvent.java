package client.editor.component.listener;

import java.util.EventObject;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class ResizeEvent extends EventObject {
  @NotNull
  private final Dimension dimension;

  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   * @throws IllegalArgumentException if source is null.
   */
  public ResizeEvent(@NotNull Object source, @NotNull Dimension dimension) {
    super(source);
    this.dimension = dimension;
  }

  @NotNull
  public Dimension getDimension() {
    return dimension;
  }

  public enum Dimension {WIDTH, HEIGHT, BOTH}
}
