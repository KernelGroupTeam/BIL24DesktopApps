package report.utils;

import java.text.ParseException;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 10.11.2017
 */
public final class Comparators {
  @NotNull
  public static final Comparator<Map.Entry<RAgent, ?>> AGENT_BY_NAME = new Comparator<Map.Entry<RAgent, ?>>() {
    @Override
    public int compare(Map.Entry<RAgent, ?> o1, Map.Entry<RAgent, ?> o2) {
      return o1.getKey().getName().compareTo(o2.getKey().getName());
    }
  };
  @NotNull
  public static final Comparator<Map.Entry<RFrontend, ?>> FRONTEND_BY_NAME = new Comparator<Map.Entry<RFrontend, ?>>() {
    @Override
    public int compare(Map.Entry<RFrontend, ?> o1, Map.Entry<RFrontend, ?> o2) {
      return o1.getKey().getName().compareTo(o2.getKey().getName());
    }
  };
  @NotNull
  public static final Comparator<ActionEventObj> ACTION_EVENT_BY_NAME = new Comparator<ActionEventObj>() {
    @Override
    public int compare(ActionEventObj o1, ActionEventObj o2) {
      return o1.getActionName().compareTo(o2.getActionName());
    }
  };
  @NotNull
  public static final Comparator<Map.Entry<ActionEventObj, ?>> ACTION_EVENT_BY_SHOW_TIME = new Comparator<Map.Entry<ActionEventObj, ?>>() {
    @Override
    public int compare(Map.Entry<ActionEventObj, ?> o1, Map.Entry<ActionEventObj, ?> o2) {
      Date date1;
      try {
        date1 = o1.getKey().getShowTimeDate();
      } catch (ParseException e) {
        date1 = new Date(0);
      }
      Date date2;
      try {
        date2 = o2.getKey().getShowTimeDate();
      } catch (ParseException e) {
        date2 = new Date(0);
      }
      return date1.compareTo(date2);
    }
  };
  @NotNull
  public static final Comparator<String> STRING_BY_SHOW_TIME = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      Date date1;
      try {
        date1 = ActionEventObj.parseFormat(o1);
      } catch (ParseException e) {
        date1 = new Date(0);
      }
      Date date2;
      try {
        date2 = ActionEventObj.parseFormat(o2);
      } catch (ParseException e) {
        date2 = new Date(0);
      }
      return date1.compareTo(date2);
    }
  };
  @NotNull
  public static final Comparator<TicketObj> TICKET_BY_SEAT_LOCATION = new Comparator<TicketObj>() {
    @Override
    public int compare(TicketObj o1, TicketObj o2) {
      SeatLocationObj seatLocation1 = o1.getSeatLocation();
      SeatLocationObj seatLocation2 = o2.getSeatLocation();

      if (seatLocation1 == null && seatLocation2 == null) return 0;
      else if (seatLocation1 == null) return -1;
      else if (seatLocation2 == null) return 1;

      int compare;
      compare = seatLocation1.getSector().compareToIgnoreCase(seatLocation2.getSector());
      if (compare != 0) return compare;

      compare = compareByInteger(seatLocation1.getRow(), seatLocation2.getRow());
      if (compare != 0) return compare;

      compare = compareByInteger(seatLocation1.getNumber(), seatLocation2.getNumber());
      return compare;
    }

    private int compareByInteger(@NotNull String value1, @NotNull String value2) {
      if (StaticMethods.isDigit(value1) && StaticMethods.isDigit(value2)) return Integer.compare(Integer.parseInt(value1), Integer.parseInt(value2));
      else return value1.compareToIgnoreCase(value2);
    }
  };
  @NotNull
  public static final Comparator<String> STRING_AS_INTEGER = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      if (StaticMethods.isDigit(o1) && StaticMethods.isDigit(o2)) return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
      return o1.compareToIgnoreCase(o2);
    }
  };

  private Comparators() {
  }
}
