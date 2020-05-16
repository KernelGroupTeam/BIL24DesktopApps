package client.layout;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 20.12.16
 */
public class LayeredPaneLayout implements LayoutManager {
  private final Container target;
  private final Dimension preferredSize = new Dimension(500, 500);

  public LayeredPaneLayout(Container target) {
    this.target = target;
  }

  @Override
  public void addLayoutComponent(String name, Component comp) {
  }

  @Override
  public void layoutContainer(Container container) {
    for (Component component : container.getComponents()) {
      component.setBounds(new Rectangle(0, 0, target.getWidth(), target.getHeight()));
    }
  }

  @Override
  public Dimension minimumLayoutSize(Container parent) {
    return preferredLayoutSize(parent);
  }

  @Override
  public Dimension preferredLayoutSize(Container parent) {
    return preferredSize;
  }

  @Override
  public void removeLayoutComponent(Component comp) {
  }
}
