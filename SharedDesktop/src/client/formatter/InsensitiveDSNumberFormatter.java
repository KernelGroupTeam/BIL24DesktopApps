package client.formatter;

import java.text.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 03.08.15
 */
public class InsensitiveDSNumberFormatter extends CacheDFSNumberFormatter {
  private boolean insensitiveDecimalSeparator = true;

  public InsensitiveDSNumberFormatter(boolean nullableValue) {
    super(nullableValue);
  }

  public InsensitiveDSNumberFormatter(NumberFormat format, boolean nullableValue) {
    super(format, nullableValue);
  }

  public boolean isInsensitiveDecimalSeparator() {
    return insensitiveDecimalSeparator;
  }

  public void setInsensitiveDecimalSeparator(boolean insensitiveDecimalSeparator) {
    this.insensitiveDecimalSeparator = insensitiveDecimalSeparator;
  }

  @Override
  public Object stringToValue(String text) throws ParseException {
    text = replaceDSIfNecessary(text);
    return super.stringToValue(text);
  }

  String replaceDSIfNecessary(String text) {
    if (insensitiveDecimalSeparator) {
      DecimalFormatSymbols dfs = getDecimalFormatSymbols();
      if (dfs != null) text = text.replace('.', dfs.getDecimalSeparator());
    }
    return text;
  }
}
