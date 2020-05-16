package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 26.11.2017
 */
public final class Form9 extends AFormQuota<ReportSaleInfo> {
  @Nullable
  private final Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap;

  public Form9(@Nullable String sign, @Nullable RAction action, @Nullable Map<Integer, Map<Long, Set<String>>> actionEventQuotaMap) throws ValidationException {
    super(EForm.FORM_9, null, sign, action);
    this.actionEventQuotaMap = actionEventQuotaMap;
  }

  @Nullable
  public Map<Integer, Map<Long, Set<String>>> getActionEventQuotaMap() {
    return actionEventQuotaMap;
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader()
        .add(EHeader.CITY, getData().getCityNames())
        .add(EHeader.VENUE, getData().getVenueNames())
        .add(EHeader.ACTION, getData().getActionName())
        .add(EHeader.ACTION_LEGAL_OWNER, getData().getActionLegalOwner());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull ReportSaleInfo reportSaleInfo) {
    FormulaSubtotals formulaSubtotalsActionEvent = new FormulaSubtotals('B', 18);//Итого
    FormulaSubtotals formulaSubtotalsAction = new FormulaSubtotals('B', 18);//Итого по представлению

    formulaSubtotalsAction.reset();
    formulaSubtotalsAction.setRowStart(sheet.getRowCurrentIndex() + 5);
    for (ReportSaleInfo.EventInfo eventInfo : reportSaleInfo.getEventInfoList()) {
      sheet.createRow()
          .createCell("Город проведения", EStyle.BOLD)
          .createCell(eventInfo.getCityName(), EStyle.BOLD);

      sheet.createRow()
          .createCell("Место проведения", EStyle.BOLD)
          .createCell(eventInfo.getVenueName(), EStyle.BOLD);

      sheet.createRow()
          .createCell("Дата проведения", EStyle.BOLD)
          .createCell(eventInfo.getShowTime(), EStyle.BOLD);

      sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex() + 1, 0, 0);
      for (int firstCol = 1, lastCol = 2; firstCol <= 17; firstCol += 2, lastCol += 2)
        sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), firstCol, lastCol);

      sheet.createRow()
          .setHeightInPoints(30)
          .createCell("Ценовая категория\n(руб.)", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Получено мест\n(квота)", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Возвращено мест\n(квота)", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Забронировано мест", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Продано мест", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Свободно мест", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Продано билетов\n(номинал)", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Скидка на билеты", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Возвращено билетов", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell("Итого оплаченных\nбилетов", EStyle.NORMAL_CENTER_CENTER_WRAP)
          .createCell(EStyle.NORMAL_CENTER_CENTER_WRAP);

      WrapRow row = sheet.createRow().createCell(EStyle.NORMAL_CENTER_CENTER);
      for (int i = 0; i < 5; i++) row.createCell("Мест", EStyle.NORMAL_CENTER_CENTER).createCell("На сумму", EStyle.NORMAL_CENTER_CENTER);
      for (int i = 5; i < 9; i++) row.createCell("Билетов", EStyle.NORMAL_CENTER_CENTER).createCell("На сумму", EStyle.NORMAL_CENTER_CENTER);

      formulaSubtotalsActionEvent.reset();
      formulaSubtotalsActionEvent.setRowStart(sheet.getRowCurrentIndex());
      for (Map.Entry<BigDecimal, ReportSaleInfo.Info> category : eventInfo.getCategoryInfoMap().entrySet()) {
        ReportSaleInfo.Info info = category.getValue();

        sheet.createRow()
            .createCell(category.getKey(), EStyle.NORMAL_242)
            .createCell(info.getQuotaInCount(), EStyle.NORMAL_242)
            .createCell(info.getQuotaInSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getQuotaOutCount(), EStyle.NORMAL_242)
            .createCell(info.getQuotaOutSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getQuotaReservedCount(), EStyle.NORMAL_242)
            .createCell(info.getQuotaReservedSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getQuotaOccupiedCount(), EStyle.NORMAL_242)
            .createCell(info.getQuotaOccupiedSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getQuotaAvailableCount(), EStyle.NORMAL_242)
            .createCell(info.getQuotaAvailableSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getTicketCount(), EStyle.NORMAL_242)
            .createCell(info.getTicketSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getTicketDiscountCount(), EStyle.NORMAL_242)
            .createCell(info.getTicketDiscountSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getTicketRefundCount(), EStyle.NORMAL_242)
            .createCell(info.getTicketRefundSum(), EStyle.NORMAL_242_COUNT)
            .createCell(info.getTicketOccupiedCount(), EStyle.NORMAL_242)
            .createCell(info.getTicketOccupiedSum(), EStyle.NORMAL_242_COUNT);
      }
      formulaSubtotalsActionEvent.setRowEnd(sheet.getRowCurrentIndex() - 1);

      row = sheet.createRow().createCell("Итого", EStyle.NORMAL_216);
      while (formulaSubtotalsActionEvent.hasNext()) {
        row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.NORMAL_216);
        //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
        row.createCell(formulaSubtotalsActionEvent.nextFormula(), EStyle.NORMAL_216_COUNT);
      }

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsAction.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow().createCell("Итого по представлению", EStyle.BOLD_191);
    while (formulaSubtotalsAction.hasNext()) {
      row.createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_191);
      //Не делаю проверку на hasNext, т.к. кол-во формул четное ошибки быть не должно
      row.createCell(formulaSubtotalsAction.nextFormula(), EStyle.BOLD_191_COUNT);
    }

    sheet.setColumnWidth(0, 6000);
    for (int i1 = 1, i2 = 2; i2 <= 16; i1 += 2, i2 += 2) {
      sheet.setColumnWidth(i1, 2500);
      sheet.setColumnWidth(i2, 3500);
    }
  }

  @NotNull
  public Form9 generateData(@Nullable ReportSaleInfo reportSaleInfo) throws ValidationException {
    if (reportSaleInfo == null) throw ValidationException.absent("Информация по продажам");
    setData(reportSaleInfo);
    return this;
  }
}
