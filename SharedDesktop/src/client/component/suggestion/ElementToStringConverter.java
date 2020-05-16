package client.component.suggestion;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.08.2016
 */
public interface ElementToStringConverter<E> {
  ElementToStringConverter<Object> DEFAULT = new ElementToStringConverter<Object>() {
    @Override
    public String stringValue(Object value) {
      if (value == null) return "";
      return value.toString();
    }
  };

  String stringValue(E value);
}
