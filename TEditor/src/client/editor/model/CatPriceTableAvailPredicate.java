package client.editor.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;

public class CatPriceTableAvailPredicate implements HighlightPredicate {
  @NotNull
  private final CatPriceTableModel catPriceTableModel;

  public CatPriceTableAvailPredicate(@NotNull CatPriceTableModel catPriceTableModel) {
    this.catPriceTableModel = catPriceTableModel;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return (catPriceTableModel.getAvailability(row) <= 0);
  }
}
