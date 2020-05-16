package server.protocol2.manager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.01.18
 */
public class MecObj implements Serializable {
  private static final long serialVersionUID = 3782306323153145170L;
  private long id;
  @NotNull
  private String name;
  private long organizerId;
  @NotNull
  private String organizerName;
  @NotNull
  private List<Discount> cityDiscountList = Collections.emptyList();
  @NotNull
  private List<Discount> actionDiscountList = Collections.emptyList();

  public MecObj(long id, @NotNull String name, long organizerId, @NotNull String organizerName) {
    this.id = id;
    this.name = name;
    this.organizerId = organizerId;
    this.organizerName = organizerName;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public long getOrganizerId() {
    return organizerId;
  }

  @NotNull
  public String getOrganizerName() {
    return organizerName;
  }

  @NotNull
  public List<Discount> getCityDiscountList() {
    return cityDiscountList;
  }

  public void setCityDiscountList(@NotNull List<Discount> cityDiscountList) {
    this.cityDiscountList = cityDiscountList;
  }

  @NotNull
  public List<Discount> getActionDiscountList() {
    return actionDiscountList;
  }

  public void setActionDiscountList(@NotNull List<Discount> actionDiscountList) {
    this.actionDiscountList = actionDiscountList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MecObj)) return false;
    MecObj that = (MecObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "[" + id + "] " + name;
  }

  public static class Discount implements Serializable {
    private long id;
    @NotNull
    private String name;
    @NotNull
    private BigDecimal discountPercent;

    public Discount(long id, @NotNull String name, @NotNull BigDecimal discountPercent) {
      this.id = id;
      this.name = name;
      this.discountPercent = discountPercent;
    }

    public long getId() {
      return id;
    }

    @NotNull
    public String getName() {
      return name;
    }

    @NotNull
    public BigDecimal getDiscountPercent() {
      return discountPercent;
    }
  }
}
