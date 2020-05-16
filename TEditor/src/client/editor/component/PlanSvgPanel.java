/*
 * Created by JFormDesigner on Wed Sep 02 11:04:54 MSK 2015
 */

package client.editor.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import client.editor.Env;
import client.editor.cache.entity.SeatingPlanSvg;
import client.layout.LayeredPaneLayout;
import client.net.*;
import client.utils.Format;
import common.svg.*;
import common.svg.swing.*;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.swing.gvt.*;
import org.apache.batik.swing.svg.*;
import org.jdesktop.swingx.JXBusyLabel;
import org.jetbrains.annotations.*;
import org.w3c.dom.svg.SVGDocument;
import server.protocol2.*;
import server.protocol2.editor.CategoryObj;

/**
 * @author Maksim
 */
public class PlanSvgPanel extends JPanel implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JLayeredPane layeredPane;
  private JXBusyLabel busyLabel;
  private SVGCanvas svgCanvas;
  private JLabel errorLabel;
  private JLabel emptyLabel;
  private JLabel verLabel;
  private JLabel sizeLabel;
  private JPopupMenu popupMenu;
  private JMenuItem pngMenuItem;
  private JMenuItem svgMenuItem;
  private JMenuItem svgzMenuItem;
  private JMenuItem xmlMenuItem;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final SVGDocument emptyDocument = SVGDoc.createEmptyDocument();
  private static final Dimension normalSize = new Dimension(600, 300);
  private static final Dimension compactSize = new Dimension(600, 250);
  @NotNull
  private String planName = "";
  @Nullable
  private SVGLoader loader;
  @Nullable
  private SVGData svgData;
  @Nullable
  private AffineTransform lastTransform;

  public PlanSvgPanel() {
    initComponents();
    setSize(false);
    layeredPane.moveToFront(emptyLabel);
    svgCanvas.getOverlays().add(new LabelOverlay(LabelOverlay.Anchor.LOWER_LEFT, svgCanvas, verLabel));
    svgCanvas.getOverlays().add(new LabelOverlay(LabelOverlay.Anchor.LOWER_RIGHT, svgCanvas, sizeLabel));

    svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
      @Override
      public void gvtBuildFailed(GVTTreeBuilderEvent e) {
        busyLabel.setBusy(false);
        layeredPane.moveToFront(errorLabel);
      }
    });

    svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
      @Override
      public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
        if (busyLabel.isBusy()) {
          busyLabel.setBusy(false);
          layeredPane.moveToFront(svgCanvas);
          setPopupEnabled(true);
          if (lastTransform != null) svgCanvas.setRenderingTransform(lastTransform);
        }
      }

      @Override
      public void gvtRenderingFailed(GVTTreeRendererEvent e) {
        busyLabel.setBusy(false);
        layeredPane.moveToFront(errorLabel);
      }
    });
  }

  public void setSize(boolean compact) {
    if (compact) {
      setPreferredSize(new Dimension(compactSize));
      setMinimumSize(new Dimension(compactSize));
    } else {
      setPreferredSize(new Dimension(normalSize));
      setMinimumSize(new Dimension(normalSize));
    }
  }

  @NotNull
  public String getPlanName() {
    return planName;
  }

  public void setPlanName(@NotNull String planName) {
    this.planName = planName;
  }

  public void setVenueSvgZip(boolean combined, byte[] svgZip, List<CategoryObj> categoryList, long planId) {
    if (categoryList == null) this.svgData = null;
    else this.svgData = new VenueSVGData(combined, svgZip, categoryList, planId);
    updateSvgData();
  }

  public void setEventSvgData(boolean combined, byte[] svgData, Set<Long> ebsNotAvailIdSet, boolean ebsView,
                              Set<Long> selectedSeatSet, SVGDocEventSeatListener seatListener) {
    if (svgData == null || selectedSeatSet == null) this.svgData = null;
    else this.svgData = new EventSVGData(combined, svgData, ebsNotAvailIdSet, ebsView, selectedSeatSet, seatListener);
    updateSvgData();
  }

  public void clear() {
    svgData = null;
    updateSvgData();
  }

  public void setEventEbsView(final boolean ebsView) {
    if (svgData == null || svgData.getType() != 2 || loader == null) return;
    final SVGDocEvent svgDocEvent = (SVGDocEvent) loader.getSvgDoc();
    if (svgDocEvent == null) return;
    UpdateManager updateManager = svgCanvas.getUpdateManager();
    if (updateManager == null) return;//мелкая проблема - не можем выделить, пока строится изображение схемы
    updateManager.getUpdateRunnableQueue().invokeLater(new Runnable() {
      @Override
      public void run() {
        svgDocEvent.setEbsView(ebsView);
      }
    });
  }

  public void updateEventSelection(@NotNull Set<Long> selectedSeatSet) {
    if (svgData == null || svgData.getType() != 2 || loader == null) return;
    final SVGDocEvent svgDocEvent = (SVGDocEvent) loader.getSvgDoc();
    if (svgDocEvent == null) return;
    UpdateManager updateManager = svgCanvas.getUpdateManager();
    if (updateManager == null) return;//мелкая проблема - не можем выделить, пока строится изображение схемы
    final Set<Long> cloneSelectedSeatSet = new HashSet<>(selectedSeatSet);
    updateManager.getUpdateRunnableQueue().invokeLater(new Runnable() {
      @Override
      public void run() {
        svgDocEvent.setSelected(cloneSelectedSeatSet);
      }
    });
  }

  private void updateSvgData() {
    if (loader != null) loader.cancel();
    loader = new SVGLoader(svgData);
    loader.prepare();
    if (svgData == null || svgData.getType() != 1 || ((VenueSVGData) svgData).getSvgZip() != null) {
      loader.execute();
    } else {
      VenueSVGData venueSVGData = (VenueSVGData) svgData;
      long planId = venueSVGData.getPlanId();
      SeatingPlanSvg planSvg = Env.cache.getSeatingPlanSvg(planId);
      if (planSvg == null) {
        Env.net.create("GET_PLAN_SVG_ZIP", new Request(planId), this, 10000).start();
      } else {
        venueSVGData.setSvgZip(planSvg.getSvgZip());
        loader.execute();
      }
    }
  }

  public void dispose() {
    if (loader != null) loader.cancel();
  }

  @Override
  public synchronized void addMouseListener(MouseListener l) {
    svgCanvas.addMouseListener(l);
  }

  public void linkRenderingTransform(@NotNull PlanSvgPanel anotherSvgPanel) {
    linkRenderingTransform(anotherSvgPanel.svgCanvas);
  }

  private void linkRenderingTransform(@NotNull final SVGCanvas anotherSvgCanvas) {
    svgCanvas.addJGVTComponentListener(new JGVTComponentAdapter() {
      @Override
      public void componentTransformChanged(ComponentEvent event) {
        anotherSvgCanvas.setRenderingTransform(svgCanvas.getRenderingTransform());
      }
    });
  }

  private void setPopupEnabled(boolean b) {
    pngMenuItem.setEnabled(b);
    svgMenuItem.setEnabled(b);
    svgzMenuItem.setEnabled(b);
    xmlMenuItem.setEnabled(b);
  }

  private void thisMousePopup(MouseEvent e) {
    if (e.isPopupTrigger()) popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void pngMenuItemActionPerformed() {
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setFileFilter(exportPlanDialog.pngFilter);
    exportPlan();
  }

  private void svgMenuItemActionPerformed() {
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setFileFilter(exportPlanDialog.svgFilter);
    exportPlan();
  }

  private void svgzMenuItemActionPerformed() {
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setFileFilter(exportPlanDialog.svgzFilter);
    exportPlan();
  }

  private void xmlMenuItemActionPerformed() {
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setFileFilter(exportPlanDialog.xmlFilter);
    exportPlan();
  }

  private void exportPlan() {
    if (loader == null || loader.getSvg() == null || loader.getSvgDoc() == null) return;
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setSelectedFile(new File(planName));
    if (exportPlanDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = exportPlanDialog.getSelectedFile();
      byte[] bytes;
      FileFilter fileFilter = exportPlanDialog.getFileFilter();
      try {
        if (fileFilter.equals(exportPlanDialog.pngFilter)) {
          BufferedImage image = SVGTranscoder.svgToImage(loader.getSvgDoc().getDocument());
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(image, "png", baos);
          baos.flush();
          bytes = baos.toByteArray();
        } else if (fileFilter.equals(exportPlanDialog.svgFilter)) {
          bytes = loader.getSvg();
        } else if (fileFilter.equals(exportPlanDialog.svgzFilter)) {
          bytes = SVGTranscoder.toGzip(loader.getSvg());
        } else if (fileFilter.equals(exportPlanDialog.xmlFilter)) {
          bytes = SVGPlan.svgToSvgIndentXML(loader.getSvg());
        } else throw new IllegalStateException("file filter");
        Files.write(file.toPath(), bytes);
      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Произошла ошибка экспорта", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    layeredPane = new JLayeredPane();
    layeredPane.setLayout(new LayeredPaneLayout(layeredPane));
    busyLabel = new JXBusyLabel();
    svgCanvas = new SVGCanvas();
    errorLabel = new JLabel();
    emptyLabel = new JLabel();
    verLabel = new JLabel();
    sizeLabel = new JLabel();
    popupMenu = new JPopupMenu();
    pngMenuItem = new JMenuItem();
    svgMenuItem = new JMenuItem();
    svgzMenuItem = new JMenuItem();
    xmlMenuItem = new JMenuItem();

    //======== this ========
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        thisMousePopup(e);
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        thisMousePopup(e);
      }
    });
    setLayout(new BorderLayout());

    //======== layeredPane ========
    {

      //---- busyLabel ----
      busyLabel.setText("\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0441\u0445\u0435\u043c\u044b...");
      busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
      busyLabel.setOpaque(true);
      layeredPane.add(busyLabel, JLayeredPane.DEFAULT_LAYER);
      busyLabel.setBounds(new Rectangle(new Point(0, 0), busyLabel.getPreferredSize()));

      //---- svgCanvas ----
      svgCanvas.setBackground(UIManager.getColor("Panel.background"));
      layeredPane.add(svgCanvas, JLayeredPane.DEFAULT_LAYER);
      svgCanvas.setBounds(new Rectangle(new Point(0, 30), svgCanvas.getPreferredSize()));

      //---- errorLabel ----
      errorLabel.setText("\u041e\u0448\u0438\u0431\u043a\u0430");
      errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
      errorLabel.setOpaque(true);
      layeredPane.add(errorLabel, JLayeredPane.DEFAULT_LAYER);
      errorLabel.setBounds(new Rectangle(new Point(5, 235), errorLabel.getPreferredSize()));

      //---- emptyLabel ----
      emptyLabel.setOpaque(true);
      layeredPane.add(emptyLabel, JLayeredPane.DEFAULT_LAYER);
      emptyLabel.setBounds(new Rectangle(new Point(245, 10), emptyLabel.getPreferredSize()));
    }
    add(layeredPane, BorderLayout.CENTER);

    //---- verLabel ----
    verLabel.setText("\u0412\u0435\u0440\u0441\u0438\u044f \u0441\u0445\u0435\u043c\u044b:");
    verLabel.setFont(verLabel.getFont().deriveFont(Font.ITALIC, verLabel.getFont().getSize() - 1f));

    //---- sizeLabel ----
    sizeLabel.setText("\u0440\u0430\u0437\u043c\u0435\u0440:");
    sizeLabel.setFont(sizeLabel.getFont().deriveFont(Font.ITALIC, sizeLabel.getFont().getSize() - 1f));

    //======== popupMenu ========
    {

      //---- pngMenuItem ----
      pngMenuItem.setText("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u0432 png");
      pngMenuItem.setEnabled(false);
      pngMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          pngMenuItemActionPerformed();
        }
      });
      popupMenu.add(pngMenuItem);

      //---- svgMenuItem ----
      svgMenuItem.setText("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u0432 svg");
      svgMenuItem.setEnabled(false);
      svgMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          svgMenuItemActionPerformed();
        }
      });
      popupMenu.add(svgMenuItem);

      //---- svgzMenuItem ----
      svgzMenuItem.setText("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u0432 svgz");
      svgzMenuItem.setEnabled(false);
      svgzMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          svgzMenuItemActionPerformed();
        }
      });
      popupMenu.add(svgzMenuItem);

      //---- xmlMenuItem ----
      xmlMenuItem.setText("\u042d\u043a\u0441\u043f\u043e\u0440\u0442 \u0432 xml");
      xmlMenuItem.setEnabled(false);
      xmlMenuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          xmlMenuItemActionPerformed();
        }
      });
      popupMenu.add(xmlMenuItem);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    long planId = (Long) result.getRequest().getData();
    boolean cancelled = false;//уже другая схема загружается
    if (svgData == null || svgData.getType() != 1 || ((VenueSVGData) svgData).getPlanId() != planId) cancelled = true;
    if (!result.getResponse().isSuccess()) {
      if (!cancelled && loader != null) loader.error();
      return;
    }
    byte[] svgZip = (byte[]) result.getResponse().getData();
    Env.cache.put(new SeatingPlanSvg(planId, svgZip));
    if (!cancelled) {
      ((VenueSVGData) svgData).setSvgZip(svgZip);
      if (loader != null) loader.execute();
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    long planId = (Long) error.getRequest().getData();
    if (svgData != null && svgData.getType() == 1 && ((VenueSVGData) svgData).getPlanId() == planId) {
      if (loader != null) loader.error();
    }
  }

  private class SVGLoader extends SwingWorker<SVGDoc, Void> {
    @Nullable
    private final SVGData svgData;
    @Nullable
    private byte[] svg;
    @Nullable
    private SVGDoc svgDoc;
    private volatile boolean cancelled = false;

    public SVGLoader(@Nullable SVGData svgData) {
      this.svgData = svgData;
    }

    @Nullable
    public byte[] getSvg() {
      return svg;
    }

    @Nullable
    public SVGDoc getSvgDoc() {
      return svgDoc;
    }

    public void cancel() {
      //используем свою переменную, поскольку cancel может не успеть отменить задачу и она уже выполнена и находится
      //в очереди EDT на отрисовку, а нам нужно гарантировать, что после выполнения этого метода, панель никто не тронет
      cancelled = true;
      cancel(true);
    }

    public void prepare() {
      setPopupEnabled(false);
      busyLabel.setBusy(true);
      layeredPane.moveToFront(busyLabel);
    }

    public void error() {
      busyLabel.setBusy(false);
      layeredPane.moveToFront(errorLabel);
    }

    @Override
    protected SVGDoc doInBackground() throws Exception {
      if (svgData == null) return null;
      if (svgData.getType() == 1) {
        VenueSVGData venueSVGData = (VenueSVGData) svgData;
        svg = SVGTranscoder.fromGzip(venueSVGData.getSvgZip());
        if (isCancelled()) return null;
        svgDoc = new SVGDocVenue(svg, svgData.isCombined());
        for (CategoryObj category : venueSVGData.getCategoryList()) {
          if (category.isPlacement()) ((SVGDocVenue) svgDoc).setPrice(category.getId(), category.getInitPrice());
        }
      } else if (svgData.getType() == 2) {
        EventSVGData eventSVGData = (EventSVGData) svgData;
        svg = eventSVGData.getSvgData();
        svgDoc = new SVGDocEvent(svg, svgData.isCombined());
        ((SVGDocEvent) svgDoc).setEbsNotAvailIdSet(eventSVGData.getEbsNotAvailIdSet());
        ((SVGDocEvent) svgDoc).setEbsView(eventSVGData.isEbsView());
        ((SVGDocEvent) svgDoc).setSelected(eventSVGData.getSelectedSeatSet());
        ((SVGDocEvent) svgDoc).setSeatListener(eventSVGData.getSeatListener());
      }
      return svgDoc;
    }

    @Override
    protected void done() {//метод может быть вызван ДО окончания работы doInBackground() (например при отмене)
      if (cancelled || isCancelled()) return;
      try {
        SVGDoc result = get();
        lastTransform = null;
        svgCanvas.flushImageCache();
        if (result == null) {
          verLabel.setText("");
          sizeLabel.setText("");
          svgCanvas.setDocumentState(SVGCanvas.ALWAYS_STATIC);
          svgCanvas.setSVGDocument(emptyDocument);
        } else {
          if (svgData != null && svgData.getType() == 2) lastTransform = svgCanvas.getRenderingTransform();
          verLabel.setText(result.getVersionDesc());
          sizeLabel.setText(Format.bytesToStr(result.getSize()));
          svgCanvas.setDocumentState(result.getDocumentState());
          svgCanvas.setSVGDocument(result.getDocument());
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        e.printStackTrace();
        error();
      }
    }
  }

  private static class SVGData {
    private final byte type;
    private final boolean combined;

    public SVGData(byte type, boolean combined) {
      this.type = type;
      this.combined = combined;
    }

    public byte getType() {
      return type;
    }

    public boolean isCombined() {
      return combined;
    }
  }

  private static class VenueSVGData extends SVGData {
    @Nullable
    private byte[] svgZip;
    @NotNull
    private final List<CategoryObj> categoryList;
    private final long planId;

    public VenueSVGData(boolean combined, @Nullable byte[] svgZip, @NotNull List<CategoryObj> categoryList, long planId) {
      super((byte) 1, combined);
      this.svgZip = svgZip;
      this.categoryList = categoryList;
      this.planId = planId;
    }

    @Nullable
    public byte[] getSvgZip() {
      return svgZip;
    }

    public void setSvgZip(@NotNull byte[] svgZip) {
      this.svgZip = svgZip;
    }

    @NotNull
    public List<CategoryObj> getCategoryList() {
      return categoryList;
    }

    public long getPlanId() {
      return planId;
    }
  }

  private static class EventSVGData extends SVGData {
    @NotNull
    private final byte[] svgData;
    @NotNull
    private final Set<Long> ebsNotAvailIdSet;
    private final boolean ebsView;
    @NotNull
    private final Set<Long> selectedSeatSet;
    @Nullable
    private final SVGDocEventSeatListener seatListener;

    public EventSVGData(boolean combined, @NotNull byte[] svgData, @Nullable Set<Long> ebsNotAvailIdSet, boolean ebsView,
                        @NotNull Set<Long> selectedSeatSet, @Nullable SVGDocEventSeatListener seatListener) {
      super((byte) 2, combined);
      this.svgData = svgData;
      this.ebsNotAvailIdSet = (ebsNotAvailIdSet == null ? Collections.<Long>emptySet() : new HashSet<>(ebsNotAvailIdSet));
      this.ebsView = ebsView;
      this.selectedSeatSet = new HashSet<>(selectedSeatSet);
      this.seatListener = seatListener;
    }

    @NotNull
    public byte[] getSvgData() {
      return svgData;
    }

    @NotNull
    public Set<Long> getEbsNotAvailIdSet() {
      return ebsNotAvailIdSet;
    }

    public boolean isEbsView() {
      return ebsView;
    }

    @NotNull
    public Set<Long> getSelectedSeatSet() {
      return selectedSeatSet;
    }

    @Nullable
    public SVGDocEventSeatListener getSeatListener() {
      return seatListener;
    }
  }
}
