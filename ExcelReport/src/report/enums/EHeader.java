package report.enums;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 27.11.2017
 */
public enum EHeader {
  NAME(""),
  PERIOD("Период отчета: "),
  ACQUIRING("Эквайринг: "),
  ORGANIZER("Организатор: "),
  CITY("Город: "),
  VENUE("Место: "),
  ACTION("Представление: "),
  ACTION_EVENT("Сеанс: "),
  AGENT("Агент: "),
  FRONTEND("Интерфейс: "),
  SYSTEM("Шлюз в ВБС: "),
  GATEWAY("Подключение к ВБС: "),
  FULL_REPORT("Все продажи: "),
  ALL_STATUSES("Все статусы: "),
  PERIOD_TYPE("Период: "),
  CHARGE("С учетом сервисного сбора: "),
  DISCOUNT("С учетом скидки: "),
  ACTION_LEGAL_OWNER("Поставщик: "),
  INVOICE_DATE("Накладная от: "),
  INVOICE_NUMBER("Накладная №"),
  TRANSFER_TO("Передал поставщик: "),
  TRANSFER_FROM("Получила орг-ция: "),
  ADDRESS("Адрес: "),
  NUMBER("№ "),
  CREATED("Создан: "),
  OPERATOR("Оператор: "),
  ;

  @NotNull
  private final String desc;

  EHeader(@NotNull String desc) {
    this.desc = desc;
  }

  @NotNull
  public String getDesc() {
    return desc;
  }
}
