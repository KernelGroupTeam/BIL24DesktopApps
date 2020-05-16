package client.editor.model;

import java.util.*;
import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import server.protocol2.common.SubsActionObj;
import server.protocol2.editor.ActionObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 25.03.16
 */
public class SubsActionListModel extends AbstractListModel<SubsActionObj> {
  @NotNull
  private final List<SubsActionObj> data = new ArrayList<>();
  private boolean groupByOrganizer = false;

  @Override
  public int getSize() {
    return data.size();
  }

  @Override
  public SubsActionObj getElementAt(int index) {
    return data.get(index);
  }

  public void setData(@NotNull List<SubsActionObj> subsActionList) {
    clear();
    data.addAll(subsActionList);
    Collections.sort(data, new GroupComparator(groupByOrganizer));
    int index1 = data.size() - 1;
    if (index1 >= 0) fireIntervalAdded(this, 0, index1);
  }

  public void addActionList(@NotNull List<ActionObj> actionList) {
    int index0 = data.size();
    for (ActionObj action : actionList) {
      SubsActionObj subsAction = new SubsActionObj(action.getId(), action.getName(), action.getOrganizerId(), action.getOrganizerName(), action.getCityIdSet(), action.getVenueIdSet(), 0);
      if (!data.contains(subsAction)) data.add(subsAction);
    }
    int index1 = data.size() - 1;
    if (index1 >= index0) fireIntervalAdded(this, index0, index1);
  }

  public void remove(@NotNull int[] indices) {
    Arrays.sort(indices);
    for (int i = indices.length - 1; i >= 0; i--) {
      int index = indices[i];
      data.remove(index);
      fireIntervalRemoved(this, index, index);
    }
  }

  public void remove(@NotNull List<SubsActionObj> subsActionList) {
    for (int i = data.size() - 1; i >= 0; i--) {
      SubsActionObj element = data.get(i);
      for (SubsActionObj subsAction : subsActionList) {
        if (element.equals(subsAction)) {
          data.remove(i);
          fireIntervalRemoved(this, i, i);
          break;
        }
      }
    }
  }

  public void clear() {
    int index1 = data.size() - 1;
    data.clear();
    if (index1 >= 0) fireIntervalRemoved(this, 0, index1);
  }

  @NotNull
  public List<SubsActionObj> getSubsActionList() {
    return new ArrayList<>(data);
  }

  public void setGroupByOrganizer(boolean groupByOrganizer) {
    if (this.groupByOrganizer == groupByOrganizer) return;
    this.groupByOrganizer = groupByOrganizer;
    Collections.sort(data, new GroupComparator(groupByOrganizer));
    fireContentsChanged(this, 0, data.size() - 1);
  }

  private static class GroupComparator implements Comparator<SubsActionObj> {
    private final boolean groupByOrganizer;

    private GroupComparator(boolean groupByOrganizer) {
      this.groupByOrganizer = groupByOrganizer;
    }

    @Override
    public int compare(SubsActionObj o1, SubsActionObj o2) {
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
