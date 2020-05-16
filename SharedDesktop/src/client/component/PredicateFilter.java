package client.component;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.12.16
 */
public interface PredicateFilter<E> {
  boolean filter(@NotNull E element);
}
