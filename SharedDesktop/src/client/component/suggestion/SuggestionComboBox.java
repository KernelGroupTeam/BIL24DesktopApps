package client.component.suggestion;

import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 10.06.16
 */
public class SuggestionComboBox<E> extends JComboBox<E> {
  private ElementToStringConverter<? super E> elementToStringConverter = ElementToStringConverter.DEFAULT;
  private boolean suggest = true;
  private boolean excludeFirstItem = false;
  private ComboAutoCompleter<E> completer = null;

  public SuggestionComboBox(ComboBoxModel<E> aModel) {
    super(aModel);
  }

  public SuggestionComboBox(E[] items) {
    super(items);
  }

  public SuggestionComboBox(Vector<E> items) {
    super(items);
  }

  public SuggestionComboBox() {
  }

  @Override
  public void setModel(ComboBoxModel<E> aModel) {
    super.setModel(aModel);
    completer = null;
  }

  @Override
  public boolean selectWithKeyChar(char keyChar) {
    if (suggest) {
      if (completer == null) {
        completer = new ComboAutoCompleter<>(this, true, true, getModel(), elementToStringConverter);
        completer.setExcludeFirstItem(excludeFirstItem);
      }
      if (KeyEvent.VK_DELETE == keyChar) completer.setTextAndShow("");
      else completer.setTextAndShow(String.valueOf(keyChar));
      return true;
    }
    return super.selectWithKeyChar(keyChar);
  }

  public ElementToStringConverter<? super E> getElementToStringConverter() {
    return elementToStringConverter;
  }

  public void setElementToStringConverter(ElementToStringConverter<? super E> elementToStringConverter) {
    if (elementToStringConverter == null) throw new NullPointerException();
    this.elementToStringConverter = elementToStringConverter;
    completer = null;
  }

  public boolean isSuggest() {
    return suggest;
  }

  public void setSuggest(boolean suggest) {
    this.suggest = suggest;
  }

  public boolean isExcludeFirstItem() {
    return excludeFirstItem;
  }

  public void setExcludeFirstItem(boolean excludeFirstItem) {
    this.excludeFirstItem = excludeFirstItem;
    if (completer != null) completer.setExcludeFirstItem(excludeFirstItem);
  }
}
