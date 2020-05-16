/*
 * Created by JFormDesigner on Wed Apr 13 10:06:28 MSK 2016
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.net.InetSocketAddress;
import javax.swing.*;

import client.component.auth.*;
import client.editor.svn.SvnRevision;
import client.net.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.*;
import server.protocol2.common.LoginUser;

import static client.editor.Env.net;

/**
 * @author Maksim
 */
public class AuthFrame extends JFrame implements AuthResultListener, NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private AuthPanel authPanel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final AuthType[] AUTH_TYPES = {
      new AuthType(UserType.ORGANIZER.getId(), UserType.ORGANIZER.getDesc()),
      new AuthType(UserType.OPERATOR.getId(), UserType.OPERATOR.getDesc())};
  private final AuthKeyEventDispatcher authKeyEventDispatcher = new AuthKeyEventDispatcher();
  @NotNull
  private final InetSocketAddress realZoneAddress;
  @NotNull
  private final InetSocketAddress testZoneAddress;
  @NotNull
  private final AuthFrameListener listener;
  private boolean svgCorrectionMode = false;

  public AuthFrame(@NotNull InetSocketAddress realZoneAddress, @NotNull InetSocketAddress testZoneAddress, @NotNull AuthFrameListener listener) {
    this.realZoneAddress = realZoneAddress;
    this.testZoneAddress = testZoneAddress;
    this.listener = listener;
    initComponents();
    authPanel.setAuthType(Env.pref.get("login.type", null));
    authPanel.setLogin(Env.pref.get("login", null));
    if (Env.testZone) authPanel.setZone(AuthZone.TEST);
    pack();
    setLocationRelativeTo(null);
    authPanel.setLogin(Env.pref.get("login", null));//фокус в панели
  }

  private void thisWindowOpened() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(authKeyEventDispatcher);
  }

  private void thisWindowClosed() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(authKeyEventDispatcher);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    authPanel = new AuthPanel(this, AUTH_TYPES);

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("\u0410\u0432\u0442\u043e\u0440\u0438\u0437\u0430\u0446\u0438\u044f");
    setResizable(false);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        thisWindowClosed();
      }
      @Override
      public void windowOpened(WindowEvent e) {
        thisWindowOpened();
      }
    });
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(authPanel, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void authCancelled() {
    System.exit(0);
  }

  @Override
  public void authReset(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login) {
    Request.setDefHeader(App.EDITOR, Env.ver, SvnRevision.SVN_REV, login, UserType.getUserType(authType.getId()));
    net.setServerAddress(zone == AuthZone.REAL ? realZoneAddress : testZoneAddress);
    net.removeMacSecurity();
    net.create("RESET_PASS", new Request(null), this).start();
  }

  @Override
  public void authLogin(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login, @NotNull byte[] key) {
    if (authPanel.isKeepLogin()) Env.pref.put("login", login);
    else Env.pref.remove("login");
    Env.pref.put("login.type", String.valueOf(authType.getId()));
    Request.setDefHeader(App.EDITOR, Env.ver, SvnRevision.SVN_REV, login, UserType.getUserType(authType.getId()));
    net.setServerAddress(zone == AuthZone.REAL ? realZoneAddress : testZoneAddress);
    net.setMacSecurity(key);
    net.create("AUTH_EDITOR", new Request(null), this).start();
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    switch (state) {
      case STARTED:
        authPanel.setProgress(5);
        break;
      case ESTABLISHING_CONNECTION:
        authPanel.setProgress(10);
        break;
      case CONNECTED:
        authPanel.setProgress(20);
        break;
      case HEADER_SENT:
        authPanel.setProgress(30);
        break;
      case DATA_SENT:
        authPanel.setProgress(50);
        break;
      case HEADER_RECEIVED:
        authPanel.setProgress(70);
        break;
      case DATA_RECEIVED:
        authPanel.setProgress(90);
        break;
      case FINISHED:
        authPanel.setProgress(100);
        break;
    }
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      authPanel.reset();
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (result.getCommand().equals("RESET_PASS")) {
      JOptionPane.showMessageDialog(this, result.getResponse().getData(), "Восстановление пароля", JOptionPane.INFORMATION_MESSAGE);
      authPanel.reset();
    }
    if (result.getCommand().equals("AUTH_EDITOR")) {
      AuthZone zone = (result.getSource().getServerAddress() == realZoneAddress ? AuthZone.REAL : AuthZone.TEST);
      Object[] data = (Object[]) result.getResponse().getData();
      LoginUser user = (LoginUser) data[0];
      listener.authComplete(zone, user, svgCorrectionMode);
      this.dispose();
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    authPanel.reset();
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private class AuthKeyEventDispatcher implements KeyEventDispatcher {
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
      if (e.getID() == KeyEvent.KEY_PRESSED) {
        if (e.getKeyCode() == KeyEvent.VK_Z && e.isAltDown()) authPanel.changeZone();
        if (e.getKeyCode() == KeyEvent.VK_L && e.isAltDown() && e.isControlDown()) {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(AuthFrame.this);
            AuthFrame.this.pack();
          } catch (Exception ignored) {
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_C && e.isAltDown()) svgCorrectionMode = true;
      }
      return false;
    }
  }
}
