package client.formatter;

import java.math.BigDecimal;
import java.text.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.08.15
 */
public class PositiveNumberFormatter extends InsensitiveDSNumberFormatter {
  private int minimumFractionDigits;
  private int minimumIntegerDigits;

  public PositiveNumberFormatter() {
    super(true);
    NumberFormat format = NumberFormat.getInstance();
    format.setMinimumIntegerDigits(1);
    format.setMaximumFractionDigits(2);
    setFormat(format);

    setValueClass(BigDecimal.class);
    super.setMinimum(BigDecimal.ZERO);
    setAllowsInvalid(false);
  }

  public PositiveNumberFormatter(NumberFormat format) {
    super(format, true);
    setValueClass(BigDecimal.class);
    super.setMinimum(BigDecimal.ZERO);
    setAllowsInvalid(false);
  }

  @Override
  public void setMinimum(Comparable minimum) {
    throw new UnsupportedOperationException("cannot change minimum");
  }

  @Override
  public void setFormat(Format format) {
    if (format instanceof NumberFormat) {
      minimumFractionDigits = ((NumberFormat) format).getMinimumFractionDigits();
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

    text = replaceDSIfNecessary(text);
    text = text.replace(Character.toString(dfs.getGroupingSeparator()), "");
    int len = text.length();

    int dSeparatorPos = -1;
    for (int i = 0; i < len; i++) {
      if (text.charAt(i) == dfs.getDecimalSeparator()) {
        if (dSeparatorPos > -1) throw new ParseException("multiple decimal separators", i);
        dSeparatorPos = i;
      }
    }
    if (dSeparatorPos > -1 && dSeparatorPos == len - 1) decimalFormat.setDecimalSeparatorAlwaysShown(true);
    else decimalFormat.setDecimalSeparatorAlwaysShown(false);

    int fractionLen = len - 1 - dSeparatorPos;
    if (dSeparatorPos > -1 && fractionLen > decimalFormat.getMaximumFractionDigits())
      throw new ParseException("maximum fraction digits", len - 1);
    if (dSeparatorPos > -1 && text.endsWith("0")) {
      decimalFormat.setMinimumFractionDigits(fractionLen);
    } else decimalFormat.setMinimumFractionDigits(minimumFractionDigits);

    int integerLen;
    if (dSeparatorPos > -1) integerLen = dSeparatorPos;
    else integerLen = len;
    if (integerLen > decimalFormat.getMaximumIntegerDigits()) throw new ParseException("maximum integer digits", 0);
    if (text.startsWith("0")) {
      decimalFormat.setMinimumIntegerDigits(integerLen);
    } else decimalFormat.setMinimumIntegerDigits(minimumIntegerDigits);

    return super.stringToValue(text);
  }
}
