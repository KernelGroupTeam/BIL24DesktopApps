package common.svg.swing;

import java.awt.*;
import javax.swing.*;

import org.apache.batik.swing.gvt.Overlay;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.12.16
 */
public class LabelOverlay implements Overlay {
  private final Anchor anchor;
  private final JComponent component;
  private final JLabel label;

  public LabelOverlay(Anchor anchor, JComponent component) {
    this.anchor = anchor;
    this.component = component;
    this.label = new JLabel();
  }

  public LabelOverlay(Anchor anchor, JComponent component, JLabel label) {
    this.anchor = anchor;
    this.component = component;
    this.label = label;
  }

  public Font getFont() {
    return label.getFont();
  }

  public void setFont(Font font) {
    label.setFont(font);
  }

  public String getText() {
    return label.getText();
  }

  public void setText(String text) {
    label.setText(text);
  }

  @Override
  public void paint(Graphics g) {
    if (label.getText().isEmpty()) return;
    layoutComponent(label);
    Dimension preferredSize = label.getPreferredSize();
    Rectangle rectangle = new Rectangle(preferredSize);
    switch (anchor) {
      case LOWER_LEFT:
        rectangle.setLocation(0, component.getHeight() - preferredSize.height);
        break;
      case LOWER_RIGHT:
        rectangle.setLocation(component.getWidth() - preferredSize.width, component.getHeight() - preferredSize.height);
        break;
    }
    CellRendererPane crp = new CellRendererPane();
    SwingUtilities.paintComponent(g, label, crp, rectangle);
  }

  private static void layoutComponent(Component c) {
    synchronized (c.getTreeLock()) {
      c.doLayout();
      if (c instanceof Container)
        for (Component child : ((Container) c).getComponents())
          layoutComponent(child);
    }
  }

  public enum Anchor {
    LOWER_LEFT, LOWER_RIGHT
  }
}
