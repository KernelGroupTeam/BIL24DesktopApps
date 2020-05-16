package client.editor.model;

import java.awt.*;
import javax.swing.*;

import org.jdesktop.swingx.decorator.*;
import org.jetbrains.annotations.*;
import server.protocol2.editor.EventSeatObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.11.16
 */
public class EventSeatStatePredicate implements HighlightPredicate {
  @NotNull
  private final EventSeatTableModel eventSeatTableModel;
  @NotNull
  private final EventSeatObj.State state;
  @Nullable
  private final Boolean categoryAndPriceOnly;

  public EventSeatStatePredicate(@NotNull EventSeatTableModel eventSeatTableModel, @NotNull EventSeatObj.State state) {
    this(eventSeatTableModel, state, null);
  }

  public EventSeatStatePredicate(@NotNull EventSeatTableModel eventSeatTableModel, @NotNull EventSeatObj.State state, @Nullable Boolean categoryAndPriceOnly) {
    this.eventSeatTableModel = eventSeatTableModel;
    this.state = state;
    this.categoryAndPriceOnly = categoryAndPriceOnly;
  }

  @SuppressWarnings({"RedundantIfStatement"})
  @Override
  public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
    JTable table = (JTable) adapter.getComponent();
    int row = table.convertRowIndexToModel(adapter.row);
    EventSeatObj.State modelState = eventSeatTableModel.getWithEbsState(row);
    boolean result = (state == modelState);
    if (!result) return false;
    if (categoryAndPriceOnly != null) {
      int column = table.convertColumnIndexToModel(adapter.column);
      if (categoryAndPriceOnly && (column == 4 || column == 5)) return true;
      else if (!categoryAndPriceOnly && column != 4 && column != 5) return true;
      else return false;
    }
    return true;
  }
}
