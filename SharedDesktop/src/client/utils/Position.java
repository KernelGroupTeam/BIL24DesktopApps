package client.utils;

import java.awt.*;

import sun.awt.SunToolkit;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.03.17
 */
public class Position {
  public static final Position DEFAULT = new Position(null, null, 0, 0);
  public final Horizontal horizontal;
  public final Vertical vertical;
  public final int dx;
  public final int dy;

  public Position(Vertical vertical) {
    this(vertical, 0);
  }

  public Position(Horizontal horizontal) {
    this(horizontal, 0);
  }

  public Position(Vertical vertical, int dy) {
    this(null, vertical, 0, dy);
  }

  public Position(Horizontal horizontal, int dx) {
    this(horizontal, null, dx, 0);
  }

  public Position(Horizontal horizontal, Vertical vertical) {
    this(horizontal, vertical, 0, 0);
  }

  public Position(Horizontal horizontal, Vertical vertical, int dx, int dy) {
    this.horizontal = (horizontal == null ? Horizontal.DEFAULT : horizontal);
    this.vertical = (vertical == null ? Vertical.DEFAULT : vertical);
    this.dx = dx;
    this.dy = dy;
  }

  //на основе метода Window.setLocationRelativeTo(Component c)
  @SuppressWarnings({"MultipleVariablesInDeclaration", "UnusedAssignment"})
  public static void setLocationRelativeTo(Window window, Component c, Position position) {
    if (window == null || position == null || (position.horizontal == Horizontal.DEFAULT && position.vertical == Vertical.DEFAULT)) {
      return;
    }
    // target location
    int dx = 0, dy = 0;
    // target GC
    GraphicsConfiguration gc = window.getGraphicsConfiguration();
    Rectangle gcBounds = gc.getBounds();

    Dimension windowSize = window.getSize();

    // search a top-level of c
    Window componentWindow = SunToolkit.getContainingWindow(c);
    if ((c == null) || (componentWindow == null)) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
      gcBounds = gc.getBounds();
      Point centerPoint = ge.getCenterPoint();
      switch (position.horizontal) {
        case DEFAULT:
          break;
        case LEFT:
          dx = gcBounds.x;
          break;
        case CENTER:
          dx = centerPoint.x - windowSize.width / 2;
          break;
        case RIGHT:
          dx = gcBounds.x + gcBounds.width - windowSize.width;
          break;
      }
      switch (position.vertical) {
        case DEFAULT:
          break;
        case TOP:
          dy = gcBounds.y;
          break;
        case CENTER:
          dy = centerPoint.y - windowSize.height / 2;
          break;
        case BOTTOM:
          dy = gcBounds.y + gcBounds.height - windowSize.height;
          break;
      }
    } else if (!c.isShowing()) {
      gc = componentWindow.getGraphicsConfiguration();
      gcBounds = gc.getBounds();
      switch (position.horizontal) {
        case DEFAULT:
          break;
        case LEFT:
          dx = gcBounds.x;
          break;
        case CENTER:
          dx = gcBounds.x + (gcBounds.width - windowSize.width) / 2;
          break;
        case RIGHT:
          dx = gcBounds.x + gcBounds.width - windowSize.width;
          break;
      }
      switch (position.vertical) {
        case DEFAULT:
          break;
        case TOP:
          dy = gcBounds.y;
          break;
        case CENTER:
          dy = gcBounds.y + (gcBounds.height - windowSize.height) / 2;
          break;
        case BOTTOM:
          dy = gcBounds.y + gcBounds.height - windowSize.height;
          break;
      }
    } else {
      gc = componentWindow.getGraphicsConfiguration();
      gcBounds = gc.getBounds();
      Dimension compSize = c.getSize();
      Point compLocation = c.getLocationOnScreen();
      switch (position.horizontal) {
        case DEFAULT:
          break;
        case LEFT:
          dx = compLocation.x - windowSize.width;
          break;
        case CENTER:
          dx = compLocation.x + ((compSize.width - windowSize.width) / 2);
          break;
        case RIGHT:
          dx = compLocation.x + compSize.width;
          break;
      }
      switch (position.vertical) {
        case DEFAULT:
          break;
        case TOP:
          dy = compLocation.y - windowSize.height;
          break;
        case CENTER:
          dy = compLocation.y + ((compSize.height - windowSize.height) / 2);
          break;
        case BOTTOM:
          dy = compLocation.y + compSize.height;
          break;
      }

      // Adjust for bottom edge being offscreen
//      if (dy + windowSize.height > gcBounds.y + gcBounds.height) {
//        dy = gcBounds.y + gcBounds.height - windowSize.height;
//        if (compLocation.x - gcBounds.x + compSize.width / 2 < gcBounds.width / 2) {
//          dx = compLocation.x + compSize.width;
//        } else {
//          dx = compLocation.x - windowSize.width;
//        }
//      }
    }

    dx += position.dx;
    dy += position.dy;

    // Avoid being placed off the edge of the screen:
    // bottom
    if (dy + windowSize.height > gcBounds.y + gcBounds.height) {
      dy = gcBounds.y + gcBounds.height - windowSize.height;
    }
    // top
    if (dy < gcBounds.y) {
      dy = gcBounds.y;
    }
    // right
    if (dx + windowSize.width > gcBounds.x + gcBounds.width) {
      dx = gcBounds.x + gcBounds.width - windowSize.width;
    }
    // left
    if (dx < gcBounds.x) {
      dx = gcBounds.x;
    }

    Point currentLocation = window.getLocation();
    dx = (position.horizontal == Horizontal.DEFAULT ? currentLocation.x : dx);
    dy = (position.vertical == Vertical.DEFAULT ? currentLocation.y : dy);
    window.setLocation(dx, dy);
  }

  public enum Horizontal {DEFAULT, LEFT, CENTER, RIGHT}

  public enum Vertical {DEFAULT, TOP, CENTER, BOTTOM}
}
