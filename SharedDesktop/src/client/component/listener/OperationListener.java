package client.component.listener;

import java.util.EventListener;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 18.07.15
 */
public interface OperationListener<E> extends EventListener {

  void clear();

  boolean check();

  void load(@NotNull E object);

  boolean save(@NotNull E object);
}
