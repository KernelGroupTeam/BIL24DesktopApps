package server.protocol2.cassa;

import java.math.BigDecimal;

import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 19.10.2018
 * Если у ценовых категорий один и тот же идентификатор лимита ценовой категории
 * значит значение availability для них единое
 * Например:
 * Ценовая категория 1 - categoryPriceLimitId = null, availability = 100
 * Ценовая категория 2 - categoryPriceLimitId = 1, availability = 200
 * Ценовая категория 3 - categoryPriceLimitId = 2, availability = 300
 * Ценовая категория 4 - categoryPriceLimitId = 2, availability = 300
 * При покупке 2-х мест в ценовой категории 1 значение availability станет 98 только для ценовой категории 1
 * При покупке 2-х мест в ценовой категории 2 значение availability станет 198 только для ценовой категории 2
 * При покупке 2-х мест в ценовой категории 3 значение availability станет 298 для ценовой категории 3 и ценовой категории 4
 * При покупке 2-х мест в ценовой категории 4 значение availability станет 298 для ценовой категории 3 и ценовой категории 4
 */
public class CassaCategoryPriceActual extends CassaCategoryPrice {
  private static final long serialVersionUID = 1394049883499119545L;
  @Nullable
  private Long categoryPriceLimitId;//Идентификатор лимита ценовой категории. Лимиты по местам для безместовых категорий
  private int availability;//Количество свободных(доступных) к продаже мест

  public CassaCategoryPriceActual(long id, @NotNull String name, @NotNull BigDecimal price, boolean placement,
                                  @Nullable Long categoryPriceLimitId, int availability) {
    super(id, name, price, placement);
    this.categoryPriceLimitId = categoryPriceLimitId;
    this.availability = availability;
  }

  @Nullable
  public Long getCategoryPriceLimitId() {
    return categoryPriceLimitId;
  }

  public int getAvailability() {
    return availability;
  }

  @Override
  public String toString() {
    return "CassaCategoryPriceActual{id=" + getId() + ", name=" + getName() + ", price=" + getPrice() + ", placement=" + isPlacement() + ", categoryPriceLimitId=" + categoryPriceLimitId + ", availability=" + availability + '}';
  }
}
