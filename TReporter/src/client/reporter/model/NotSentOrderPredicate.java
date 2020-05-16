package client.reporter.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;

public class NotSentOrderPredicate implements HighlightPredicate {
  @NotNull
  private final OrderTableModel orderTableModel;

  public NotSentOrderPredicate(@NotNull OrderTableModel orderTableModel) {
    this.orderTableModel = orderTableModel;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return orderTableModel.isNotSent(row);
  }
}
