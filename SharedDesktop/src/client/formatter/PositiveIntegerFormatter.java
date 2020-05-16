package client.formatter;

import java.text.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.08.15
 */
public class PositiveIntegerFormatter extends CacheDFSNumberFormatter {
  private int minimumIntegerDigits;

  public PositiveIntegerFormatter() {
    super(true);
    NumberFormat format = NumberFormat.getIntegerInstance();
    setFormat(format);

    setValueClass(Integer.class);
    super.setMinimum(0);
    setAllowsInvalid(false);
  }

  public PositiveIntegerFormatter(Integer max) {
    this();
    setMaximum(max);
  }

  public PositiveIntegerFormatter(NumberFormat format) {
    super(format, true);
    setValueClass(Integer.class);
    super.setMinimum(0);
    setAllowsInvalid(false);
  }

  public PositiveIntegerFormatter(NumberFormat format, Integer max) {
    this(format);
    setMaximum(max);
  }

  @Override
  public void setMinimum(Comparable minimum) {
    throw new UnsupportedOperationException("cannot change minimum");
  }

  @Override
  public void setFormat(Format format) {
    if (format instanceof NumberFormat) {
      minimumIntegerDigits = ((NumberFormat) format).getMinimumIntegerDigits();
    }
    super.setFormat(format);
  }

  @Override
  public Object stringToValue(String text) throws ParseException {
    if (text.isEmpty() && isNullableValue()) return null;

    Format format = getFormat();
    if (!(format instanceof DecimalFormat)) return super.stringToValue(text);
    DecimalFormat decimalFormat = ((DecimalFormat) format);
    DecimalFormatSymbols dfs = getDecimalFormatSymbols();
    if (dfs == null) return super.stringToValue(text);

    if (text.indexOf(dfs.getDecimalSeparator()) > -1) throw new ParseException("decimal separator", 0);
    text = text.replace(Character.toString(dfs.getGroupingSeparator()), "");

    int integerLen = text.length();
    if (integerLen > decimalFormat.getMaximumIntegerDigits()) throw new ParseException("maximum integer digits", 0);
    if (text.startsWith("0")) {
      decimalFormat.setMinimumIntegerDigits(integerLen);
    } else decimalFormat.setMinimumIntegerDigits(minimumIntegerDigits);

    return super.stringToValue(text);
  }
}
