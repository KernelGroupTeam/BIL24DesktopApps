package report.forms;

import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import server.protocol2.reporter.RAction;

/**
 * Created by Inventor on 27.11.2017
 * Класс для отчетов формирующихся по накладным
 */
public abstract class AFormQuota<Data> extends AForm<Data> {
  @NotNull
  private final RAction action;

  protected AFormQuota(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign, @Nullable RAction action) throws ValidationException {
    super(form, sheetName, sign);
    if (action == null) throw ValidationException.absent("Представление");
    this.action = action;
  }

  @NotNull
  public final RAction getAction() {
    return action;
  }
}
