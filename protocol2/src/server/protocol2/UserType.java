package server.protocol2;

import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 04.04.16
 */
public enum UserType {
  OPERATOR(0, "Оператор"), AGENT(1, "Агент"), ORGANIZER(2, "Организатор");
  private static final UserType[] en;

  static {
    en = new UserType[]{OPERATOR, AGENT, ORGANIZER};
    for (int i = 0; i < en.length; i++) {
      if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
    }
    List<UserType> enList = Arrays.asList(en);
    for (UserType value : values()) {
      if (!enList.contains(value)) throw new IllegalStateException("enum table");
    }
  }

  private final int id;
  @NotNull
  private final String desc;

  UserType(int id, @NotNull String desc) {
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
  public static UserType getUserType(int id) {
    if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
    return en[id];
  }
}
