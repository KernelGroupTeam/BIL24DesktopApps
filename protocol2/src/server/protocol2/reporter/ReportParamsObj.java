package server.protocol2.reporter;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 26.01.2018.
 */
public class ReportParamsObj implements Serializable {
  private static final long serialVersionUID = -1850722301099807252L;
  private long id;
  private int formId;
  private int reportPeriod;
  @NotNull
  private String name = "";
  @NotNull
  private List<String> emailList = Collections.emptyList();
  @NotNull
  private String startDate = "";//в формате дд.мм.гггг чч:мм
  @NotNull
  private String endDate = "";//в формате дд.мм.гггг чч:мм
  private boolean allowed;
  private boolean deficient;
  private boolean expired;

  public ReportParamsObj(long id, int formId) {
    this.id = id;
    this.formId = formId;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {//для упрощения, чтобы не клонировать объект
    this.id = id;
  }

  public int getFormId() {
    return formId;
  }

  public int getReportPeriod() {
    return reportPeriod;
  }

  public void setReportPeriod(int reportPeriod) {
    this.reportPeriod = reportPeriod;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public List<String> getEmailList() {
    return emailList;
  }

  public void setEmailList(@NotNull List<String> emailList) {
    this.emailList = emailList;
  }

  @NotNull
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(@NotNull String startDate) {
    this.startDate = startDate;
  }

  @NotNull
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(@NotNull String endDate) {
    this.endDate = endDate;
  }

  public boolean isAllowed() {
    return allowed;
  }

  public void setAllowed(boolean allowed) {
    this.allowed = allowed;
  }

  public boolean isDeficient() {
    return deficient;
  }

  public void setDeficient(boolean deficient) {
    this.deficient = deficient;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }
}
