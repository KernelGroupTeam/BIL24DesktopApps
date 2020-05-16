package client.reporter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;

import client.component.ConsoleFrame;
import client.net.NetPool;
import server.protocol2.common.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.04.16
 */
public class Env {
  public static final String ver = "1.8";
  public static final ImageIcon excelIcon = new ImageIcon(Env.class.getResource("/resources/excel.png"));
  public static final List<Image> frameIcons;

  static {
    List<Image> icons = new ArrayList<>(6);
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon16.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon32.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon48.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon64.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon128.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon256.png")).getImage());
    frameIcons = Collections.unmodifiableList(icons);
  }

  public static boolean testZone;
  public static Preferences pref;
  public static NetPool net;
  public static ConsoleFrame consoleFrame;
  public static LoginUser user;
  public static AcquiringObj agentAcquiring;//если не null, значит это базовый агент

  private Env() {
  }
}
