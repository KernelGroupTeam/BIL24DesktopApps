package common.svg.swing;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.*;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.w3c.dom.svg.SVGDocument;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 20.12.16
 */
public class SVGCanvas extends JSVGCanvas {
  protected final Interactor panInteractor2 = new AbstractPanInteractor() {
    @Override
    public boolean startInteraction(InputEvent ie) {
      int mods = ie.getModifiers();
      return ie.getID() == MouseEvent.MOUSE_PRESSED && (mods & InputEvent.BUTTON1_MASK) != 0;
    }
  };
  protected final Interactor resetTransformInteractor2 = new AbstractResetTransformInteractor() {
    @Override
    public boolean startInteraction(InputEvent ie) {
      int mods = ie.getModifiers();
      return ie.getID() == MouseEvent.MOUSE_CLICKED && (mods & InputEvent.BUTTON2_MASK) != 0;
    }
  };

  public SVGCanvas() {
    this(null, true, false);
  }

  public SVGCanvas(SVGUserAgent ua, boolean eventsEnabled, boolean selectableText) {
    super(ua, eventsEnabled, selectableText);

    List<Interactor> list = getInteractors();
    list.remove(zoomInteractor);
    list.remove(imageZoomInteractor);
    list.remove(panInteractor);
    list.remove(rotateInteractor);
    list.remove(resetTransformInteractor);

    list.add(panInteractor2);
    list.add(resetTransformInteractor2);

    this.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        JGVTComponent c = (JGVTComponent) e.getSource();
        int xStart = e.getX();
        int yStart = e.getY();
        double s;
        if (e.getWheelRotation() < 0) {
          s = 1.25;
        } else {
          s = 0.8;
        }
        AffineTransform ot = c.getRenderingTransform();
        double tx = -xStart * (s - 1.0);
        double ty = -yStart * (s - 1.0);
        AffineTransform st = AffineTransform.getTranslateInstance(tx, ty);
        AffineTransform at = AffineTransform.getScaleInstance(s, s);
        ot.preConcatenate(at);
        ot.preConcatenate(st);
        c.setRenderingTransform(ot);
      }
    });
  }

  @Override
  public void setSVGDocument(SVGDocument doc) {
    toolTipMap = null;//позволяем GC собрать старый Map
    super.setSVGDocument(doc);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Overlay> getOverlays() {
    return super.getOverlays();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Interactor> getInteractors() {
    return super.getInteractors();
  }
}
