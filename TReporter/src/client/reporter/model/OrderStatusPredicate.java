package client.reporter.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.OrderObj;

public class OrderStatusPredicate implements HighlightPredicate {
  @NotNull
  private final OrderTableModel orderTableModel;
  @NotNull
  private final OrderObj.Status status;

  public OrderStatusPredicate(@NotNull OrderTableModel orderTableModel, @NotNull OrderObj.Status status) {
    this.orderTableModel = orderTableModel;
    this.status = status;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return status == orderTableModel.getOrderStatus(row);
  }
}
