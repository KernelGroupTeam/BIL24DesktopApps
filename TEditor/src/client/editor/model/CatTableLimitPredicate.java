package client.editor.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;

public class CatTableLimitPredicate implements HighlightPredicate {
  @NotNull
  private final CatTableModel catTableModel;

  public CatTableLimitPredicate(@NotNull CatTableModel catTableModel) {
    this.catTableModel = catTableModel;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return catTableModel.isRowLimit(row);
  }
}
