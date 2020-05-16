package client.editor;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.prefs.Preferences;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;

import client.component.ConsoleFrame;
import client.component.auth.AuthZone;
import client.component.menu.TextComponentMouseEventQueue;
import client.editor.cache.LocalCache;
import client.net.*;
import client.utils.*;
import org.jetbrains.annotations.*;
import server.protocol2.common.LoginUser;

import static client.editor.Env.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.06.15
 */
public class TEditor implements AuthFrameListener {
  private static final boolean PRO = false;
  private MainFrame mainFrame;

  public TEditor(@Nullable final String xmxParam) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        shutdown();
      }
    });
    final InetSocketAddress realZoneAddress;
    final InetSocketAddress testZoneAddress;
    try {
      pref = Preferences.userRoot().node(testZone ? "bil24test" : "bil24").node("editor");
      ConfigurationRO config = new ConfigurationRO(new BufferedInputStream(getClass().getResource("/resources/editor.properties").openStream()));
      String host = config.getProperty("host");
      realZoneAddress = new InetSocketAddress(host, Integer.parseInt(config.getProperty("port")));
      testZoneAddress = new InetSocketAddress(host, Integer.parseInt(config.getProperty("test")));
      net = new NetPool(realZoneAddress);
      net.setTimeout(20000);
      net.setSocketFactory(SSLSocketFactory.getDefault());
      net.setEventMode(Network.EventMode.EDT_INVOKE_LATER);
      net.addPoolListener(new NetPoolListener());
      net.setPoolRequestProcessor(new NetRequestProcessor());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return;
    }

    Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TextComponentMouseEventQueue());
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        } catch (Exception ignored) {
        }
        try {
          L10n.localize();
          consoleFrame = new ConsoleFrame();
          ConsolePrintStream.Appender appender = new ConsolePrintStream.TextPaneAppender(consoleFrame.getConsoleTextPane(), consoleFrame.getRegularStyle(), consoleFrame.getErrorStyle());
          System.setOut(new ConsolePrintStream(System.out, appender, false));
          System.setErr(new ConsolePrintStream(System.err, appender, true));
          System.out.println(Utils.getSystemInfo());
          if (xmxParam != null) System.out.println(xmxParam);
          new AuthFrame(realZoneAddress, testZoneAddress, TEditor.this).setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
      }
    });
  }

  @Override
  public void authComplete(@NotNull AuthZone zone, @NotNull LoginUser user, boolean svgCorrectionMode) {
    try {
      testZone = (zone == AuthZone.TEST);
      pref = Preferences.userRoot().node(testZone ? "bil24test" : "bil24").node("editor");
      Env.user = user;

      if (svgCorrectionMode) {
        new SVGCorrectionFrame().startWork();
        return;
      }
      File userHome = new File(getUserHome("BIL24"), "TEditor");
      if (!userHome.exists() && !userHome.mkdir()) throw new Exception("user home folder");
      String storeName = (testZone ? "ImageCacheTestZone" : "ImageCache");
      cache = new LocalCache(new File(userHome, "cache"), storeName, true);
      mainFrame = new MainFrame();
      mainFrame.startWork();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(3);
    }
  }

  private void shutdown() {
    if (cache != null) cache.shutdown();
    if (net != null) net.removeMacSecurity();
    if (mainFrame != null) mainFrame.saveState();
  }

  private static File getUserHome(String folder) {
    File home = new File(System.getProperty("user.home"));
    if (folder == null || folder.isEmpty()) return home;
    File temp;

    temp = new File(home, "AppData");// Vista, Seven
    if (temp.isDirectory()) {
      temp = new File(temp, "Local");
      if (!temp.isDirectory()) return home;
      temp = new File(temp, folder);
      if (temp.isDirectory()) return temp;
      if (temp.mkdir()) return temp;
      return home;
    }

    temp = new File(home, "Application Data");// более старая Windows
    if (temp.isDirectory()) {
      temp = new File(temp, folder);
      if (temp.isDirectory()) return temp;
      if (temp.mkdir()) return temp;
      return home;
    }

    temp = new File(home, folder);// всё остальное
    if (temp.isDirectory()) return temp;
    if (temp.mkdir()) return temp;
    return home;
  }

  public static void main(String[] args) {
    String xmxParam = null;
    for (String arg : args) {
      if (arg.equalsIgnoreCase("-test")) testZone = true;
      if (arg.startsWith("-Xmx")) xmxParam = arg;
    }
    if (xmxParam != null || !restartApp(args)) new TEditor(xmxParam);
  }

  private static boolean restartApp(String[] args) {
    try {
      String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "javaw";
      File currentJar = new File(TEditor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      if (!currentJar.getName().endsWith(".jar")) return false;

      String xmx = "-Xmx1024M";
      if (System.getProperty("sun.arch.data.model").equals("64")) xmx = (PRO ? "-Xmx6144M" : "-Xmx2048M");
      ArrayList<String> command = new ArrayList<>();
      command.add(javaBin);
      command.add("-jar");
      command.add(xmx);
      command.add(currentJar.getPath());
      command.addAll(Arrays.asList(args));
      command.add(xmx);

      ProcessBuilder builder = new ProcessBuilder(command);
      builder.start();
      return true;
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
