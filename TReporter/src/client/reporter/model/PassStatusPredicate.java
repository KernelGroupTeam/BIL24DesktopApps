package client.reporter.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.PassObj;

public class PassStatusPredicate implements HighlightPredicate {
  @NotNull
  private final PassTableModel passTableModel;
  @NotNull
  private final PassObj.Status status;

  public PassStatusPredicate(@NotNull PassTableModel passTableModel, @NotNull PassObj.Status status) {
    this.passTableModel = passTableModel;
    this.status = status;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return status == passTableModel.getStatus(row);
  }
}
