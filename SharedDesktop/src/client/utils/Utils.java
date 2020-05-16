package client.utils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 18.06.15
 */
public class Utils {
  public static final BigDecimal minLat = new BigDecimal("-90");
  public static final BigDecimal maxLat = new BigDecimal("90");
  public static final BigDecimal minLon = new BigDecimal("-180");
  public static final BigDecimal maxLon = new BigDecimal("180");
  public static final long todayMidnight;

  static {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    todayMidnight = calendar.getTime().getTime();
  }

  private Utils() {
  }

  public static Date endOfDay(Date beginningOfDay) {//получает дату с 0:00:00.000 возвращает дату с 23:59:59.999
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(beginningOfDay);
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MILLISECOND, -1);
    return cal.getTime();
  }

  public static String getSystemInfo() {
    return "OS: " + System.getProperty("os.name") + " [" + System.getProperty("os.version") + "] " + System.getProperty("sun.os.patch.level") +
        "\nJava: " + System.getProperty("java.version") + " [" + System.getProperty("sun.arch.data.model") + "-bit] ";
  }
}
