package report.enums;

import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 13.11.2017
 */
public enum EForm {
  FORM_1(Type.FILTER, 1, "Форма №1. Отчет по представлению."),
  FORM_2(Type.FILTER, 2, "Форма №2. Возвращенные билеты."),
  FORM_3(Type.FILTER, 3, "Форма №3. Отчет по всем агентам."),
  FORM_4(Type.FILTER, 4, "Форма №4. Продажи на сеансы."),
  FORM_5(Type.FILTER, 5, "Форма №5. Полный отчет по мероприятию."),
  FORM_6(Type.FILTER, 6, "Форма №6. Отчет по площадке."),
  FORM_7(Type.FILTER, 7, "Форма №7. Отчет по категориям."),
  FORM_8(Type.FILTER, 8, "Форма №8. Общий отчет по продажам."),
  FORM_9(Type.QUOTA_SALE, 9, "Форма №9. Отчет по продажам."),
  FORM_10(Type.FILTER, 10, "Форма №10. Продажи на сеансы по агентам."),
  FORM_11(Type.FILTER, 11, "Форма №11. Билеты."),
  FORM_12(Type.CASHIER_WORK_SHIFT, 12, "Форма №12. Отчет по кассовым сменам."),
  FORM_13(Type.FILTER, 13, "Форма №13. Техносервис."),
  FORM_14(Type.QUOTA, 14, "Форма №14. Продажи на сеансы."),
  FORM_15(Type.QUOTA, 15, "Форма №15. Полный отчет по мероприятию."),
  FORM_16(Type.FILTER, 16, "Форма №16. Выгрузка штрихкодов."),
  FORM_17(Type.FILTER, 17, "Форма №17. Продажи и возвраты за период."),
  FORM_18(Type.FILTER, 18, "Форма №18. Отчет по представлению."),
  FORM_19(Type.FILTER, 19, "Форма №19. Продажи на сеансы."),
  FORM_20(Type.FILTER, 20, "Форма №20. Акт оказанных услуг."),
  FORM_21(Type.FILTER, 21, "Форма №21. Отчёт по продажам билетов."),
  FORM_22(Type.FILTER, 22, "Форма №22. Отчет по всем агентам."),
  FORM_23(Type.FILTER, 23, "Форма №23. Отчет билеты на сеансы."),
  INVOICE_IN(Type.INVOICE, 51, "Приходная накладная"),
  INVOICE_OUT(Type.INVOICE, 52, "Накладная на возврат");
  private static final EForm[] en;

  static {
    en = new EForm[]{null, FORM_1, FORM_2, FORM_3, FORM_4, FORM_5, FORM_6, FORM_7, FORM_8, FORM_9,
        FORM_10, FORM_11, FORM_12, FORM_13, FORM_14, FORM_15, FORM_16, FORM_17, FORM_18, FORM_19,
        FORM_20, FORM_21, FORM_22, FORM_23, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null,
        null, INVOICE_IN, INVOICE_OUT};
    for (int i = 0; i < en.length; i++) {
      if (en[i] != null && en[i].getId() != i) throw new IllegalStateException("enum table");
    }
    List<EForm> enList = Arrays.asList(en);
    for (EForm value : values()) {
      if (!enList.contains(value)) throw new IllegalStateException("enum table");
    }
  }

  @NotNull
  private final Type type;
  private final int id;
  @NotNull
  private final String name;

  EForm(@NotNull Type type, int id, @NotNull String name) {
    this.type = type;
    this.id = id;
    this.name = name;
  }

  @NotNull
  public Type getType() {
    return type;
  }

  public int getId() {
    return id;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public static EForm getForm(int id) {
    if (id < 0 || id >= en.length || en[id] == null) throw new IllegalArgumentException("enum consistency error");
    return en[id];
  }

  public enum Type {
    FILTER,//Отчеты по фильтру
    QUOTA_SALE,//Отчет по накладным форма 9
    QUOTA,//Отчеты по накладным
    INVOICE,//Накладные
    CASHIER_WORK_SHIFT//Отчеты по сменам кассира
  }
}
