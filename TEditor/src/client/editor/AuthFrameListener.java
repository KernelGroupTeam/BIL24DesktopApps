package client.editor;

import java.util.EventListener;

import client.component.auth.AuthZone;
import org.jetbrains.annotations.NotNull;
import server.protocol2.common.LoginUser;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.04.16
 */
public interface AuthFrameListener extends EventListener {
  void authComplete(@NotNull AuthZone zone, @NotNull LoginUser user, boolean svgCorrectionMode);
}
