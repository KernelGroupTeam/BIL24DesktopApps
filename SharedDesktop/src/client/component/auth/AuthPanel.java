/*
 * Created by JFormDesigner on Tue Apr 12 15:52:11 MSK 2016
 */

package client.component.auth;

import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXHyperlink;
import org.jetbrains.annotations.*;

/**
 * @author Maksim
 */
public class AuthPanel extends JPanel {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLabel zoneLabel;
  private JButton okButton;
  private JProgressBar progressBar;
  private JComboBox<AuthType> authTypeComboBox;
  private JTextField loginField;
  private JPasswordField passField;
  private JCheckBox keepLoginCheckBox;
  private JXHyperlink resetPassLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final String resetText1 = "Чтобы восстановить пароль для роли \"";
  private static final String resetText2 = "\"\nвведите адрес своей электронной почты:\n\n";
  private static final String testZoneText = "ТЗ";
  private static final String testZoneToolTipText = "Тестовая зона (смена: Alt+Z)";
  @NotNull
  private AuthResultListener listener;
  @NotNull
  private AuthZone zone = AuthZone.REAL;

  @Deprecated //for designer
  public AuthPanel() {
    this(new AuthResultListener() {
      @Override
      public void authCancelled() {
      }

      @Override
      public void authReset(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login) {
      }

      @Override
      public void authLogin(@NotNull AuthZone zone, @NotNull AuthType authType, @NotNull String login, @NotNull byte[] key) {
      }
    }, new AuthType(-1, ""));
  }

  public AuthPanel(@NotNull AuthResultListener listener, AuthType... authTypes) {
    this.listener = listener;
    if (authTypes == null || authTypes.length == 0) throw new IllegalArgumentException("authTypes");
    initComponents();
    for (AuthType authType : authTypes) {
      authTypeComboBox.addItem(authType);
    }
  }

  public void reset() {
    passField.setText("");
    if (!loginField.getText().trim().isEmpty()) passField.requestFocus();
    resetPassLabel.setEnabled(true);
    okButton.setEnabled(true);
    progressBar.setValue(0);
  }

  public void setAuthType(int authTypeId) {
    authTypeComboBox.setSelectedItem(new AuthType(authTypeId, ""));
  }

  public void setAuthType(@Nullable String authTypeId) {
    if (authTypeId == null) return;
    try {
      int id = Integer.parseInt(authTypeId);
      setAuthType(id);
    } catch (NumberFormatException ignored) {
    }
  }

  public void setLogin(@Nullable String login) {
    if (login == null || login.isEmpty()) {
      loginField.requestFocus();
    } else {
      loginField.setText(login);
      passField.requestFocus();
      keepLoginCheckBox.setSelected(true);
    }
  }

  public void setZone(@NotNull AuthZone zone) {
    this.zone = zone;
    if (zone == AuthZone.REAL) {
      zoneLabel.setText("");
      zoneLabel.setToolTipText("");
    } else {
      zoneLabel.setText(testZoneText);
      zoneLabel.setToolTipText(testZoneToolTipText);
    }
  }

  public void setProgress(int value) {
    progressBar.setValue(value);
  }

  public void changeZone() {
    setZone(zone.opposite());
  }

  public boolean isKeepLogin() {
    return keepLoginCheckBox.isSelected();
  }

  @NotNull
  private AuthType getAuthType() {
    return authTypeComboBox.getItemAt(authTypeComboBox.getSelectedIndex());
  }

  private void loginFieldActionPerformed() {
    if (!loginField.getText().trim().isEmpty()) {
      passField.requestFocus();
    }
  }

  private void passFieldActionPerformed() {
    if (passField.getPassword().length > 0) {
      okButtonActionPerformed();
    }
  }

  private void resetPassLabelActionPerformed() {
    String resetLogin = (String) JOptionPane.showInputDialog(this, resetText1 + getAuthType() + resetText2,
        "Восстановление пароля", JOptionPane.PLAIN_MESSAGE, null, null, loginField.getText().trim());
    if (resetLogin != null) resetLogin = resetLogin.trim();
    if (resetLogin != null && !resetLogin.isEmpty()) {
      resetPassLabel.setEnabled(false);
      okButton.setEnabled(false);
      listener.authReset(zone, getAuthType(), resetLogin);
    }
  }

  private void zoneLabelMouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && !e.isConsumed()) {
      e.consume();
      changeZone();
    }
  }

  private void okButtonActionPerformed() {
    if (loginField.getText().trim().isEmpty()) {
      loginField.requestFocus();
      JOptionPane.showMessageDialog(this, "Не указано имя для доступа к серверу", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (passField.getPassword().length == 0) {
      passField.requestFocus();
      JOptionPane.showMessageDialog(this, "Не указан пароль для доступа к серверу", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    String login = loginField.getText().trim();
    char[] pass = passField.getPassword();

    CharBuffer charBuffer = CharBuffer.wrap(pass);
    ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
    byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    byte[] key = digest.digest(bytes);

    // clear sensitive data
    Arrays.fill(pass, '\u0000');
    Arrays.fill(charBuffer.array(), '\u0000');
    Arrays.fill(byteBuffer.array(), (byte) 0);
    Arrays.fill(bytes, (byte) 0);
    resetPassLabel.setEnabled(false);
    okButton.setEnabled(false);
    listener.authLogin(zone, getAuthType(), login, key);
  }

  private void cancelButtonActionPerformed() {
    listener.authCancelled();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JPanel buttonBar = new JPanel();
    zoneLabel = new JLabel();
    okButton = new JButton();
    JButton cancelButton = new JButton();
    progressBar = new JProgressBar();
    JPanel contentPanel = new JPanel();
    JLabel authTypeLabel = new JLabel();
    authTypeComboBox = new JComboBox<>();
    JLabel loginLabel = new JLabel();
    loginField = new JTextField();
    JLabel passLabel = new JLabel();
    passField = new JPasswordField();
    keepLoginCheckBox = new JCheckBox();
    resetPassLabel = new JXHyperlink();
    resetPassLabel.setClickedColor(resetPassLabel.getUnclickedColor());

    //======== this ========
    setBorder(new EmptyBorder(10, 10, 0, 10));
    setLayout(new BorderLayout());

    //======== buttonBar ========
    {
      buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
      buttonBar.setLayout(new GridBagLayout());
      ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
      ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

      //---- zoneLabel ----
      zoneLabel.setForeground(Color.red);
      zoneLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          zoneLabelMouseClicked(e);
        }
      });
      buttonBar.add(zoneLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- okButton ----
      okButton.setText("OK");
      okButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          okButtonActionPerformed();
        }
      });
      buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- cancelButton ----
      cancelButton.setText("\u041e\u0442\u043c\u0435\u043d\u0430");
      cancelButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          cancelButtonActionPerformed();
        }
      });
      buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));

      //---- progressBar ----
      progressBar.setBorderPainted(false);
      progressBar.setMinimumSize(new Dimension(10, 8));
      progressBar.setPreferredSize(new Dimension(148, 8));
      buttonBar.add(progressBar, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(1, 0, 0, 0), 0, 0));
    }
    add(buttonBar, BorderLayout.SOUTH);

    //======== contentPanel ========
    {
      contentPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
      ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
      ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
      ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};

      //---- authTypeLabel ----
      authTypeLabel.setText("\u0420\u043e\u043b\u044c:");
      contentPanel.add(authTypeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));
      contentPanel.add(authTypeComboBox, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- loginLabel ----
      loginLabel.setText("E-mail:");
      contentPanel.add(loginLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- loginField ----
      loginField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          loginFieldActionPerformed();
        }
      });
      contentPanel.add(loginField, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- passLabel ----
      passLabel.setText("\u041f\u0430\u0440\u043e\u043b\u044c:");
      contentPanel.add(passLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 5), 0, 0));

      //---- passField ----
      passField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          passFieldActionPerformed();
        }
      });
      contentPanel.add(passField, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));

      //---- keepLoginCheckBox ----
      keepLoginCheckBox.setText("\u0417\u0430\u043f\u043e\u043c\u043d\u0438\u0442\u044c e-mail");
      contentPanel.add(keepLoginCheckBox, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));

      //---- resetPassLabel ----
      resetPassLabel.setText("\u0417\u0430\u0431\u044b\u043b\u0438 \u043f\u0430\u0440\u043e\u043b\u044c?");
      resetPassLabel.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          resetPassLabelActionPerformed();
        }
      });
      contentPanel.add(resetPassLabel, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
        new Insets(0, 10, 0, 0), 0, 0));
    }
    add(contentPanel, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
}
