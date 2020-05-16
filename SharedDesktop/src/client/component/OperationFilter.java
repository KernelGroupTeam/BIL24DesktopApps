package client.component;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 25.04.16
 */
public interface OperationFilter<E> {
  boolean filter(@NotNull E element, @Nullable Object match);
}
