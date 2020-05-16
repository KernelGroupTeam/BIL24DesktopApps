package client.editor.model;

import java.util.*;

import org.jdesktop.swingx.decorator.Filter;
import org.jetbrains.annotations.Nullable;
import server.protocol2.common.SubsActionObj;
import server.protocol2.editor.ActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 09.10.17
 */
public class SubsActionFilter extends Filter {
  private final Type type;
  private ArrayList<Integer> toPrevious;
  private boolean filterActual = false;
  @Nullable
  private Long cityId;

  public SubsActionFilter(Type type) {
    this.type = type;
    refresh();
  }

  public boolean isFilterActual() {
    return filterActual;
  }

  public void setFilterActual(boolean filterActual) {
    boolean refresh = (this.filterActual != filterActual);
    this.filterActual = filterActual;
    if (refresh) refresh();
  }

  public void setCityId(@Nullable Long cityId) {
    boolean refresh = !Objects.equals(this.cityId, cityId);
    this.cityId = cityId;
    if (refresh) refresh();
  }

  @Override
  public int getSize() {
    return toPrevious.size();
  }

  @Override
  protected void init() {
    toPrevious = new ArrayList<>();
  }

  @Override
  protected void reset() {
    toPrevious.clear();
    int inputSize = getInputSize();
    fromPrevious = new int[inputSize];  // fromPrevious is inherited protected
    for (int i = 0; i < inputSize; i++) {
      fromPrevious[i] = -1;
    }
  }

  @Override
  protected void filter() {
    int inputSize = getInputSize();
    int current = 0;
    for (int i = 0; i < inputSize; i++) {
      if (test(i)) {
        toPrevious.add(i);
        // generate inverse map entry while we are here
        fromPrevious[i] = current++;
      }
    }
  }

  @SuppressWarnings("SimplifiableIfStatement")
  private boolean test(int row) {
    if (!adapter.isTestable(getColumnIndex())) return false;
    if (type == Type.ACTION) {
      ActionObj action = (ActionObj) getInputValue(row, getColumnIndex());
      if (cityId != null && !action.getCityIdSet().contains(cityId)) return false;
      if (!filterActual) return true;
      return action.isActual();
    } else if (type == Type.SUBS_ACTION) {
      SubsActionObj subsAction = (SubsActionObj) getInputValue(row, getColumnIndex());
      if (cityId != null && !subsAction.getCityIdSet().contains(cityId)) return false;
      if (!filterActual) return true;
      return subsAction.isActual();
    }
    throw new IllegalStateException();
  }

  @Override
  protected int mapTowardModel(int row) {
    return toPrevious.get(row);
  }

  public enum Type {ACTION, SUBS_ACTION}
}
