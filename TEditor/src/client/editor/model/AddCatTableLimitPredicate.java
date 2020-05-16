package client.editor.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;

public class AddCatTableLimitPredicate implements HighlightPredicate {
  @NotNull
  private final AddCatTableModel addCatTableModel;

  public AddCatTableLimitPredicate(@NotNull AddCatTableModel addCatTableModel) {
    this.addCatTableModel = addCatTableModel;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return addCatTableModel.isRowLimit(row);
  }
}
