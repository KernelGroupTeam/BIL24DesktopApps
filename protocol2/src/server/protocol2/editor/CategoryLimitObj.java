package server.protocol2.editor;

import java.io.Serializable;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.07.16
 */
public class CategoryLimitObj implements Serializable {
  private static final long serialVersionUID = 586076552235507739L;
  private long id;
  @NotNull
  private List<CategoryObj> categoryList = Collections.emptyList();
  private int limit;

  public CategoryLimitObj(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public List<CategoryObj> getCategoryList() {
    return categoryList;
  }

  public void setCategoryList(@NotNull List<CategoryObj> categoryList) {
    this.categoryList = categoryList;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CategoryLimitObj)) return false;
    CategoryLimitObj that = (CategoryLimitObj) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
