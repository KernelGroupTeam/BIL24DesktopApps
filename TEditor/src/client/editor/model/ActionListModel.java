package client.editor.model;

import java.util.*;
import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.ActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 26.02.17
 */
public class ActionListModel extends AbstractListModel<ActionObj> {
  @NotNull
  private final List<ActionObj> data = new ArrayList<>();
  private boolean groupByOrganizer = false;

  @Override
  public int getSize() {
    return data.size();
  }

  @Override
  public ActionObj getElementAt(int index) {
    return data.get(index);
  }

  public void setData(@NotNull List<ActionObj> actionList) {
    clear();
    data.addAll(actionList);
    Collections.sort(data, new GroupComparator(groupByOrganizer));
    int index1 = data.size() - 1;
    if (index1 >= 0) fireIntervalAdded(this, 0, index1);
  }

  public void clear() {
    int index1 = data.size() - 1;
    data.clear();
    if (index1 >= 0) fireIntervalRemoved(this, 0, index1);
  }

  public void setGroupByOrganizer(boolean groupByOrganizer) {
    if (this.groupByOrganizer == groupByOrganizer) return;
    this.groupByOrganizer = groupByOrganizer;
    Collections.sort(data, new GroupComparator(groupByOrganizer));
    fireContentsChanged(this, 0, data.size() - 1);
  }

  private static class GroupComparator implements Comparator<ActionObj> {
    private final boolean groupByOrganizer;

    private GroupComparator(boolean groupByOrganizer) {
      this.groupByOrganizer = groupByOrganizer;
    }

    @Override
    public int compare(ActionObj o1, ActionObj o2) {
      if (groupByOrganizer) {
        int comp = -Long.compare(o1.getOrganizerId(), o2.getOrganizerId());
        if (comp == 0) comp = -Long.compare(o1.getId(), o2.getId());
        return comp;
      } else {
        return -Long.compare(o1.getId(), o2.getId());
      }
    }
  }
}
