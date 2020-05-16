/*
 * Created by JFormDesigner on Sat Dec 05 17:52:57 MSK 2015
 */

package client.editor.component;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;

import client.editor.Env;
import client.net.*;
import client.utils.Format;
import server.protocol2.Response;

/**
 * @author Maksim
 */
public class StatusBarPanel extends JPanel implements NetPoolListener {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JButton reloadButton;
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JLabel label4;
  private JLabel userTypeLabel;
  private JLabel authorityNameLabel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final DecimalFormat format = new DecimalFormat();

  public StatusBarPanel() {
    initComponents();
    //noinspection SuspiciousNameCombination
    reloadButton.setPreferredSize(new Dimension(reloadButton.getPreferredSize().height, reloadButton.getPreferredSize().height));
    label2.setPreferredSize(new Dimension(170, label2.getPreferredSize().height));
    label3.setPreferredSize(new Dimension(160, label3.getPreferredSize().height));
    label4.setPreferredSize(new Dimension(130, label4.getPreferredSize().height));
  }

  public void setUserType(String userType) {
    userTypeLabel.setText(userType);
  }

  public void setAuthorityName(String authorityName) {
    authorityNameLabel.setText(authorityName);
  }

  public void addReloadButtonActionListener(ActionListener l) {
    reloadButton.addActionListener(l);
  }

  private void consoleLabelMouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && !e.isConsumed()) {
      e.consume();
      Env.consoleFrame.setVisible(true);
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    JLabel consoleLabel = new JLabel();
    reloadButton = new JButton();
    label1 = new JLabel();
    label2 = new JLabel();
    label3 = new JLabel();
    label4 = new JLabel();
    userTypeLabel = new JLabel();
    authorityNameLabel = new JLabel();

    //======== this ========
    setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
    setLayout(new GridBagLayout());
    ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
    ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
    ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
    ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

    //---- consoleLabel ----
    consoleLabel.setText("\u041a\u043e\u043d\u0441\u043e\u043b\u044c");
    consoleLabel.setFont(consoleLabel.getFont().deriveFont(Font.PLAIN, consoleLabel.getFont().getSize() - 1f));
    consoleLabel.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    consoleLabel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        consoleLabelMouseClicked(e);
      }
    });
    add(consoleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- reloadButton ----
    reloadButton.setIcon(new ImageIcon(getClass().getResource("/resources/reload.png")));
    reloadButton.setMargin(new Insets(0, 0, 0, 0));
    reloadButton.setBorder(null);
    add(reloadButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- label1 ----
    label1.setFont(label1.getFont().deriveFont(Font.PLAIN, label1.getFont().getSize() - 1f));
    label1.setBorder(new CompoundBorder(
      new MatteBorder(0, 1, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    label1.setText(" ");
    add(label1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- label2 ----
    label2.setFont(label2.getFont().deriveFont(Font.PLAIN, label2.getFont().getSize() - 1f));
    label2.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    label2.setText(" ");
    add(label2, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- label3 ----
    label3.setFont(label3.getFont().deriveFont(Font.PLAIN, label3.getFont().getSize() - 1f));
    label3.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    label3.setText(" ");
    add(label3, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- label4 ----
    label4.setFont(label4.getFont().deriveFont(Font.PLAIN, label4.getFont().getSize() - 1f));
    label4.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    label4.setText(" ");
    add(label4, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- userTypeLabel ----
    userTypeLabel.setText(" ");
    userTypeLabel.setFont(userTypeLabel.getFont().deriveFont(Font.PLAIN, userTypeLabel.getFont().getSize() - 1f));
    userTypeLabel.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 1, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    add(userTypeLabel, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));

    //---- authorityNameLabel ----
    authorityNameLabel.setText(" ");
    authorityNameLabel.setFont(authorityNameLabel.getFont().deriveFont(Font.PLAIN, authorityNameLabel.getFont().getSize() - 1f));
    authorityNameLabel.setBorder(new CompoundBorder(
      new MatteBorder(0, 0, 0, 0, UIManager.getColor("Button.darkShadow")),
      new EmptyBorder(0, 2, 0, 2)));
    add(authorityNameLabel, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(0, 0, 0, 0), 0, 0));
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  private String getSent(NetTelemetry telemetry) {
    String result = "Отправлено: ";
    result += Format.bytesToStr(telemetry.getSentBytes());
    if (telemetry.isZipOutput()) {
      result += " (" + Format.bytesToStr(telemetry.getSentBytesZip()) + ")";
    }
    return result;
  }

  private String getReceived(NetTelemetry telemetry) {
    String result = "Получено: ";
    result += Format.bytesToStr(telemetry.getReceivedBytes());
    if (telemetry.isZipInput()) {
      result += " (" + Format.bytesToStr(telemetry.getReceivedBytesZip()) + ")";
    }
    return result;
  }

  @Override
  public void netState(Network<?, ?> network, NetEvent<?, ?> event, Network.State state) {
    if (state == Network.State.REQUEST_PROCESSING) {
      label1.setText("Подготовка запроса...");
      label2.setText("");
      label3.setText("");
      label4.setText("");
    }
    if (state == Network.State.ESTABLISHING_CONNECTION) {
      label1.setText("Соединение...");
      label2.setText("");
      label3.setText("");
      label4.setText("");
    }
    if (state == Network.State.CONNECTED) {
      label1.setText("Отправка запроса...");
      label2.setText("");
      label3.setText("");
      label4.setText("");
    }
    if (state == Network.State.DATA_SENT) {
      label1.setText("Обработка запроса...");
      label2.setText(getSent(network.getTelemetry()));
      label3.setText("");
      label4.setText("");
    }
    if (state == Network.State.HEADER_RECEIVED) {
      label1.setText("Получение данных...");
      label2.setText(getSent(network.getTelemetry()));
      label3.setText("");
      label4.setText("");
    }
  }

  @Override
  public void netResult(Network<?, ?> network, NetResultEvent<?, ?> result) {
    label1.setText("Запрос обработан");
    label2.setText(getSent(network.getTelemetry()));
    label3.setText(getReceived(network.getTelemetry()));
    try {
      Response response = (Response) result.getResponse();
      label4.setText("Обработка: " + format.format(response.getExecutionTime()) + " мс");
      if (!response.isSuccess()) label1.setText("Ошибка обработки запроса");
    } catch (Exception e) {
      label4.setText("");
      e.printStackTrace();
    }
  }

  @Override
  public void netError(Network<?, ?> network, NetErrorEvent<?, ?> error) {
    label1.setText("Ошибка соединения с сервером");
    label2.setText("");
    label3.setText("");
    label4.setText("");
  }
}
