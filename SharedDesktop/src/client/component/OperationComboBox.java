package client.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import client.component.changes.ChComboBox;
import client.component.listener.OperationListener;
import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 18.07.15
 */
// Важная информация:
//
// JComboBox.removeAllItems() вызывает во время своего выполнения:
// ItemEvent[stateChange=DESELECTED]
// ActionEvent[cmd=comboBoxChanged]
//
// после этого вызов JComboBox.addItem() вызывает во время своего выполнения:
// ItemEvent[stateChange=SELECTED]
// ActionEvent[cmd=comboBoxChanged]
public class OperationComboBox<E> extends ChComboBox<E> {
  private static final DummyOperationListener dummyOperationListener = new DummyOperationListener();
  private final List<E> elementList = new ArrayList<>();
  private final List<E> filteredElementList = new ArrayList<>();
  private final List<PredicateFilter<? super E>> filterList = new ArrayList<>();
  private final Map<OperationComboBox<?>, OperationFilter<? super E>> parentMap = new HashMap<>();
  private final Set<OperationComboBox<?>> childSet = new HashSet<>();

  private boolean cancellationMode = false;//используется при отмене выбора, для исключения рекурсии
  @NotNull
  private Component changeConfirmComponent = this;
  @NotNull
  private String changeConfirmText = "Сохранить изменения?";
  @NotNull
  private OperationListener<? super E> operationListener = dummyOperationListener;

  public OperationComboBox() {
    addActionListener(new EActionListener());
    addItemListener(new EItemListener());
  }

  @Override
  public void addItem(E item) {
    directAccess();
  }

  @Override
  public void insertItemAt(E item, int index) {
    directAccess();
  }

  @Override
  public void removeItem(Object anObject) {
    directAccess();
  }

  @Override
  public void removeItemAt(int anIndex) {
    directAccess();
  }

  @Override
  public void removeAllItems() {
    directAccess();
  }

  private void directAccess() {
    throw new UnsupportedOperationException("direct access");
  }

  @NotNull
  public Component getChangeConfirmComponent() {
    return changeConfirmComponent;
  }

  public void setChangeConfirmComponent(@NotNull Component changeConfirmComponent) {
    this.changeConfirmComponent = changeConfirmComponent;
  }

  @NotNull
  public String getChangeConfirmText() {
    return changeConfirmText;
  }

  public void setChangeConfirmText(@NotNull String changeConfirmText) {
    this.changeConfirmText = changeConfirmText;
  }

  public void setOperationListener(@NotNull OperationListener<? super E> operationListener) {
    this.operationListener = new OverriddenOperationListener(operationListener);
  }

  public void addFilter(@NotNull PredicateFilter<? super E> filter) {
    filterList.add(filter);
    filter();
  }

  public void removeFilter(@NotNull PredicateFilter<? super E> filter) {
    if (filterList.remove(filter)) filter();
  }

  public void linkTo(@NotNull OperationComboBox<?> parentComboBox, @NotNull OperationFilter<? super E> filter) {
    if (!parentComboBox.canLink(this)) throw new UnsupportedOperationException("recursive link");
    parentMap.put(parentComboBox, filter);
    parentComboBox.childSet.add(this);
    filter();
  }

  public void unlinkFrom(@NotNull OperationComboBox<?> parentComboBox) {
    parentMap.remove(parentComboBox);
    parentComboBox.childSet.remove(this);
    filter();
  }

  //рекурсивный обход вверх, чтобы убедиться, что childComboBox выше не используется, позволяет избежать закольцевания
  private boolean canLink(OperationComboBox<?> childComboBox) {
    if (childComboBox == this) return false;
    for (OperationComboBox<?> parent : parentMap.keySet()) {
      if (!parent.canLink(childComboBox)) return false;
    }
    return true;
  }

  //рекурсивный обход вниз, чтобы убедиться, что все потомки согласны на фильтрование
  private boolean canFilter() {
    for (OperationComboBox<?> child : childSet) {
      if (!child.canFilter()) return false;
    }
    return canExit();
  }

  //родитель поменялся, надо заново отфильтровать список и оповестить потомков
  private void filter() {
    rebuildFilteredElementList();
    E currentElement = getSelectedElement();
    int fIndex = -1;
    if (currentElement != null && (fIndex = filteredElementList.indexOf(currentElement)) >= 0) cancellationMode = true;
    super.removeAllItems();
    for (E item : filteredElementList) {
      super.addItem(item);
    }
    if (cancellationMode) {
      setSelectedItem(filteredElementList.get(fIndex));
      cancellationMode = false;
    } else {
      for (OperationComboBox<?> child : childSet) {
        child.filter();
      }
    }
  }

  private boolean addToFilteredElementList(E newElement) {
    boolean pass = true;
    for (PredicateFilter<? super E> filter : filterList) {
      pass = pass && filter.filter(newElement);
    }
    if (!pass) return false;
    for (Map.Entry<OperationComboBox<?>, OperationFilter<? super E>> entry : parentMap.entrySet()) {
      Object match = entry.getKey().getSelectedElement();
      OperationFilter<? super E> filter = entry.getValue();
      pass = pass && filter.filter(newElement, match);
    }
    if (pass) filteredElementList.add(newElement);
    return pass;
  }

  private void rebuildFilteredElementList() {
    filteredElementList.clear();
    for (E e : elementList) {
      addToFilteredElementList(e);
    }
  }

  public void addElement(E element, boolean select) {
    if (element == null) return;
    elementList.add(0, element);
    if (addToFilteredElementList(element)) {
      super.insertItemAt(element, 0);
      if (select) setSelectedItem(element);
    }
  }

  public void setElementList(List<? extends E> list) {
    if (list == null) return;
    elementList.clear();
    elementList.addAll(list);
    filter();
  }

  public List<E> getElementList() {
    return Collections.unmodifiableList(elementList);
  }

  public List<E> getFilteredElementList() {
    return Collections.unmodifiableList(filteredElementList);
  }

  @Nullable
  public E getSelectedElement() {
    return getItemAt(getSelectedIndex());
  }

  public void removeElement(E element) {
    if (element == null) return;
    resetEdited();
    elementList.remove(element);
    filteredElementList.remove(element);
    super.removeItem(element);
  }

  //OverriddenOperationListener.check() вернёт true только если getSelectedElement() не null
  @SuppressWarnings("ConstantConditions")
  public void saveChanges() {
    if (operationListener.check()) operationListener.save(getSelectedElement());
  }

  public void reload() {
    E selectedElement = getSelectedElement();
    if (selectedElement != null) operationListener.load(selectedElement);
  }

  //OverriddenOperationListener.check() вернёт true только если getSelectedElement() не null
  @SuppressWarnings({"RedundantIfStatement", "ConstantConditions"})
  public boolean canExit() {
    if (!isEdited()) return true;
    int res = JOptionPane.showConfirmDialog(changeConfirmComponent, changeConfirmText, "Вопрос", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (res == JOptionPane.YES_OPTION) {
      if (!operationListener.check() || !operationListener.save(getSelectedElement())) return false;
//      resetEdited() внутри operationListener.save()
    }
    if (res == JOptionPane.NO_OPTION) reload();//resetEdited() внутри reload() -> operationListener.load()
    if (res == JOptionPane.CANCEL_OPTION) return false;
    return true;
  }

  private class EActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (cancellationMode) return;
      if (getSelectedIndex() == -1) operationListener.clear();
    }
  }

  @SuppressWarnings("unchecked")
  private class EItemListener implements ItemListener {
    private Object lastItem = null;//используется для отмены выбора

    @Override
    public void itemStateChanged(ItemEvent e) {
      if (cancellationMode) return;
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        if (!childrenCanFilter()) {//если кто-то из потомков не готов к смене данных
          lastItem = e.getItem();
          return;
        }
        if (isEdited()) {
          int res = JOptionPane.showConfirmDialog(changeConfirmComponent, changeConfirmText, "Вопрос", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
          if (res == JOptionPane.YES_OPTION) {
            if (operationListener.check() && operationListener.save((E) e.getItem())) {
//              resetEdited() внутри operationListener.save()
              lastItem = null;
            } else lastItem = e.getItem();
          }
          if (res == JOptionPane.NO_OPTION) {
            chComponentsClearValue();
            resetEdited();
            lastItem = null;
          }
          if (res == JOptionPane.CANCEL_OPTION) {
            lastItem = e.getItem();
          }
        }
      }
      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (lastItem == null && !isEdited()) {
          try {
            operationListener.load((E) e.getItem());
          } catch (ClassCastException ex) {
            operationListener.clear();
          }
          childrenFilter();
        } else if (lastItem != null) {
          cancellationMode = true;
          setSelectedItem(lastItem);
          lastItem = null;
          cancellationMode = false;
        }
      }
    }

    private boolean childrenCanFilter() {
      for (OperationComboBox<?> child : childSet) {
        if (!child.canFilter()) return false;
      }
      return true;
    }

    private void childrenFilter() {
      for (OperationComboBox<?> child : childSet) {
        child.filter();
      }
    }
  }

  private class OverriddenOperationListener implements OperationListener<E> {
    private final OperationListener<? super E> overriddenListener;

    public OverriddenOperationListener(@NotNull OperationListener<? super E> overriddenListener) {
      this.overriddenListener = overriddenListener;
    }

    @Override
    public void clear() {
      chComponentsClearValue();
      overriddenListener.clear();
      resetEdited();
    }

    @Override
    public boolean check() {
      if (OperationComboBox.this.getSelectedElement() == null) {
        OperationComboBox.this.requestFocus();
        JOptionPane.showMessageDialog(changeConfirmComponent, "Сначала необходимо выбрать элемент", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return false;
      }
      return overriddenListener.check();
    }

    @Override
    public void load(@NotNull E object) {
      chComponentsClearValue();
      overriddenListener.load(object);
      chComponentsSetValue();
      resetEdited();
    }

    @Override
    public boolean save(@NotNull E object) {
      boolean result = overriddenListener.save(object);
      if (result) {
        chComponentsSetValue();
        resetEdited();
      }
      return result;
    }
  }

  private static class DummyOperationListener implements OperationListener<Object> {
    @Override
    public void clear() {
    }

    @Override
    public boolean check() {
      return true;
    }

    @Override
    public void load(@NotNull Object object) {
    }

    @Override
    public boolean save(@NotNull Object object) {
      return true;
    }
  }

}
