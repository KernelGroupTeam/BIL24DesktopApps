package client.reporter.component.renderer;

import java.awt.*;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import server.protocol2.reporter.QuotaDataObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 15.03.17
 */
public class QuotaDataListRenderer implements ListCellRenderer<QuotaDataObj>, ElementToStringConverter<QuotaDataObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends QuotaDataObj> list, QuotaDataObj value, int index, boolean isSelected, boolean cellHasFocus) {
    return renderer.getListCellRendererComponent(list, stringValue(value), index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(QuotaDataObj value) {
    StringBuilder str = new StringBuilder();
    if (value != null) {
      switch (value.getType()) {
        case IN:
          str.append("Приходная накладная ");
          break;
        case OUT:
          str.append("Накладная на возврат ");
          break;
      }
      str.append("№").append(value.getNumber()).append(" от ").append(value.getDate());
    }
    return str.toString();
  }
}
