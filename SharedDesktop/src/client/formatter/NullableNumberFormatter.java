package client.formatter;

import java.text.*;
import javax.swing.text.NumberFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 03.08.15
 *
 * Допускает пустое значение форматируемого поля, при этом условии возващаемое значение равно null
 */
public class NullableNumberFormatter extends NumberFormatter {
  private boolean nullableValue;

  public NullableNumberFormatter(boolean nullableValue) {
    this.nullableValue = nullableValue;
  }

  public NullableNumberFormatter(NumberFormat format, boolean nullableValue) {
    super(format);
    this.nullableValue = nullableValue;
  }

  public boolean isNullableValue() {
    return nullableValue;
  }

  public void setNullableValue(boolean nullableValue) {
    this.nullableValue = nullableValue;
  }

  @Override
  public Object stringToValue(String text) throws ParseException {
    if (nullableValue && text.isEmpty()) return null;
    return super.stringToValue(text);
  }
}
