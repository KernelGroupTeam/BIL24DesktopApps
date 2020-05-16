package client.formatter;

import java.text.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 07.08.15
 */
public class CacheDFSNumberFormatter extends NullableNumberFormatter {
  private Format cacheFormat;
  private DecimalFormatSymbols cacheDecimalFormatSymbols;

  public CacheDFSNumberFormatter(boolean nullableValue) {
    super(nullableValue);
    cacheDFS();
  }

  public CacheDFSNumberFormatter(NumberFormat format, boolean nullableValue) {
    super(format, nullableValue);
    cacheDFS();
  }

  public DecimalFormatSymbols getDecimalFormatSymbols() {//nullable
    Format format = getFormat();
    if (format == null) return null;
    if (!format.equals(cacheFormat)) {//если формат изменился
      cacheDFS();
    }
    return cacheDecimalFormatSymbols;
  }

  private void cacheDFS() {
    Format format = getFormat();
    if (format instanceof DecimalFormat) {//null format returns false
      DecimalFormat decimalFormat = ((DecimalFormat) format);
      cacheDecimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
    } else {
      cacheDecimalFormatSymbols = null;
    }
    cacheFormat = format;
  }

}
