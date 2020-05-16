package server.protocol2.cassa;

import java.io.Serializable;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import server.protocol2.common.KindObj;

/**
 * Created by Inventor on 13.10.2018
 */
public class CassaAction<E extends CassaActionEvent> implements Serializable {
  private static final long serialVersionUID = -6917140503379807959L;
  private long id;//Идентификатор представления
  @NotNull
  private String name;//Название представления
  @NotNull
  private KindObj kind;//Раздел представления
  @NotNull
  private String legalOwner;//Устроитель представления
  @NotNull
  private String legalOwnerInn;//ИНН устроителя
  @NotNull
  private String legalOwnerPhone;//Телефон устроителя
  @NotNull
  private String age;//Возрастное ограничение представления
  @NotNull
  private String smallPosterUrl;//Ссылка на маленький постер представления
  @Deprecated
  @NotNull
  private String organizerInn;//ИНН организатора
  @NotNull
  private List<E> actionEventList;//Список сеансов представления

  public CassaAction(long id, @NotNull String name, @NotNull KindObj kind, @NotNull String legalOwner,
                     @NotNull String legalOwnerInn, @NotNull String legalOwnerPhone, @NotNull String age,
                     @NotNull String smallPosterUrl, @NotNull String organizerInn, @NotNull List<E> actionEventList) {
    this.id = id;
    this.name = name;
    this.kind = kind;
    this.legalOwner = legalOwner;
    this.legalOwnerInn = legalOwnerInn;
    this.legalOwnerPhone = legalOwnerPhone;
    this.age = age;
    this.smallPosterUrl = smallPosterUrl;
    this.organizerInn = organizerInn;
    this.actionEventList = actionEventList;
  }

  public final long getId() {
    return id;
  }

  @NotNull
  public final String getName() {
    return name;
  }

  @NotNull
  public final KindObj getKind() {
    return kind;
  }

  @NotNull
  public final String getLegalOwner() {
    return legalOwner;
  }

  @NotNull
  public String getLegalOwnerInn() {
    return legalOwnerInn;
  }

  @NotNull
  public String getLegalOwnerPhone() {
    return legalOwnerPhone;
  }

  @NotNull
  public final String getAge() {
    return age;
  }

  @NotNull
  public final String getSmallPosterUrl() {
    return smallPosterUrl;
  }

  @Deprecated
  @NotNull
  public String getOrganizerInn() {
    return organizerInn;
  }

  @NotNull
  public final List<E> getActionEventList() {
    return actionEventList;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CassaAction<?>)) return false;
    CassaAction<?> that = (CassaAction<?>) o;
    return id == that.id;
  }

  @Override
  public final int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

  @Override
  public String toString() {
    return "CassaAction{id=" + id + ", name=" + name + ", kind=" + kind + ", legalOwner=" + legalOwner +
        ", legalOwnerInn=" + legalOwnerInn + ", legalOwnerPhone=" + legalOwnerPhone + ", age=" + age +
        ", smallPosterUrl=" + smallPosterUrl + ", organizerInn=" + organizerInn + ", actionEventList=" + actionEventList.size() + '}';
  }
}
