package client.reporter;

import java.awt.*;
import java.io.BufferedInputStream;
import java.net.InetSocketAddress;
import java.util.prefs.Preferences;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;

import client.component.ConsoleFrame;
import client.component.auth.AuthZone;
import client.component.menu.TextComponentMouseEventQueue;
import client.net.*;
import client.utils.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.common.LoginUser;

import static client.reporter.Env.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.06.15
 */
public class TReporter implements AuthFrameListener {

  public TReporter() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        shutdown();
      }
    });
    final InetSocketAddress realZoneAddress;
    final InetSocketAddress testZoneAddress;
    try {
      pref = Preferences.userRoot().node(testZone ? "bil24test" : "bil24").node("reporter");
      ConfigurationRO config = new ConfigurationRO(new BufferedInputStream(getClass().getResource("/resources/reporter.properties").openStream()));
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
        } catch (Exception ignored) {
        }
        try {
          L10n.localize();
          consoleFrame = new ConsoleFrame();
          ConsolePrintStream.Appender appender = new ConsolePrintStream.TextPaneAppender(consoleFrame.getConsoleTextPane(), consoleFrame.getRegularStyle(), consoleFrame.getErrorStyle());
          System.setOut(new ConsolePrintStream(System.out, appender, false));
          System.setErr(new ConsolePrintStream(System.err, appender, true));
          System.out.println(Utils.getSystemInfo());
          new AuthFrame(realZoneAddress, testZoneAddress, TReporter.this).setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
      }
    });
  }

  @Override
  public void authComplete(@NotNull AuthZone zone, @NotNull LoginUser user) {
    try {
      testZone = (zone == AuthZone.TEST);
      pref = Preferences.userRoot().node(testZone ? "bil24test" : "bil24").node("reporter");
      Env.user = user;
      new MainFrame().startWork();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(3);
    }
  }

  private void shutdown() {
    if (net != null) net.removeMacSecurity();
  }

  @SuppressWarnings("IfStatementMissingBreakInLoop")
  public static void main(String[] args) {
    for (String arg : args) {
      if (arg.equalsIgnoreCase("-test")) testZone = true;
    }
    new TReporter();
  }
}
