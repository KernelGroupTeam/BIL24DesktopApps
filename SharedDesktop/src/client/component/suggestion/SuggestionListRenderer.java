package client.component.suggestion;

import java.awt.*;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 10.06.16
 */
class SuggestionListRenderer<E> implements ListCellRenderer<E> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private final boolean ignoreCase;
  private final ElementToStringConverter<? super E> elementToStringConverter;
  private E continuationMarker;
  private String matchStr = "";

  public SuggestionListRenderer(boolean ignoreCase, ElementToStringConverter<? super E> elementToStringConverter) {
    this.ignoreCase = ignoreCase;
    this.elementToStringConverter = elementToStringConverter;
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
    String strValue;
    if (value == null) strValue = "";
    else if (value == continuationMarker) strValue = ". . .";
    else {
      strValue = elementToStringConverter.stringValue(value);
      if (!strValue.isEmpty()) {
        String strValue2;
        String matchStr2;
        if (ignoreCase) {
          strValue2 = strValue.toLowerCase().replace('ё', 'е');
          matchStr2 = matchStr.toLowerCase().replace('ё', 'е');
        } else {
          strValue2 = strValue.replace('ё', 'е').replace('Ё', 'Е');
          matchStr2 = matchStr.replace('ё', 'е').replace('Ё', 'Е');
        }
        int start = strValue2.indexOf(matchStr2);
        if (start > -1) {
          int end = start + matchStr2.length();
          strValue = "<html>" + strValue.substring(0, start) +
              "<span style=\"background-color:#38D878;color:#FFFFFF;\">" + strValue.substring(start, end) + "</span>" +
              strValue.substring(end) + "</html>";
        }
      }
    }
    return renderer.getListCellRendererComponent(list, strValue, index, isSelected, cellHasFocus);
  }

  public boolean isContinuationMarker(E value) {
    return continuationMarker != null && continuationMarker == value;
  }

  public void setContinuationMarker(E continuationMarker) {
    this.continuationMarker = continuationMarker;
  }

  public void setMatchStr(String matchStr) {
    this.matchStr = matchStr;
  }
}
