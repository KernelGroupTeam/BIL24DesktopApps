package client.component.suggestion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
// edit by Maksim Ponomarev
public abstract class AutoCompleter<E> {
  private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
  final JList<E> list = new JList<>();
  final JPopupMenu popup = new JPopupMenu();
  final JTextComponent textComponent;
  final boolean arbitraryMatch;

  public AutoCompleter(JTextComponent textComponent, boolean arbitraryMatch) {
    this.textComponent = textComponent;
    this.arbitraryMatch = arbitraryMatch;
    this.textComponent.putClientProperty(AUTOCOMPLETER, this);
    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(null);

    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setFocusable(false);
    scroll.getVerticalScrollBar().setFocusable(false);
    scroll.getHorizontalScrollBar().setFocusable(false);

    popup.setBorder(BorderFactory.createLineBorder(Color.black));
    popup.add(scroll);

    if (this.textComponent instanceof JTextField) {
      this.textComponent.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
      this.textComponent.getDocument().addDocumentListener(documentListener);
    } else {
      this.textComponent.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
    }
    this.textComponent.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
    this.textComponent.registerKeyboardAction(hidePopupAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

    popup.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        AutoCompleter.this.textComponent.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
    list.setRequestFocusEnabled(false);
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

  static Action acceptAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      AutoCompleter<?> completer = (AutoCompleter<?>) tf.getClientProperty(AUTOCOMPLETER);
      completer.popup.setVisible(false);
      completer.acceptedListItem(completer.list.getSelectedValue());
    }
  };

  static Action showAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      AutoCompleter<?> completer = (AutoCompleter<?>) tf.getClientProperty(AUTOCOMPLETER);
      if (tf.isEnabled()) {
        if (completer.popup.isVisible()) completer.selectNextPossibleValue();
        else completer.showPopup();
      }
    }
  };

  static Action upAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      AutoCompleter<?> completer = (AutoCompleter<?>) tf.getClientProperty(AUTOCOMPLETER);
      if (tf.isEnabled()) {
        if (completer.popup.isVisible()) completer.selectPreviousPossibleValue();
      }
    }
  };

  static Action hidePopupAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      AutoCompleter<?> completer = (AutoCompleter<?>) tf.getClientProperty(AUTOCOMPLETER);
      if (tf.isEnabled()) completer.popup.setVisible(false);
    }
  };

  private void showPopup() {
    popup.setVisible(false);
    if (textComponent.isEnabled() && updateListData() && list.getModel().getSize() != 0) {
      if (!(textComponent instanceof JTextField)) textComponent.getDocument().addDocumentListener(documentListener);
      textComponent.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
      int size = list.getModel().getSize();
      list.setVisibleRowCount(Math.min(size, 10));

      int x = 0;
      if (!arbitraryMatch) {
        try {
          int pos = Math.min(textComponent.getCaret().getDot(), textComponent.getCaret().getMark());
          x = textComponent.getUI().modelToView(textComponent, pos).x;
        } catch (BadLocationException e) {
          e.printStackTrace();// this should never happen!!!
        }
      }
      popup.show(textComponent, x, textComponent.getHeight());
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
  protected abstract boolean updateListData();

  // user has selected some item in the list. update textfield accordingly...
  protected abstract void acceptedListItem(Object selected);
}
