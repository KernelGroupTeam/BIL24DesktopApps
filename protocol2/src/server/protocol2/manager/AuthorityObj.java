package server.protocol2.manager;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.*;
import server.protocol2.Filterable;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.04.16
 */
public abstract class AuthorityObj implements Filterable, Serializable {
  private static final long serialVersionUID = 2225446039873525096L;
  private long id;
  @NotNull
  private AuthTypeObj type;
  @NotNull
  private Entity entity;
  @NotNull
  private String name = "";
  @NotNull
  private String address = "";
  @NotNull
  private String inn = "";
  @NotNull
  private String ogrn = "";
  @NotNull
  private String currentAcc = "";//расчетный счет
  @NotNull
  private String bankName = "";
  @NotNull
  private String corrAcc = "";
  @NotNull
  private String bic = "";//БИК
  @NotNull
  private String web = "";
  private boolean disabled = false;

  public AuthorityObj(long id, @NotNull AuthTypeObj type, @NotNull Entity entity) {
    this.id = id;
    this.type = type;
    this.entity = entity;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public AuthTypeObj getType() {
    return type;
  }

  @NotNull
  public Entity getEntity() {
    return entity;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getAddress() {
    return address;
  }

  public void setAddress(@NotNull String address) {
    this.address = address;
  }

  @NotNull
  public String getInn() {
    return inn;
  }

  public void setInn(@NotNull String inn) {
    this.inn = inn;
  }

  @NotNull
  public String getOgrn() {
    return ogrn;
  }

  public void setOgrn(@NotNull String ogrn) {
    this.ogrn = ogrn;
  }

  @NotNull
  public String getCurrentAcc() {
    return currentAcc;
  }

  public void setCurrentAcc(@NotNull String currentAcc) {
    this.currentAcc = currentAcc;
  }

  @NotNull
  public String getBankName() {
    return bankName;
  }

  public void setBankName(@NotNull String bankName) {
    this.bankName = bankName;
  }

  @NotNull
  public String getCorrAcc() {
    return corrAcc;
  }

  public void setCorrAcc(@NotNull String corrAcc) {
    this.corrAcc = corrAcc;
  }

  @NotNull
  public String getBic() {
    return bic;
  }

  public void setBic(@NotNull String bic) {
    this.bic = bic;
  }

  @NotNull
  public String getWeb() {
    return web;
  }

  public void setWeb(@NotNull String web) {
    this.web = web;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  @Override
  public boolean pass(@Nullable Object filter) {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AuthorityObj)) return false;
    AuthorityObj that = (AuthorityObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return name + (disabled ? " [Отключен]" : "");
  }

  public enum Entity {
    NATURAL(0, "Физическое лицо"), LEGAL(1, "Юридическое лицо");
    private static final Entity[] en;

    static {
      en = new Entity[]{NATURAL, LEGAL};
      for (int i = 0; i < en.length; i++) {
        //noinspection ConstantConditions
        if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
      }
      List<Entity> enList = Arrays.asList(en);
      for (Entity value : values()) {
        if (!enList.contains(value)) throw new IllegalStateException("enum table");
      }
    }

    private final int id;
    @NotNull
    private final String desc;

    Entity(int id, @NotNull String desc) {
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
    public static Entity getEntity(int id) {
      if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
      return en[id];
    }
  }
}
