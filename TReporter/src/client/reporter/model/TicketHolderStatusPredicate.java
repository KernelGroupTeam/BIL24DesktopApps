package client.reporter.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.TicketObj;

public class TicketHolderStatusPredicate implements HighlightPredicate {
  @NotNull
  private final TicketTableModel ticketTableModel;
  @NotNull
  private final TicketObj.HolderStatus holderStatus;

  public TicketHolderStatusPredicate(@NotNull TicketTableModel ticketTableModel, @NotNull TicketObj.HolderStatus holderStatus) {
    this.ticketTableModel = ticketTableModel;
    this.holderStatus = holderStatus;
  }

  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    return holderStatus == ticketTableModel.getHolderStatus(row);
  }
}
