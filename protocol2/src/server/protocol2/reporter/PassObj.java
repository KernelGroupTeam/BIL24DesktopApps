package server.protocol2.reporter;

import java.io.Serializable;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.10.15
 */
public class PassObj implements Serializable {
  private static final long serialVersionUID = -3931854751429221776L;
  private static final DateFormat formatDDMMYYYYHHmmss = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private long id;
  @NotNull
  private String time;//в формате дд.мм.гггг чч:мм:сс
  @NotNull
  private RAgent agent;
  @NotNull
  private RFrontend frontend;
  @NotNull
  private ActionEventObj actionEvent;
  @Nullable
  private PassTicketObj ticket;
  @NotNull
  private String description = "";
  @NotNull
  private Status status = Status.STOP;

  public PassObj(long id, @NotNull String time, @NotNull RAgent agent, @NotNull RFrontend frontend, @NotNull ActionEventObj actionEvent) {
    this.id = id;
    this.time = time;
    this.agent = agent;
    this.frontend = frontend;
    this.actionEvent = actionEvent;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getTime() {
    return time;
  }

  @NotNull
  public RAgent getAgent() {
    return agent;
  }

  @NotNull
  public RFrontend getFrontend() {
    return frontend;
  }

  @NotNull
  public ActionEventObj getActionEvent() {
    return actionEvent;
  }

  @Nullable
  public PassTicketObj getTicket() {
    return ticket;
  }

  public void setTicket(@Nullable PassTicketObj ticket) {
    this.ticket = ticket;
  }

  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(@NotNull String description) {
    this.description = description;
  }

  @NotNull
  public Status getStatus() {
    return status;
  }

  public void setStatus(@NotNull Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PassObj)) return false;
    PassObj passObj = (PassObj) o;
    return id == passObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmmss.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmmss.parse(date);
  }

  public enum Status {
    CHECK_IN(1, "Вошел"), CHECK_OUT(2, "Вышел"), STOP(3, "Не пропустили/не выпустили"), CHECK_IN_BY_CONTROLLER(5, "Впустил контроллер");
    private static final Status[] en;

    static {
      en = new Status[]{null, CHECK_IN, CHECK_OUT, STOP, null, CHECK_IN_BY_CONTROLLER};
      for (int i = 0; i < en.length; i++) {
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<Status> enList = Arrays.asList(en);
      for (Status value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    Status(int id, @NotNull String desc) {
      this.id = id;
      this.desc = desc;
    }

    public int getId() {
      return id;
    }

    @NotNull
    public String getDesc() {
      return desc;
    }

    @Override
    public String toString() {
      return desc;
    }

    @NotNull
    public static Status getStatus(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
