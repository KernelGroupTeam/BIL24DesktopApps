package client.utils;

import java.math.*;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 14.06.16
 */
public class Format {
  private static final DecimalFormat format = new DecimalFormat();
  private static final BigDecimal kb = new BigDecimal(1024);
  private static final BigDecimal mb = new BigDecimal(1024 * 1024);

  private Format() {
  }

  public static synchronized String bytesToStr(long bytes) {
    if (bytes < 1024) return format.format(bytes) + " Б";
    BigDecimal value = new BigDecimal(bytes);
    if (bytes < 10 * 1024) {// 1 KB - 10 KB
      return format.format(value.divide(kb, 1, RoundingMode.HALF_UP)) + " КБ";
    }
    if (bytes < 1024 * 1024) {// 10 KB - 1 МБ
      return format.format(value.divide(kb, 0, RoundingMode.HALF_UP)) + " КБ";
    }
    if (bytes < 10 * 1024 * 1024) {// 1 MB - 10 MB
      return format.format(value.divide(mb, 1, RoundingMode.HALF_UP)) + " МБ";
    }
    return format.format(value.divide(mb, 0, RoundingMode.HALF_UP)) + " МБ";
  }
}
