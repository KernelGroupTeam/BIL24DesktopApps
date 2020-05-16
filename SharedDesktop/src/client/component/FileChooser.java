package client.component;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.03.17
 */
public class FileChooser extends JFileChooser {
  private boolean overwritingConfirmation = true;
  private String addingExtension = null;

  public FileChooser() {
    super();
  }

  public FileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  public FileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  public FileChooser(FileSystemView fsv) {
    super(fsv);
  }

  public FileChooser(File currentDirectory, FileSystemView fsv) {
    super(currentDirectory, fsv);
  }

  public FileChooser(String currentDirectoryPath, FileSystemView fsv) {
    super(currentDirectoryPath, fsv);
  }

  public boolean isOverwritingConfirmation() {
    return overwritingConfirmation;
  }

  public void setOverwritingConfirmation(boolean overwritingConfirmation) {
    this.overwritingConfirmation = overwritingConfirmation;
  }

  public String getAddingExtension() {
    return addingExtension;
  }

  public void setAddingExtension(String addingExtension) {
    this.addingExtension = addingExtension;
  }

  @Override
  public void approveSelection() {
    File file = getSelectedFile();
    if (addingExtension != null && getDialogType() == SAVE_DIALOG) {
      if (!file.getName().toLowerCase().endsWith(addingExtension.toLowerCase())) {
        file = new File(file.getAbsolutePath() + addingExtension);
        setSelectedFile(file);
      }
    }
    if (overwritingConfirmation && getDialogType() == SAVE_DIALOG && file.exists()) {
      if (JOptionPane.showConfirmDialog(this, "Перезаписать файл \"" + file.getName() + "\"?", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
        super.approveSelection();
      }
    } else super.approveSelection();
  }
}
