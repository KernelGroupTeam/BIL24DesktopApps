package server.protocol2;

import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 06.09.15
 */
public interface Filterable {
  boolean pass(@Nullable Object filter);
}
