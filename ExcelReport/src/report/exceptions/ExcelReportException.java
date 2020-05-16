package report.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 21.09.2018
 */
public class ExcelReportException extends Exception {
  public ExcelReportException(@NotNull String message) {
    super(message);
  }

  public ExcelReportException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }

  public ExcelReportException(@NotNull Throwable cause) {
    super(cause);
  }
}
