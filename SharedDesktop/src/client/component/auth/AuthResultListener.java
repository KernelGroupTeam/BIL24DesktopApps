package client.component.auth;

import java.util.EventListener;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.16
 */
public interface AuthResultListener extends EventListener {
  void authCancelled();

  void authReset(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login);

  void authLogin(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login, @NotNull byte[] key);
}
