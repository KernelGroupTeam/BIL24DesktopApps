package server.protocol2.manager;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.04.19
 */
public interface PromoScope {

  long getId();

  @NotNull
  String getName();

  class ByIdComparator implements Comparator<PromoScope> {
    @Override
    public int compare(PromoScope o1, PromoScope o2) {
      if (o1.getClass() == o2.getClass()) return Long.compare(o1.getId(), o2.getId());
      return o1.getClass().getName().compareTo(o2.getClass().getName());
    }
  }
}
