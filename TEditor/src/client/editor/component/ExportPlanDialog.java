package client.editor.component;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

import client.component.FileChooser;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.06.16
 */
public class ExportPlanDialog extends FileChooser {
  public final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("Формат png", "png");
  public final FileNameExtensionFilter svgFilter = new FileNameExtensionFilter("Формат svg", "svg");
  public final FileNameExtensionFilter svgzFilter = new FileNameExtensionFilter("Формат svgz", "svgz");
  public final FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("Формат xml", "xml");

  public ExportPlanDialog() {
    init();
  }

  public ExportPlanDialog(File currentDirectory) {
    super(currentDirectory);
    init();
  }

  private void init() {
    setOverwritingConfirmation(false);
    setDialogType(SAVE_DIALOG);
    setDialogTitle("Экспорт схемы зала");
    setAcceptAllFileFilterUsed(false);
    addChoosableFileFilter(pngFilter);
    addChoosableFileFilter(svgFilter);
    addChoosableFileFilter(svgzFilter);
    addChoosableFileFilter(xmlFilter);
    setFileFilter(pngFilter);
  }

  @Override
  public void approveSelection() {
    File file = getSelectedFile();
    FileFilter fileFilter = getFileFilter();
    String ext = null;
    if (fileFilter.equals(pngFilter)) ext = ".png";
    else if (fileFilter.equals(svgFilter)) ext = ".svg";
    else if (fileFilter.equals(svgzFilter)) ext = ".svgz";
    else if (fileFilter.equals(xmlFilter)) ext = ".xml";
    if (ext != null && !file.getName().toLowerCase().endsWith(ext)) {
      file = new File(file.getAbsolutePath() + ext);
      setSelectedFile(file);
    }
    if (file.exists() && getDialogType() == SAVE_DIALOG) {
      if (JOptionPane.showConfirmDialog(this, "Перезаписать файл \"" + file.getName() + "\"?", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
        super.approveSelection();
      }
    } else super.approveSelection();
  }
}
