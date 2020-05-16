package client.component.suggestion;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
// edit by Maksim Ponomarev
class ComboAutoCompleter<E> {
  private static final Color errorColor = new Color(255, 102, 102);
  private final JList<E> list = new JList<>();
  private final SuggestionListRenderer<E> listRenderer;
  private final JPopupMenu popup = new JPopupMenu();
  private final JTextField textComponent = new JTextField();
  private final Border matchBorder = new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK), new EmptyBorder(1, 1, 1, 1));
  private final Border notMatchBorder = new EmptyBorder(1, 1, 1, 1);

  private final JComboBox<E> comboBox;
  private final boolean arbitraryMatch;
  private final boolean ignoreCase;
  private final ListModel<E> completionList;
  private final ElementToStringConverter<? super E> elementToStringConverter;
  private boolean excludeFirstItem = false;

  public ComboAutoCompleter(JComboBox<E> comboBox, boolean arbitraryMatch, boolean ignoreCase, ListModel<E> completionList,
                            ElementToStringConverter<? super E> elementToStringConverter) {
    this.comboBox = comboBox;
    this.arbitraryMatch = arbitraryMatch;
    this.ignoreCase = ignoreCase;
    this.completionList = completionList;
    this.elementToStringConverter = elementToStringConverter;

    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(null);

    listRenderer = new SuggestionListRenderer<>(ignoreCase, elementToStringConverter);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(listRenderer);
    list.setFocusable(false);
    scroll.getVerticalScrollBar().setFocusable(false);
    scroll.getHorizontalScrollBar().setFocusable(false);

    popup.setBorder(BorderFactory.createLineBorder(Color.black));
    popup.add(textComponent);
    popup.add(scroll);

    textComponent.registerKeyboardAction(downAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
    textComponent.getDocument().addDocumentListener(documentListener);
    textComponent.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
    textComponent.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

    popup.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        ComboAutoCompleter.this.textComponent.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
    list.setRequestFocusEnabled(false);
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        E value = list.getSelectedValue();
        if (listRenderer.isContinuationMarker(value)) showPopup(true);
        else {
          popup.setVisible(false);
          acceptedListItem(value);
        }
      }
    });
  }

  DocumentListener documentListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      showPopup();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      showPopup();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
  };

  Action acceptAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      E value = list.getSelectedValue();
      if (listRenderer.isContinuationMarker(value)) showPopup(true);
      else {
        popup.setVisible(false);
        acceptedListItem(value);
      }
    }
  };

  Action downAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (popup.isVisible()) selectNextPossibleValue();
      else showPopup();
    }
  };

  Action upAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (popup.isVisible()) selectPreviousPossibleValue();
    }
  };

  Action hidePopupAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      popup.setVisible(false);
    }
  };

  public void setExcludeFirstItem(boolean excludeFirstItem) {
    this.excludeFirstItem = excludeFirstItem;
  }

  public void setTextAndShow(String text) {
    textComponent.setText(text);
    if (!popup.isVisible()) showPopup();
  }

  private void showPopup() {
    showPopup(false);
  }

  private void showPopup(boolean fullUpdate) {
    popup.setVisible(false);
    if (updateListData(fullUpdate)) {
      textComponent.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
      int size = list.getModel().getSize();
      list.setVisibleRowCount(Math.min(size, 10));

      popup.show(comboBox, 0, comboBox.getHeight() + 1);
    } else {
      popup.setVisible(false);
    }
    textComponent.requestFocus();
  }

  /**
   * Selects the next item in the list.  It won't change the selection if the
   * currently selected item is already the last item.
   */
  protected void selectNextPossibleValue() {
    int si = list.getSelectedIndex();

    if (si < list.getModel().getSize() - 1) {
      list.setSelectedIndex(si + 1);
      list.ensureIndexIsVisible(si + 1);
    }
  }

  /**
   * Selects the previous item in the list.  It won't change the selection if the
   * currently selected item is already the first item.
   */
  protected void selectPreviousPossibleValue() {
    int si = list.getSelectedIndex();

    if (si > 0) {
      list.setSelectedIndex(si - 1);
      list.ensureIndexIsVisible(si - 1);
    }
  }

  // update list model depending on the data in textfield
  protected boolean updateListData(boolean full) {
    int from = (excludeFirstItem ? 1 : 0);
    if (completionList.getSize() <= from) return false;
    String value = textComponent.getText();
    listRenderer.setContinuationMarker(null);
    listRenderer.setMatchStr(value);
    if (ignoreCase) value = value.toLowerCase().replace('ё', 'е');
    else value = value.replace('ё', 'е').replace('Ё', 'Е');
    int valueLen = value.length();

    Vector<E> possibleList = new Vector<>();
    for (int i = from; i < completionList.getSize(); i++) {
      E element = completionList.getElementAt(i);
      String elementStr = elementToStringConverter.stringValue(element);
      if (valueLen > elementStr.length()) continue;
      if (ignoreCase) elementStr = elementStr.toLowerCase().replace('ё', 'е');
      else elementStr = elementStr.replace('ё', 'е').replace('Ё', 'Е');
      if (arbitraryMatch) {
        if (elementStr.contains(value)) possibleList.add(element);
      } else {
        if (elementStr.startsWith(value)) possibleList.add(element);
      }
      if (possibleList.size() >= 10 && !full) {
        listRenderer.setContinuationMarker(element);
        break;
      }
    }

    list.setListData(possibleList);
    if (possibleList.isEmpty()) {
      textComponent.setForeground(Color.WHITE);
      textComponent.setBackground(errorColor);
      if (notMatchBorder != textComponent.getBorder()) textComponent.setBorder(notMatchBorder);
    } else {
      textComponent.setForeground(Color.BLACK);
      textComponent.setBackground(Color.WHITE);
      if (matchBorder != textComponent.getBorder()) textComponent.setBorder(matchBorder);
    }
    return true;
  }

  // user has selected some item in the list. update textfield accordingly...
  protected void acceptedListItem(E selected) {
    if (selected != null) comboBox.setSelectedItem(selected);
  }
}
