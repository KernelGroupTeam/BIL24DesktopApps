package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.wraps.*;
import org.jetbrains.annotations.*;
import report.enums.*;
import report.exceptions.ValidationException;
import report.models.*;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 25.11.2017
 */
public final class Form7 extends AFormFilter<List<Form7.VenueInfo>> {
  public Form7(@Nullable String sign, @Nullable Filter filter, @Nullable Boolean charge, @Nullable Boolean discount) throws ValidationException {
    super(EForm.FORM_7, null, sign, filter, charge, discount);
  }

  @Override
  protected void fillHeader() {
    super.fillHeader();
    getHeader().add(EHeader.CHARGE, isCharge()).add(EHeader.DISCOUNT, isDiscount());
  }

  @Override
  protected void fillSheet(@NotNull WrapSheet sheet, @NotNull List<VenueInfo> venueInfoList) {
    if (venueInfoList.isEmpty()) return;

    FormulaSubtotals formulaSubtotalsVenue = new FormulaSubtotals('C', 4);//Итого
    FormulaSubtotals formulaSubtotalsVenues = new FormulaSubtotals('C', 4);//Общий итог

    formulaSubtotalsVenues.setRowStart(sheet.getRowCurrentIndex() + 2);
    for (VenueInfo venueInfo : venueInfoList) {
      sheet.createRow()
          .createCell("Место проведения", EStyle.BOLD)
          .createCell(venueInfo.name, EStyle.BOLD);

      sheet.createRow()
          .setHeightInPoints(47.25f)
          .createCell("Категория билета", EStyle.NORMAL_CENTER_CENTER)
          .createCell("Цена", EStyle.NORMAL_CENTER_CENTER)
          .createCell("Кол-во проданных билетов", EStyle.NORMAL_JUSTIFY_CENTER)
          .createCell("Количество возвращенных билетов", EStyle.NORMAL_JUSTIFY_CENTER)
          .createCell("Кол-во билетов (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_CENTER)
          .createCell("Сумма (проданные минус возвращенные)", EStyle.NORMAL_JUSTIFY_CENTER);

      formulaSubtotalsVenue.reset();
      formulaSubtotalsVenue.setRowStart(sheet.getRowCurrentIndex());
      for (CategoryInfo categoryInfo : venueInfo.categoryInfoList) {
        for (PriceInfo priceInfo : categoryInfo.priceInfoList) {
          sheet.createRow()
              .createCell(categoryInfo.name, EStyle.NORMAL_242)
              .createCell(priceInfo.price, EStyle.NORMAL_242_MONEY)
              .createCell(priceInfo.countTicketSold, EStyle.NORMAL_242)
              .createCell(priceInfo.countTicketRefund, EStyle.NORMAL_242)
              .createCell(priceInfo.countTicketTotal, EStyle.NORMAL_242)
              .createCell(priceInfo.sumTicketTotal, EStyle.NORMAL_242_MONEY);
        }
      }
      formulaSubtotalsVenue.setRowEnd(sheet.getRowCurrentIndex() - 1);

      WrapRow row = sheet.createRow()
          .createCell("Итого", EStyle.BOLD_216)
          .createCell(EStyle.BOLD_216);
      while (formulaSubtotalsVenue.hasNext()) row.createCell(formulaSubtotalsVenue.nextFormula(), formulaSubtotalsVenue.isLastAfterNext() ? EStyle.BOLD_216_MONEY : EStyle.BOLD_216);

      sheet.incRowCurrentIndex();
    }
    formulaSubtotalsVenues.setRowEnd(sheet.getRowCurrentIndex() - 2);

    WrapRow row = sheet.createRow()
        .createCell("Общий итог", EStyle.BOLD_BLUE)
        .createCell(EStyle.BOLD_BLUE);
    while (formulaSubtotalsVenues.hasNext()) row.createCell(formulaSubtotalsVenues.nextFormula(), formulaSubtotalsVenues.isLastAfterNext() ? EStyle.BOLD_BLUE_MONEY : EStyle.BOLD_BLUE);

    sheet.setColumnWidth(0, 8900);
    sheet.setColumnWidth(1, 4300);
    sheet.setColumnWidth(2, 4500);
    sheet.setColumnWidth(3, 4100);
    sheet.setColumnWidth(4, 4700);
    sheet.setColumnWidth(5, 5200);
  }

  @NotNull
  public Form7 generateData(@Nullable List<OrderObj> orderList) throws ValidationException {
    if (orderList == null) throw ValidationException.absent("Список заказов");

    Map<String, Map<String, Map<BigDecimal, List<TicketObj>>>> venueMap = new TreeMap<>();
    for (OrderObj order : orderList) {
      for (TicketObj ticket : order.getTicketList()) {
        Map<String, Map<BigDecimal, List<TicketObj>>> categoryMap = venueMap.get(ticket.getActionEvent().getVenueName());
        if (categoryMap == null) venueMap.put(ticket.getActionEvent().getVenueName(), categoryMap = new TreeMap<>());
        Map<BigDecimal, List<TicketObj>> priceMap = categoryMap.get(ticket.getCategory());
        if (priceMap == null) categoryMap.put(ticket.getCategory(), priceMap = new TreeMap<>());
        BigDecimal price = getPrice(ticket);
        List<TicketObj> ticketList = priceMap.get(price);
        if (ticketList == null) priceMap.put(price, ticketList = new ArrayList<>());
        ticketList.add(ticket);
      }
    }

    List<VenueInfo> result = new ArrayList<>();
    for (Map.Entry<String, Map<String, Map<BigDecimal, List<TicketObj>>>> venue : venueMap.entrySet()) {
      List<CategoryInfo> categoryInfoList = new ArrayList<>();
      for (Map.Entry<String, Map<BigDecimal, List<TicketObj>>> category : venue.getValue().entrySet()) {
        List<PriceInfo> priceInfoList = new ArrayList<>();
        for (Map.Entry<BigDecimal, List<TicketObj>> price : category.getValue().entrySet()) {
          int countTicketSold = 0;
          int countTicketRefund = 0;
          BigDecimal sumTicketSold = BigDecimal.ZERO;
          BigDecimal sumTicketRefund = BigDecimal.ZERO;

          for (TicketObj ticket : price.getValue()) {
            countTicketSold++;
            sumTicketSold = sumTicketSold.add(price.getKey());

            if (ticket.getHolderStatus() == TicketObj.HolderStatus.REFUND) {
              countTicketRefund++;
              sumTicketRefund = sumTicketRefund.add(price.getKey());
            }
          }

          priceInfoList.add(new PriceInfo(price.getKey(), countTicketSold, countTicketRefund, sumTicketSold, sumTicketRefund));
        }
        categoryInfoList.add(new CategoryInfo(category.getKey(), priceInfoList));
      }
      result.add(new VenueInfo(venue.getKey(), categoryInfoList));
    }
    setData(result);

    return this;
  }

  protected static final class VenueInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<CategoryInfo> categoryInfoList;

    private VenueInfo(@NotNull String name, @NotNull List<CategoryInfo> categoryInfoList) {
      this.name = name;
      this.categoryInfoList = categoryInfoList;
    }
  }

  private static final class CategoryInfo {
    @NotNull
    private final String name;
    @NotNull
    private final List<PriceInfo> priceInfoList;

    private CategoryInfo(@NotNull String name, @NotNull List<PriceInfo> priceInfoList) {
      this.name = name;
      this.priceInfoList = priceInfoList;
    }
  }

  private static final class PriceInfo {
    @NotNull
    private final BigDecimal price;
    private final int countTicketSold;
    private final int countTicketRefund;
    private final int countTicketTotal;
    @NotNull
    private final BigDecimal sumTicketTotal;

    private PriceInfo(@NotNull BigDecimal price, int countTicketSold, int countTicketRefund, @NotNull BigDecimal sumTicketSold, @NotNull BigDecimal sumTicketRefund) {
      this.price = price;
      this.countTicketSold = countTicketSold;
      this.countTicketRefund = countTicketRefund;
      this.countTicketTotal = countTicketSold - countTicketRefund;
      this.sumTicketTotal = sumTicketSold.subtract(sumTicketRefund);
    }
  }
}
