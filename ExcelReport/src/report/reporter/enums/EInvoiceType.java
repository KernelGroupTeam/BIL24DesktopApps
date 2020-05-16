package report.reporter.enums;

import org.jetbrains.annotations.*;
import report.enums.EForm;

/**
 * Created by Inventor on 27.01.2018
 */
public enum EInvoiceType {
  IN(EForm.INVOICE_IN),//Приходная
  OUT(EForm.INVOICE_OUT),//На возврат
  ;

  private final int id;

  EInvoiceType(@NotNull EForm form) {
    this.id = form.getId();
  }

  public int getId() {
    return id;
  }

  @Nullable
  public static EInvoiceType getInvoiceTypeById(int id) {
    for (EInvoiceType invoiceType : EInvoiceType.values()) {
      if (invoiceType.getId() == id) return invoiceType;
    }
    return null;
  }
}
