package client.editor.component;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.component.FileChooser;
import client.component.filter.IdFileFilter;
import client.editor.Env;
import client.editor.component.filter.BookletFileFilter;
import server.protocol2.editor.BookletType;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.03.17
 */
@SuppressWarnings("unused")
public class FileChoosers {
  private static FileChooser openImageDialog = null;
  private static File openImageDirectory = null;
  private static FileChooser openSvgDialog = null;
  private static File openSvgDirectory = null;
  private static FileChooser openSplDialog = null;
  private static File openSplDirectory = null;
  private static FileChooser openQuotaDialog = null;
  private static File openQuotaDirectory = null;
  private static FileChooser openBookletDialog = null;
  private static File openBookletDirectory = null;
  private static FileChooser saveQuotaDialog = null;
  private static File saveQuotaDirectory = null;
  private static ExportPlanDialog exportPlanDialog = null;
  private static File exportPlanDirectory = null;

  private FileChoosers() {
  }

  public static void setOpenImageDirectory(File openImageDirectory) {
    FileChoosers.openImageDirectory = openImageDirectory;
  }

  public static void createOpenImageDialog(File currentDirectory) {
    openImageDialog = new FileChooser(currentDirectory);
    openImageDialog.setDialogTitle("Открыть постер");
    openImageDialog.setAcceptAllFileFilterUsed(false);
    openImageDialog.addChoosableFileFilter(new FileNameExtensionFilter("Изображения", ImageIO.getReaderFileSuffixes()));
  }

  public static FileChooser getOpenImageDialog() {
    if (openImageDialog == null) createOpenImageDialog(openImageDirectory);
    return openImageDialog;
  }

  public static void setOpenSvgDirectory(File openSvgDirectory) {
    FileChoosers.openSvgDirectory = openSvgDirectory;
  }

  public static void createOpenSvgDialog(File currentDirectory) {
    openSvgDialog = new FileChooser(currentDirectory);
    openSvgDialog.setDialogTitle("Открыть схему зала");
    openSvgDialog.setAcceptAllFileFilterUsed(false);
    openSvgDialog.addChoosableFileFilter(new IdFileFilter<>(0, "Схема зала", "svg", "svgz"));
    openSvgDialog.addChoosableFileFilter(new IdFileFilter<>(1, "Схема зала spl", "spl"));
  }

  public static FileChooser getOpenSvgDialog() {
    if (openSvgDialog == null) createOpenSvgDialog(openSvgDirectory);
    return openSvgDialog;
  }

  public static void setOpenSplDirectory(File openSplDirectory) {
    FileChoosers.openSplDirectory = openSplDirectory;
  }

  public static void createOpenSplDialog(File currentDirectory) {
    openSplDialog = new FileChooser(currentDirectory);
    openSplDialog.setDialogTitle("Открыть схему зала spl");
    openSplDialog.setAcceptAllFileFilterUsed(false);
    openSplDialog.addChoosableFileFilter(new FileNameExtensionFilter("Схема зала spl", "spl"));
  }

  public static FileChooser getOpenSplDialog() {
    if (openSplDialog == null) createOpenSplDialog(openSplDirectory);
    return openSplDialog;
  }

  public static void setOpenQuotaDirectory(File openQuotaDirectory) {
    FileChoosers.openQuotaDirectory = openQuotaDirectory;
  }

  public static void createOpenQuotaDialog(File currentDirectory) {
    openQuotaDialog = new FileChooser(currentDirectory);
    openQuotaDialog.setDialogTitle("Открыть файл квоты");
  }

  public static FileChooser getOpenQuotaDialog() {
    if (openQuotaDialog == null) createOpenQuotaDialog(openQuotaDirectory);
    return openQuotaDialog;
  }

  public static void setOpenBookletDirectory(File openBookletDirectory) {
    FileChoosers.openBookletDirectory = openBookletDirectory;
  }

  public static void createOpenBookletDialog(File currentDirectory) {
    openBookletDialog = new FileChooser(currentDirectory);
    openBookletDialog.setDialogTitle("Открыть буклет");
    openBookletDialog.setAcceptAllFileFilterUsed(false);
  }

  public static FileChooser getOpenBookletDialog() {
    if (openBookletDialog == null) createOpenBookletDialog(openBookletDirectory);
    openBookletDialog.resetChoosableFileFilters();
    for (BookletType type : Env.bookletTypeList) {
      openBookletDialog.addChoosableFileFilter(new BookletFileFilter(type.getId(), type.getDesc(), type.getFileFilterDesc(), type.getExtension()));
    }
    return openBookletDialog;
  }

  public static void setSaveQuotaDirectory(File saveQuotaDirectory) {
    FileChoosers.saveQuotaDirectory = saveQuotaDirectory;
  }

  public static void createSaveQuotaDialog(File currentDirectory) {
    saveQuotaDialog = new FileChooser(currentDirectory);
    saveQuotaDialog.setDialogType(JFileChooser.SAVE_DIALOG);
    saveQuotaDialog.setDialogTitle("Сохранение файла квоты");
  }

  public static FileChooser getSaveQuotaDialog() {
    if (saveQuotaDialog == null) createSaveQuotaDialog(saveQuotaDirectory);
    return saveQuotaDialog;
  }

  public static void setExportPlanDirectory(File exportPlanDirectory) {
    FileChoosers.exportPlanDirectory = exportPlanDirectory;
  }

  public static void createExportPlanDialog(File currentDirectory) {
    exportPlanDialog = new ExportPlanDialog(currentDirectory);
  }

  public static ExportPlanDialog getExportPlanDialog() {
    if (exportPlanDialog == null) createExportPlanDialog(exportPlanDirectory);
    return exportPlanDialog;
  }
}
