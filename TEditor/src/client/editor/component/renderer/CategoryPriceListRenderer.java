package client.editor.component.renderer;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;

import server.protocol2.editor.CategoryPriceObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class CategoryPriceListRenderer implements ListCellRenderer<CategoryPriceObj> {
  private static final NumberFormat priceFormat = NumberFormat.getInstance(new Locale("ru", "RU"));
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

  @Override
  public Component getListCellRendererComponent(JList<? extends CategoryPriceObj> list, CategoryPriceObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = (value == null) ? "" : value.getName() + " - " + priceFormat.format(value.getPrice()) + " руб.";
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }
}
