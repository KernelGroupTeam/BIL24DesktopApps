package server.protocol2.reporter;

import java.io.Serializable;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 07.09.2018
 */
public class RCashierWorkShift implements Serializable {
  private static final long serialVersionUID = 5091516282565726289L;
  private static final DateFormat formatDDMMYYYYHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private long id;
  @NotNull
  private RAgent agent;
  @NotNull
  private List<RFrontend> frontendList;
  @Nullable
  private String kktSerialNumber;
  @NotNull
  private String startDate;
  @NotNull
  private String endDate;

  public RCashierWorkShift(long id, @NotNull RAgent agent, @NotNull List<RFrontend> frontendList, @Nullable String kktSerialNumber, @NotNull String startDate, @NotNull String endDate) {
    this.id = id;
    this.agent = agent;
    this.frontendList = frontendList;
    this.kktSerialNumber = kktSerialNumber;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public RAgent getAgent() {
    return agent;
  }

  @NotNull
  public List<RFrontend> getFrontendList() {
    return frontendList;
  }

  @Nullable
  public String getKktSerialNumber() {
    return kktSerialNumber;
  }

  @NotNull
  public String getStartDate() {
    return startDate;
  }

  @NotNull
  public String getEndDate() {
    return endDate;
  }

  @NotNull
  public static synchronized Date parseDate(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmmss.parse(date);
  }
}
