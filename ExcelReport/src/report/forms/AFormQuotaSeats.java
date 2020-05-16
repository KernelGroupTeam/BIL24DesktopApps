package report.forms;

import java.util.Set;

import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import server.protocol2.reporter.RAction;

/**
 * Created by Inventor on 27.01.2018
 */
public abstract class AFormQuotaSeats<Data> extends AFormQuota<Data> {
  @NotNull
  private final Set<Long> allowedSeatIdSet;

  protected AFormQuotaSeats(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign, @Nullable RAction action, @Nullable Set<Long> allowedSeatIdSet) throws ValidationException {
    super(form, sheetName, sign, action);
    if (allowedSeatIdSet == null) throw ValidationException.absent("Множество нужных мест из накладных");
    this.allowedSeatIdSet = allowedSeatIdSet;
  }

  @NotNull
  public final Set<Long> getAllowedSeatIdSet() {
    return allowedSeatIdSet;
  }

  @Override
  protected final void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.ACTION, getAction().getName());
  }
}
