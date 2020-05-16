package server.protocol2.manager;

import java.io.Serializable;
import java.text.*;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.03.16
 */
public class NewsObj implements Filterable, Serializable {
  private static final long serialVersionUID = 7079671224676852233L;
  private static final DateFormat formatDDMMYYYYHHmm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  private long id;
  @NotNull
  private String showTime = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private String header = "";
  @NotNull
  private String briefDescription = "";
  @NotNull
  private String fullDescription = "";
  @NotNull
  private Destination destination = Destination.ALL;
  @NotNull
  private List<String> destinationList = Collections.emptyList();
  private boolean show = true;
  private boolean needPush = false;

  public NewsObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getShowTime() {
    return showTime;
  }

  public void setShowTime(@NotNull String showTime) {
    this.showTime = showTime;
  }

  @NotNull
  public String getHeader() {
    return header;
  }

  public void setHeader(@NotNull String header) {
    this.header = header;
  }

  @NotNull
  public String getBriefDescription() {
    return briefDescription;
  }

  public void setBriefDescription(@NotNull String briefDescription) {
    this.briefDescription = briefDescription;
  }

  @NotNull
  public String getFullDescription() {
    return fullDescription;
  }

  public void setFullDescription(@NotNull String fullDescription) {
    this.fullDescription = fullDescription;
  }

  @NotNull
  public Destination getDestination() {
    return destination;
  }

  public void setDestination(@NotNull Destination destination) {
    this.destination = destination;
  }

  @NotNull
  public List<String> getDestinationList() {
    return destinationList;
  }

  public void setDestinationList(@NotNull List<String> destinationList) {
    this.destinationList = destinationList;
  }

  public boolean isShow() {
    return show;
  }

  public void setShow(boolean show) {
    this.show = show;
  }

  public boolean isNeedPush() {
    return needPush;
  }

  public void setNeedPush(boolean needPush) {
    this.needPush = needPush;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NewsObj)) return false;
    NewsObj newsObj = (NewsObj) o;
    return id == newsObj.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return showTime + " " + header;
  }

  public static synchronized boolean checkFormat(String date) {
    try {
      formatDDMMYYYYHHmm.parse(date);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @NotNull
  public static synchronized Date parseFormat(@NotNull String date) throws ParseException {
    return formatDDMMYYYYHHmm.parse(date);
  }

  public enum Destination {
    ALL, AGENT, FRONTEND
  }
}
