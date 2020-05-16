/*
 * Created by JFormDesigner on Wed Sep 02 11:04:54 MSK 2015
 */

package client.editor.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import client.editor.Env;
import client.editor.cache.entity.SeatingPlanSvg;
import client.net.*;
import client.utils.Format;
import common.svg.*;
import org.jdesktop.swingx.JXBusyLabel;
import org.jetbrains.annotations.*;
import server.protocol2.*;
import server.protocol2.editor.CategoryObj;

/**
 * @author Maksim
 */
public class PlanImagePanel extends JPanel implements NetListener<Request, Response> {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JXBusyLabel busyLabel;
  private JLabel svgLabel;
  private JLabel errorLabel;
  private JLabel verLabel;
  private JLabel sizeLabel;
  private JPopupMenu popupMenu;
  private JMenuItem pngMenuItem;
  private JMenuItem svgMenuItem;
  private JMenuItem svgzMenuItem;
  private JMenuItem xmlMenuItem;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  private static final Dimension normalSize = new Dimension(600, 300);
  private static final Dimension compactSize = new Dimension(600, 250);
  private static final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
    final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(@NotNull Runnable r) {
      Thread thread = defaultFactory.newThread(r);
      thread.setName("SVGImageResize-" + thread.getName());
      return thread;
    }
  });
  @NotNull
  private String planName = "";
  @Nullable
  private SVGLoader loader;
  @Nullable
  private ResizeListener resizeListener = null;
  @Nullable
  private SVGData svgData;

  public PlanImagePanel() {
    initComponents();
    setSize(false);
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

  public void setVenueSvgZip(byte[] svgZip, List<CategoryObj> categoryList, long planId) {
    if (categoryList == null) this.svgData = null;
    else this.svgData = new SVGDataVenue(svgZip, categoryList, planId);
    updateSvgData();
  }

  public void setEventSvgData(byte[] svgData, List<Long> selectedSeatsList) {
    if (svgData == null || selectedSeatsList == null) this.svgData = null;
    else this.svgData = new SVGDataEvent(svgData, selectedSeatsList);
    updateSvgData();
  }

  public void clear() {
    svgData = null;
    updateSvgData();
  }

  private void updateSvgData() {
    if (loader != null) loader.cancel();
    loader = new SVGLoader(svgData);
    loader.prepare();
    if (svgData == null || svgData.getType() != 1 || ((SVGDataVenue) svgData).getSvgZip() != null) {
      loader.execute();
    } else {
      SVGDataVenue svgDataVenue = (SVGDataVenue) svgData;
      long planId = svgDataVenue.getPlanId();
      SeatingPlanSvg planSvg = Env.cache.getSeatingPlanSvg(planId);
      if (planSvg == null) {
        Env.net.create("GET_PLAN_SVG_ZIP", new Request(planId), this, 10000).start();
      } else {
        svgDataVenue.setSvgZip(planSvg.getSvgZip());
        loader.execute();
      }
    }
  }

  public void dispose() {
    if (loader != null) loader.cancel();
    if (resizeListener != null) resizeListener.dispose();
  }

  private Dimension getInnerSize() {
    Border border = getBorder();
    if (border == null) return new Dimension(getWidth(), getHeight());
    Insets insets = border.getBorderInsets(this);
    int dx = insets.left + insets.right;
    int dy = insets.top + insets.bottom;
    return new Dimension(getWidth() - dx, getHeight() - dy);
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
    if (loader == null || loader.getSvg() == null || loader.getImage() == null) return;
    ExportPlanDialog exportPlanDialog = FileChoosers.getExportPlanDialog();
    exportPlanDialog.setSelectedFile(new File(planName));
    if (exportPlanDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = exportPlanDialog.getSelectedFile();
      byte[] bytes;
      FileFilter fileFilter = exportPlanDialog.getFileFilter();
      try {
        if (fileFilter.equals(exportPlanDialog.pngFilter)) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(loader.getImage(), "png", baos);
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
    busyLabel = new JXBusyLabel();
    svgLabel = new JLabel();
    errorLabel = new JLabel();
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

    //---- busyLabel ----
    busyLabel.setText("\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0441\u0445\u0435\u043c\u044b...");
    busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

    //---- svgLabel ----
    svgLabel.setHorizontalAlignment(SwingConstants.CENTER);

    //---- errorLabel ----
    errorLabel.setText("\u041e\u0448\u0438\u0431\u043a\u0430");
    errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

    //---- verLabel ----
    verLabel.setText("\u0412\u0435\u0440\u0441\u0438\u044f \u0441\u0445\u0435\u043c\u044b:");
    verLabel.setHorizontalAlignment(SwingConstants.LEFT);
    verLabel.setFont(verLabel.getFont().deriveFont(Font.ITALIC, verLabel.getFont().getSize() - 1f));
    verLabel.setVerticalAlignment(SwingConstants.BOTTOM);

    //---- sizeLabel ----
    sizeLabel.setText("\u0440\u0430\u0437\u043c\u0435\u0440:");
    sizeLabel.setFont(sizeLabel.getFont().deriveFont(Font.ITALIC, sizeLabel.getFont().getSize() - 1f));
    sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    sizeLabel.setVerticalAlignment(SwingConstants.BOTTOM);

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
    if (svgData == null || svgData.getType() != 1 || ((SVGDataVenue) svgData).getPlanId() != planId) cancelled = true;
    if (!result.getResponse().isSuccess()) {
      if (!cancelled && loader != null) loader.error();
      return;
    }
    byte[] svgZip = (byte[]) result.getResponse().getData();
    Env.cache.put(new SeatingPlanSvg(planId, svgZip));
    if (!cancelled) {
      ((SVGDataVenue) svgData).setSvgZip(svgZip);
      if (loader != null) loader.execute();
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    long planId = (Long) error.getRequest().getData();
    if (svgData != null && svgData.getType() == 1 && ((SVGDataVenue) svgData).getPlanId() == planId) {
      if (loader != null) loader.error();
    }
  }

  private class ResizeListener extends ComponentAdapter implements Runnable {
    private final PlanImagePanel svgPanel;
    private volatile boolean terminated = false;
    private Dimension paintedSize;
    private Dimension currentSize;
    private boolean resized = false;
    private long lastResize;

    public ResizeListener() {
      this.svgPanel = PlanImagePanel.this;
      this.paintedSize = svgPanel.getSize();
      this.currentSize = paintedSize;
    }

    @Override
    public void componentResized(ComponentEvent e) {
      lastResize = System.currentTimeMillis();
      currentSize = svgPanel.getSize();
      resized = !currentSize.equals(paintedSize);
    }

    @Override
    public void run() {
      try {
        while (!terminated) {
          Thread.sleep(500);
          if (!resized) continue;
          if (System.currentTimeMillis() - lastResize < 1000) continue;
          if (terminated) return;
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              svgPanel.updateSvgData();
            }
          });
          paintedSize = currentSize;
          resized = false;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    public void dispose() {
      terminated = true;
    }
  }

  private class SVGLoader extends SwingWorker<ImageIcon, Void> {
    private final SVGData svgData;
    private final JPanel svgPanel;
    private byte[] svg;
    private BufferedImage image;
    private volatile boolean cancelled = false;

    public SVGLoader(@Nullable SVGData svgData) {
      this.svgData = svgData;
      this.svgPanel = PlanImagePanel.this;
    }

    public byte[] getSvg() {
      return svg;
    }

    public BufferedImage getImage() {
      return image;
    }

    public void cancel() {
      cancelled = true;//используем свою переменную, поскольку isCancelled далеко не всегда возвращает true
      cancel(true);
    }

    public void prepare() {
      setPopupEnabled(false);
      busyLabel.setBusy(true);
      svgPanel.removeAll();
      svgPanel.add(busyLabel, BorderLayout.CENTER);
      svgPanel.revalidate();
    }

    public void error() {
      busyLabel.setBusy(false);
      svgPanel.removeAll();
      svgPanel.add(errorLabel, BorderLayout.CENTER);
      svgPanel.revalidate();
      svgPanel.repaint();
    }

    @Override
    protected ImageIcon doInBackground() throws Exception {
      Dimension size = getInnerSize();
      verLabel.setText("");
      verLabel.setSize(size);
      sizeLabel.setText("");
      sizeLabel.setSize(size);
      if (svgData == null) return new ImageIcon();
      BigDecimal version = null;
      String sizeStr = "";
      ImageIcon icon;
      if (svgData.getType() == 1) {
        SVGDataVenue svgDataVenue = (SVGDataVenue) svgData;
        svg = SVGTranscoder.fromGzip(svgDataVenue.getSvgZip());
        sizeStr = Format.bytesToStr(svg.length);
        SVGPlanVenue plan = new SVGPlanVenue(svg);
        version = plan.getVersion();
        for (CategoryObj category : svgDataVenue.getCategoryList()) {
          if (category.isPlacement()) plan.setEditorPrice(category.getId(), category.getInitPrice());
        }
        image = SVGTranscoder.svgToImage(plan.getEditorSvgData());
        icon = new ImageIcon(SVGTranscoder.scaleImage(image, size.width, size.height));
      } else if (svgData.getType() == 2) {
        SVGDataEvent svgDataEvent = (SVGDataEvent) svgData;
        svg = svgDataEvent.getSvgData();
        sizeStr = Format.bytesToStr(svg.length);
        SVGPlanEvent plan = new SVGPlanEvent(svg);
        version = plan.getVersion();
        for (Long seatId : svgDataEvent.getSelectedSeatsList()) {
          plan.setEditorSelected(seatId);
        }
        image = SVGTranscoder.svgToImage(plan.getEditorSvgData());
        icon = new ImageIcon(SVGTranscoder.scaleImage(image, size.width, size.height));
      } else icon = new ImageIcon();
      String verStr = "";
      if (version != null) {
        verStr = "Версия схемы: " + version.toString();
        if (version.compareTo(SVGPlan.VER_11) < 0) verStr += " (управление категориями не поддерживается)";
      }
      verLabel.setText(verStr);
      sizeLabel.setText(sizeStr);
      return icon;
    }

    @Override
    protected void done() {
      if (cancelled) return;
      busyLabel.setBusy(false);
      try {
        ImageIcon result = get();
        svgLabel.setIcon(result);
        svgPanel.removeAll();
        svgLabel.add(verLabel);
        svgLabel.add(sizeLabel);
        svgPanel.add(svgLabel, BorderLayout.CENTER);
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
        svgPanel.removeAll();
        svgPanel.add(errorLabel, BorderLayout.CENTER);
      }
      svgPanel.revalidate();
      svgPanel.repaint();
      if (resizeListener == null) {
        resizeListener = new ResizeListener();
        addComponentListener(resizeListener);
        pool.execute(resizeListener);
      }
      setPopupEnabled(true);
    }
  }

  private static class SVGData {
    private final byte type;

    public SVGData(byte type) {
      this.type = type;
    }

    public byte getType() {
      return type;
    }
  }

  private static class SVGDataVenue extends SVGData {
    @Nullable
    private byte[] svgZip;
    @NotNull
    private final List<CategoryObj> categoryList;
    private final long planId;

    public SVGDataVenue(@Nullable byte[] svgZip, @NotNull List<CategoryObj> categoryList, long planId) {
      super((byte) 1);
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

  private static class SVGDataEvent extends SVGData {
    @NotNull
    private final byte[] svgData;
    @NotNull
    private final List<Long> selectedSeatsList;

    public SVGDataEvent(@NotNull byte[] svgData, @NotNull List<Long> selectedSeatsList) {
      super((byte) 2);
      this.svgData = svgData;
      this.selectedSeatsList = selectedSeatsList;
    }

    @NotNull
    public byte[] getSvgData() {
      return svgData;
    }

    @NotNull
    public List<Long> getSelectedSeatsList() {
      return selectedSeatsList;
    }
  }
}
