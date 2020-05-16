package client.component.suggestion;

import java.util.*;
import javax.swing.text.*;

public class ListAutoCompleter<E> extends AutoCompleter<E> {
  private final boolean ignoreCase;
  private final List<E> completionList;

  public ListAutoCompleter(JTextComponent textComponent, boolean arbitraryMatch, boolean ignoreCase, List<E> completionList) {
    super(textComponent, arbitraryMatch);
    this.ignoreCase = ignoreCase;
    this.completionList = completionList;
  }

  // update classes model depending on the data in textfield
  @Override
  protected boolean updateListData() {
    String value = textComponent.getText();
    int valueLen = value.length();

    Vector<E> possibleList = new Vector<>();
    for (E element : completionList) {
      String elementStr = element.toString();
      if (valueLen >= elementStr.length()) continue;
      if (ignoreCase) {
        elementStr = elementStr.toLowerCase();
        value = value.toLowerCase();
      }
      if (arbitraryMatch) {
        if (elementStr.contains(value)) possibleList.add(element);
      } else {
        if (elementStr.startsWith(value)) possibleList.add(element);
      }
    }

    list.setListData(possibleList);
    return true;
  }

  // user has selected some item in the classes. update textfield accordingly...
  @Override
  protected void acceptedListItem(Object selected) {
    if (selected == null) return;
    if (arbitraryMatch) {
      textComponent.setText(selected.toString());
    } else {
      int prefixLen = textComponent.getDocument().getLength();
      try {
        textComponent.getDocument().insertString(textComponent.getCaretPosition(), selected.toString().substring(prefixLen), null);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }
    popup.setVisible(false);
  }
}