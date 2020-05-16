package report.utils;

import java.text.SimpleDateFormat;
import java.util.*;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 23.11.2017
 */
public class DateFormats {
  @NotNull
  private static final Map<ETemplate, Map<Date, String>> templateMap = new EnumMap<>(ETemplate.class);

  private DateFormats() {
  }

  @NotNull
  public static String format(@Nullable Date date, @NotNull ETemplate template) {
    if (date == null) return "";
    Map<Date, String> dateMap = templateMap.get(template);
    if (dateMap == null) templateMap.put(template, dateMap = new HashMap<>());
    String format = dateMap.get(date);
    if (format == null) dateMap.put(date, format = template.threadLocal.get().format(date));
    return format;
  }

  @SuppressWarnings({"EnumeratedConstantNamingConvention", "ThreadLocalNotStaticFinal"})
  public enum ETemplate {
    HHmm("HH:mm"),
    ddMMyyyyHHmm("dd.MM.yyyy HH:mm"),
    ddMMyyyyHHmmss("dd.MM.yyyy HH:mm:ss"),
    ;

    @NotNull
    private final String template;
    @NotNull
    private final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
      @Override
      protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(template);
      }
    };

    ETemplate(@NotNull String template) {
      this.template = template;
    }
  }
}
