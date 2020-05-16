package report.forms;

import java.math.BigDecimal;
import java.util.*;

import excel.enums.EStyle;
import excel.formulas.FormulaSubtotals;
import excel.numerals.Russian;
import excel.wraps.WrapSheet;
import org.jetbrains.annotations.*;
import report.enums.EForm;
import report.exceptions.ValidationException;
import report.utils.Comparators;
import server.protocol2.reporter.*;

/**
 * Created by Inventor on 27.11.2017
 * Класс для накладных
 */
public abstract class AFormInvoice extends AForm<AFormInvoice.TotalInfo> {
  @NotNull
  private final QuotaDataObj quotaData;

  protected AFormInvoice(@NotNull EForm form, @Nullable String sheetName, @Nullable String sign, @Nullable QuotaDataObj quotaData) throws ValidationException {
    super(form, sheetName, sign);
    if (quotaData == null) throw ValidationException.absent("Информация по квоте");
    this.quotaData = quotaData;
  }

  @NotNull
  public final QuotaDataObj getQuotaData() {
    return quotaData;
  }

  @Override
  protected final void fillSheet(@NotNull WrapSheet sheet, @NotNull TotalInfo totalInfo) {
    if (totalInfo.seatInfoList.isEmpty() && totalInfo.categoryInfoList.isEmpty()) return;

    sheet.createRow()
        .setHeightInPoints(40)
        .createCell("Наименование сектора", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Ряд", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Места", EStyle.NORMAL_CENTER_CENTER)
        .createCell("Количество\nмест", EStyle.NORMAL_CENTER_CENTER_WRAP)
        .createCell("Цена места\nруб.)", EStyle.NORMAL_CENTER_CENTER_WRAP)
        .createCell("Сумма\n(руб.)", EStyle.NORMAL_CENTER_CENTER_WRAP);

    FormulaSubtotals formulaSubtotals = new FormulaSubtotals(new char[]{'D', 'F'});//Итого
    formulaSubtotals.setRowStart(sheet.getRowCurrentIndex());
    for (SeatInfo seatInfo : totalInfo.seatInfoList) {
      sheet.createRow()
          .createCell(seatInfo.sector, EStyle.NORMAL_242_LEFT_CENTER)
          .createCell(seatInfo.row, EStyle.NORMAL_242_CENTER_CENTER)
          .createCell(seatInfo.seats, EStyle.NORMAL_242_JUSTIFY_CENTER)
          .createCell(seatInfo.countSeat, EStyle.NORMAL_242_CENTER_CENTER)
          .createCell(seatInfo.price, EStyle.NORMAL_242_RIGHT_CENTER_MONEY)
          .createCell(seatInfo.sumSeat, EStyle.NORMAL_242_RIGHT_CENTER_MONEY);
    }
    for (CategoryInfo categoryInfo : totalInfo.categoryInfoList) {
      sheet.createRow()
          .createCell(categoryInfo.category, EStyle.NORMAL_242_LEFT_CENTER)
          .createCell(EStyle.NORMAL_242_CENTER_CENTER)
          .createCell(EStyle.NORMAL_242_JUSTIFY_CENTER)
          .createCell(categoryInfo.countSeat, EStyle.NORMAL_242_CENTER_CENTER)
          .createCell(categoryInfo.price, EStyle.NORMAL_242_RIGHT_CENTER_MONEY)
          .createCell(categoryInfo.sumSeat, EStyle.NORMAL_242_RIGHT_CENTER_MONEY);
    }
    formulaSubtotals.setRowEnd(sheet.getRowCurrentIndex() - 1);

    sheet.createRow()
        .createCell("Итого", EStyle.BOLD_216)
        .createCell(EStyle.BOLD_216)
        .createCell(EStyle.BOLD_216)
        .createCell(formulaSubtotals.nextFormula(), EStyle.BOLD_216_CENTER)
        .createCell(EStyle.BOLD_216)
        .createCell(formulaSubtotals.nextFormula(), EStyle.BOLD_216_RIGHT_MONEY);

    sheet.incRowCurrentIndex();
    sheet.incRowCurrentIndex();

    sheet.createRow().createCell("Итого на сумму: " + new Russian().amount(getBook().getCreationHelper().createFormulaEvaluator().evaluate(sheet.getRow(sheet.getRowCurrentIndex() - 4).getCell(5)).getNumberValue()), EStyle.BOLD);

    sheet.incRowCurrentIndex();

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("Передал                                                                                                                              Получил", EStyle.NORMAL);

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("__________________ (________________________)                                           __________________ (________________________)", EStyle.NORMAL);

    sheet.addMergedRegion(sheet.getRowCurrentIndex(), sheet.getRowCurrentIndex(), 0, 5);
    sheet.createRow().createCell("              подпись                           расшифровка                                                                        подпись                           расшифровка", EStyle.NORMAL);

    sheet.setColumnWidth(0, 8704);
    sheet.setColumnWidth(1, 2304);
    sheet.setColumnWidth(2, 5632);
    sheet.setColumnWidth(3, 3584);
    sheet.setColumnWidth(4, 3584);
    sheet.setColumnWidth(5, 3584);
  }

  @NotNull
  public final AFormInvoice generateData() {
    Map<String, Map<String, Map<BigDecimal, List<QuotaSeat>>>> sectorMap = new TreeMap<>();
    Map<String, Map<BigDecimal, Integer>> categoryMap = new TreeMap<>();
    for (QuotaSeat seat : quotaData.getQuotaSeatList()) {
      SeatLocationObj seatLocation = seat.getSeatLocation();
      if (seatLocation == null) {
        Map<BigDecimal, Integer> priceMap = categoryMap.get(seat.getCategory());
        if (priceMap == null) categoryMap.put(seat.getCategory(), priceMap = new TreeMap<>());
        Integer countSeat = priceMap.get(seat.getPrice());
        if (countSeat == null) priceMap.put(seat.getPrice(), 1);
        else priceMap.put(seat.getPrice(), countSeat + 1);
      } else {
        Map<String, Map<BigDecimal, List<QuotaSeat>>> rowMap = sectorMap.get(seatLocation.getSector());
        if (rowMap == null) sectorMap.put(seatLocation.getSector(), rowMap = new TreeMap<>(Comparators.STRING_AS_INTEGER));
        Map<BigDecimal, List<QuotaSeat>> priceMap = rowMap.get(seatLocation.getRow());
        if (priceMap == null) rowMap.put(seatLocation.getRow(), priceMap = new TreeMap<>());
        List<QuotaSeat> seatList = priceMap.get(seat.getPrice());
        if (seatList == null) priceMap.put(seat.getPrice(), seatList = new ArrayList<>());
        seatList.add(seat);
      }
    }

    List<SeatInfo> seatInfoList = new ArrayList<>();
    for (Map.Entry<String, Map<String, Map<BigDecimal, List<QuotaSeat>>>> sector : sectorMap.entrySet()) {
      for (Map.Entry<String, Map<BigDecimal, List<QuotaSeat>>> row : sector.getValue().entrySet()) {
        for (Map.Entry<BigDecimal, List<QuotaSeat>> price : row.getValue().entrySet()) {
          Set<String> seatSet = new TreeSet<>(Comparators.STRING_AS_INTEGER);
          int countSeat = price.getValue().size();
          BigDecimal sumSeat = price.getKey().multiply(new BigDecimal(countSeat));

          for (QuotaSeat seat : price.getValue()) {
            if (seat.getSeatLocation() == null) continue;
            seatSet.add(seat.getSeatLocation().getNumber());
          }

          StringBuilder seats = new StringBuilder();
          for (String seat : seatSet) seats.append(seat).append(", ");
          seats.setLength(seats.length() - 2);

          seatInfoList.add(new SeatInfo(countSeat, price.getKey(), sumSeat, sector.getKey(), row.getKey(), seats.toString()));
        }
      }
    }
    List<CategoryInfo> categoryInfoList = new ArrayList<>();
    for (Map.Entry<String, Map<BigDecimal, Integer>> category : categoryMap.entrySet()) {
      for (Map.Entry<BigDecimal, Integer> price : category.getValue().entrySet()) {
        int countSeat = price.getValue();
        BigDecimal sumSeat = price.getKey().multiply(new BigDecimal(countSeat));

        categoryInfoList.add(new CategoryInfo(countSeat, price.getKey(), sumSeat, category.getKey()));
      }
    }
    setData(new TotalInfo(seatInfoList, categoryInfoList));

    return this;
  }

  protected static final class TotalInfo {
    @NotNull
    private final List<SeatInfo> seatInfoList;
    @NotNull
    private final List<CategoryInfo> categoryInfoList;

    private TotalInfo(@NotNull List<SeatInfo> seatInfoList, @NotNull List<CategoryInfo> categoryInfoList) {
      this.seatInfoList = seatInfoList;
      this.categoryInfoList = categoryInfoList;
    }
  }

  private static final class SeatInfo extends AInfo {
    @NotNull
    private final String sector;
    @NotNull
    private final String row;
    @NotNull
    private final String seats;

    private SeatInfo(int countSeat, @NotNull BigDecimal price, @NotNull BigDecimal sumSeat, @NotNull String sector, @NotNull String row, @NotNull String seats) {
      super(countSeat, price, sumSeat);
      this.sector = sector;
      this.row = row;
      this.seats = seats;
    }
  }

  private static final class CategoryInfo extends AInfo {
    @NotNull
    private final String category;

    private CategoryInfo(int countSeat, @NotNull BigDecimal price, @NotNull BigDecimal sumSeat, @NotNull String category) {
      super(countSeat, price, sumSeat);
      this.category = category;
    }
  }

  private abstract static class AInfo {
    protected final int countSeat;
    @NotNull
    protected final BigDecimal price;
    @NotNull
    protected final BigDecimal sumSeat;

    private AInfo(int countSeat, @NotNull BigDecimal price, @NotNull BigDecimal sumSeat) {
      this.countSeat = countSeat;
      this.price = price;
      this.sumSeat = sumSeat;
    }
  }
}
