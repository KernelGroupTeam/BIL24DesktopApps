package client.component;

import java.awt.*;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.03.19
 * <p/>
 * To avoid bug in java 7-12 with SCROLL_TAB_LAYOUT preferred size calculation
 * https://bugs.openjdk.java.net/browse/JDK-7151452
 * https://bugs.openjdk.java.net/browse/JDK-8215396
 * https://stackoverflow.com/questions/34944950/size-issues-with-jtabbedpane-and-fixed-height-content
 */
public class FixedTabbedPane extends JTabbedPane {
  private Insets tabAreaInsets;
  private boolean needFix;

  public FixedTabbedPane() {
    tabAreaInsets = getUITabAreaInsets();
    needFix = checkNeedFix();
  }

  public FixedTabbedPane(int tabPlacement) {
    super(tabPlacement);
    tabAreaInsets = getUITabAreaInsets();
    needFix = checkNeedFix();
  }

  public FixedTabbedPane(int tabPlacement, int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
    tabAreaInsets = getUITabAreaInsets();
    needFix = checkNeedFix();
  }

  private static Insets getUITabAreaInsets() {
    Object o = UIManager.getDefaults().get("TabbedPane.tabAreaInsets");
    if (o instanceof Insets) return ((Insets) o);
    return null;
  }

  private static boolean checkNeedFix() {
    String java = System.getProperty("java.version");
    return java != null && (java.startsWith("1.7.") || java.startsWith("1.8.") || java.startsWith("9.")
        || java.startsWith("10.") || java.startsWith("11.") || java.startsWith("12."));
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    if (isPreferredSizeSet() || getTabLayoutPolicy() != JTabbedPane.SCROLL_TAB_LAYOUT || tabAreaInsets == null || !needFix) {
      return size;
    }
    return new Dimension(size.width, size.height + tabAreaInsets.top + tabAreaInsets.bottom);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    tabAreaInsets = getUITabAreaInsets();
  }
}
