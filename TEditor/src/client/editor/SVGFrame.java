/*
 * Created by JFormDesigner on Wed Sep 02 14:43:23 MSK 2015
 */

package client.editor;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import client.editor.component.PlanSvgPanel;
import server.protocol2.editor.CategoryObj;

/**
 * @author Maksim
 */
public class SVGFrame extends JFrame {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private PlanSvgPanel planSvgPanel;
  // JFormDesigner - End of variables declaration  //GEN-END:variables

  public SVGFrame(Window owner, String title, String planName, boolean combined, byte[] svgZip, List<CategoryObj> categoryList, long planId) {
    initComponents();
    setTitle(title);

    Rectangle gcBounds = owner.getGraphicsConfiguration().getBounds();
    Dimension imageSize = new Dimension((int) (gcBounds.width * 0.9), (int) ((gcBounds.height) * 0.9));
    setPreferredSize(imageSize);
    pack();
    setLocationRelativeTo(owner);
    planSvgPanel.setPlanName(planName);
    planSvgPanel.setVenueSvgZip(combined, svgZip, categoryList, planId);
  }

  public SVGFrame(String title, PlanSvgPanel planSvgPanel) {
    initComponents();
    setTitle(title);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    remove(this.planSvgPanel);
    add(planSvgPanel, BorderLayout.CENTER);
  }

  @Override
  public void dispose() {
    planSvgPanel.dispose();
    super.dispose();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    planSvgPanel = new PlanSvgPanel();

    //======== this ========
    setIconImages(Env.frameIcons);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //---- planSvgPanel ----
    planSvgPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.add(planSvgPanel, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
}
