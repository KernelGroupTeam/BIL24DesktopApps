package server.protocol2.reporter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.03.17
 */
public class QuotaDataObj implements Serializable {
  private static final long serialVersionUID = -2496881517620901044L;
  @NotNull
  private QuotaEvent quotaEvent;
  @NotNull
  private Type type;
  @NotNull
  private String number;
  @NotNull
  private String date;//в формате дд.мм.гггг
  @NotNull
  private List<QuotaSeat> quotaSeatList;
  private int totalQty;
  @NotNull
  private BigDecimal totalSum;

  public QuotaDataObj(@NotNull QuotaEvent quotaEvent, @NotNull Type type, @NotNull String number, @NotNull String date,
                      @NotNull List<QuotaSeat> quotaSeatList, int totalQty, @NotNull BigDecimal totalSum) {
    this.quotaEvent = quotaEvent;
    this.type = type;
    this.number = number;
    this.date = date;
    this.quotaSeatList = quotaSeatList;
    this.totalQty = totalQty;
    this.totalSum = totalSum;
  }

  @NotNull
  public QuotaEvent getQuotaEvent() {
    return quotaEvent;
  }

  @NotNull
  public Type getType() {
    return type;
  }

  @NotNull
  public String getNumber() {
    return number;
  }

  @NotNull
  public String getDate() {
    return date;
  }

  @NotNull
  public List<QuotaSeat> getQuotaSeatList() {
    return quotaSeatList;
  }

  public int getTotalQty() {
    return totalQty;
  }

  @NotNull
  public BigDecimal getTotalSum() {
    return totalSum;
  }

  public enum Type {
    IN, OUT
  }
}
