package report.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 19.11.2017
 */
public final class ValidationException extends ExcelReportException {
  @NotNull
  private static final String TEMPLATE_ABSENT = "Отсутствует значение \"%s\"";
  @NotNull
  private static final String TEMPLATE_INCORRECT = "Некорректное значение \"%s\" у \"%s\"";

  private ValidationException(@NotNull String message) {
    super(message);
  }

  @NotNull
  public static ValidationException absent(@NotNull String field) {
    return new ValidationException(String.format(TEMPLATE_ABSENT, field));
  }

  @NotNull
  public static ValidationException incorrect(@NotNull String value, @NotNull String field) {
    return new ValidationException(String.format(TEMPLATE_INCORRECT, value, field));
  }
}
