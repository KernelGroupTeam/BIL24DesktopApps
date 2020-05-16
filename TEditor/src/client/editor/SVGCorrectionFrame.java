/*
 * Created by JFormDesigner on Wed Sep 02 14:43:23 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.component.*;
import client.component.listener.OperationListener;
import client.editor.component.*;
import client.editor.component.renderer.SeatingPlanListRenderer2;
import client.net.*;
import common.svg.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.*;
import server.protocol2.editor.SeatingPlanObj;

import static client.editor.Env.net;
import static client.editor.Env.user;

/**
 * @author Maksim
 */
public class SVGCorrectionFrame extends JFrame implements NetListener<Request,Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private StatusBarPanel statusBarPanel;
  private OperationComboBox<SeatingPlanObj> planComboBox;
  private JButton correctButton;
  private PlanSvgPanel svgPanel1;
  private PlanSvgPanel svgPanel2;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private final WaitingDialog waitingDialog;
  private byte[] svgZip = null;

  public SVGCorrectionFrame() {
    initComponents();
    if (Env.testZone) setTitle(getTitle() + " [Тестовая зона]");

    SeatingPlanListRenderer2 renderer = new SeatingPlanListRenderer2();
    planComboBox.setRenderer(renderer);
    planComboBox.setElementToStringConverter(renderer);
    planComboBox.setOperationListener(new PlanOperationListener());

    svgPanel2.linkRenderingTransform(svgPanel1);

    statusBarPanel.setUserType(user.getUserType().getDesc());
    statusBarPanel.setAuthorityName(user.getAuthorityName());
    statusBarPanel.addReloadButtonActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        net.create("GET_SVG_PLAN_LIST", new Request(null), SVGCorrectionFrame.this).start();
      }
    });
    net.addPoolListener(statusBarPanel, Network.EventMode.EDT_INVOKE_LATER);
    waitingDialog = new WaitingDialog(this, Dialog.ModalityType.APPLICATION_MODAL);

    Rectangle gcBounds = this.getGraphicsConfiguration().getBounds();
    Dimension frameSize = new Dimension((int) (gcBounds.width * 0.9), (int) ((gcBounds.height) * 0.9));
    setPreferredSize(frameSize);
    pack();
    setLocationRelativeTo(null);
  }

  public void startWork() {
    this.setVisible(true);
    net.create("GET_SVG_PLAN_LIST", new Request(null), this).start();
  }

  @Override
  public void dispose() {
    svgPanel1.dispose();
    svgPanel2.dispose();
    super.dispose();
  }

  private void correctButtonActionPerformed() {
    SeatingPlanObj seatingPlan = planComboBox.getSelectedElement();
    if (seatingPlan == null) return;
    net.create("PATCH_SVG", new Request(seatingPlan.getId()), this, 100000).start();
    correctButton.setEnabled(false);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    statusBarPanel = new StatusBarPanel();
    JPanel mainPanel = new JPanel();
    JPanel planPanel = new JPanel();
    JLabel label1 = new JLabel();
    planComboBox = new OperationComboBox<>();
    correctButton = new JButton();
    svgPanel1 = new PlanSvgPanel();
    svgPanel2 = new PlanSvgPanel();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("\u041a\u043e\u0440\u0440\u0435\u043a\u0446\u0438\u044f SVG");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(statusBarPanel, BorderLayout.SOUTH);

    //======== mainPanel ========
    {
      mainPanel.setBorder(new EmptyBorder(5, 5, 1, 5));
      mainPanel.setLayout(new GridBagLayout());
      ((GridBagLayout)mainPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
      ((GridBagLayout)mainPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
      ((GridBagLayout)mainPanel.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
      ((GridBagLayout)mainPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

      //======== planPanel ========
      {
        planPanel.setLayout(new GridBagLayout());
        ((GridBagLayout)planPanel.getLayout()).columnWidths = new int[] {0, 105, 0, 0};
        ((GridBagLayout)planPanel.getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)planPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)planPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("\u0421\u0445\u0435\u043c\u0430 \u0437\u0430\u043b\u0430:");
        planPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- planComboBox ----
        planComboBox.setMaximumRowCount(15);
        planPanel.add(planComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 5), 0, 0));

        //---- correctButton ----
        correctButton.setIcon(new ImageIcon(getClass().getResource("/resources/save.png")));
        correctButton.setMargin(new Insets(2, 2, 2, 2));
        correctButton.setToolTipText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f");
        correctButton.setEnabled(false);
        correctButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            correctButtonActionPerformed();
          }
        });
        planPanel.add(correctButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
      }
      mainPanel.add(planPanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0), 0, 0));
      mainPanel.add(svgPanel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 5), 0, 0));
      mainPanel.add(svgPanel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    }
    contentPane.add(mainPanel, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
    if (state == Network.State.STARTED) waitingDialog.setVisible(true);
    if (state == Network.State.FINISHED) waitingDialog.setVisible(false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    if (!result.getResponse().isSuccess()) {
      if (result.getCommand().startsWith("GET_PLAN_SVG_ZIP")) {
        svgZip = null;
        waitingDialog.setVisible(false);
      }
      JOptionPane.showMessageDialog(this, result.getResponse().getErrorForUser(), "Ошибка", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (result.getCommand().equals("GET_SVG_PLAN_LIST")) {
      List<SeatingPlanObj> seatingPlanList = (List<SeatingPlanObj>) result.getResponse().getData();
      planComboBox.setElementList(seatingPlanList);
    }
    if (result.getCommand().startsWith("GET_PLAN_SVG_ZIP")) {
      svgZip = (byte[]) result.getResponse().getData();
      waitingDialog.setVisible(false);
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    if (error.getCommand().startsWith("GET_PLAN_SVG_ZIP")) {
      svgZip = null;
      waitingDialog.setVisible(false);
    }
    JOptionPane.showMessageDialog(this, "Ошибка соединения с сервером. Не удалось загрузить данные", "Ошибка", JOptionPane.ERROR_MESSAGE);
  }

  private class PlanOperationListener implements OperationListener<SeatingPlanObj> {
    @Override
    public void clear() {
      svgZip = null;
      svgPanel1.clear();
      svgPanel2.clear();
      correctButton.setEnabled(false);
    }

    @Override
    public boolean check() {
      return false;
    }

    @Override
    public void load(@NotNull SeatingPlanObj plan) {
      correctButton.setEnabled(false);
      if (!plan.isPlacement()) return;
      svgZip = null;
      System.gc();
      Network<Request, Response> network = net.create("GET_PLAN_SVG_ZIP", new Request(plan.getId()), SVGCorrectionFrame.this, 10000);
      network.setFireStartFinish(false);
      network.start();
      waitingDialog.setVisible(true);

      if (svgZip == null) return;
      svgPanel1.setPlanName(plan.getName());
      svgPanel1.setVenueSvgZip(plan.isCombined(), svgZip, plan.getCategoryList(), plan.getId());
      try {
        byte[] svgData = SVGTranscoder.fromGzip(svgZip);
        AtomicReference<byte[]> result = new AtomicReference<>();
        new SVGPlanVenue(svgData, true, result);
        byte[] svgData2 = result.get();
        if (svgData2 == null) {
          svgPanel2.clear();
        } else {
          byte[] svgZip2 = SVGTranscoder.toGzip(svgData2);
          svgPanel2.setPlanName(plan.getName());
          svgPanel2.setVenueSvgZip(plan.isCombined(), svgZip2, plan.getCategoryList(), plan.getId());
          correctButton.setEnabled(true);
        }
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(svgPanel2, "Ошибка коррекции", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }

    @Override
    public boolean save(@NotNull SeatingPlanObj plan) {
      return false;
    }
  }
}
