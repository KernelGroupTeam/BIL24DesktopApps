package report.forms;

import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import server.protocol2.reporter.QuotaDataObj;

/**
 * Created by Inventor on 28.11.2017
 */
public final class FormInvoiceIn extends AFormInvoice {
  public FormInvoiceIn(@Nullable String sign, @Nullable QuotaDataObj quotaData) throws ValidationException {
    super(EForm.INVOICE_IN, null, sign, quotaData);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader()
        .add(EHeader.INVOICE_DATE, getQuotaData().getDate())
        .add(EHeader.INVOICE_NUMBER, getQuotaData().getNumber())
        .add(EHeader.TRANSFER_TO, getQuotaData().getQuotaEvent().getActionLegalOwner())
        .add(EHeader.TRANSFER_FROM, getQuotaData().getQuotaEvent().getOrganizerName())
        .add(EHeader.ACTION, getQuotaData().getQuotaEvent().getActionName())
        .add(EHeader.ACTION_EVENT, getQuotaData().getQuotaEvent().getShowTime())
        .add(EHeader.VENUE, getQuotaData().getQuotaEvent().getVenueName())
        .add(EHeader.ADDRESS, getQuotaData().getQuotaEvent().getVenueAddress());
  }
}
