package common.svg;

import java.util.*;

import org.jetbrains.annotations.*;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 31.08.15
 */
class SVGCategories {
  @NotNull
  private final LinkedHashMap<SVGColor, SVGCategory> categoryMap = new LinkedHashMap<>();
  @NotNull
  private final SVGCategory noneCategory;

  public SVGCategories(Document document, SVGColor noneColor, boolean replacePriceVar) throws SVGPlanException {
    noneCategory = new SVGCategory(noneColor);
    noneCategory.setIndex(0);
    Element categoryElement = SVGParser.getElementById(document, "g", SVGPlan.ID_CATEGORY);
    if (categoryElement == null) throw new SVGPlanException("Список категорий не найден");

    //получаем лейблы категорий и упорядочиваем их по вертикали
    List<Element> catList = SVGParser.getChildElementList(categoryElement, "circle");
    SVGParser.sortVertically(catList, "cy");
    for (Element element : catList) {
      String name = element.getAttribute("inkscape:label");
      if (name.length() < 2) continue;
      name = name.substring(1);

      String fillColor = SVGParser.getStyleByName(element, "fill");
      if (fillColor == null) continue;
      SVGColor color = new SVGColor(fillColor);

      if (categoryMap.containsKey(color)) throw new SVGPlanException("Дублирующая категория, цвет: " + color);
      categoryMap.put(color, new SVGCategory(element, name, color));
    }

    //проверка мест для вывода стоимости категории
    Element textElement = SVGParser.getElementById(categoryElement, "text", SVGPlan.ID_CATEGORY_TEXT);
    if (textElement == null) throw new SVGPlanException("Список цен не найден");

    List<Element> textList = SVGParser.getChildElementList(textElement, "tspan");
    for (Element element : textList) {
      String name = element.getAttribute("inkscape:label");
      if (name.length() < 2) continue;
      name = name.substring(1);

      for (SVGCategory svgCategory : categoryMap.values()) {
        if (svgCategory.getName().equals(name)) {
          if (svgCategory.getPriceElement() != null) throw new SVGPlanException("Дублирующая цена категории, название: " + name);
          svgCategory.setPriceElement(element, replacePriceVar);
          break;
        }
      }
    }

    //удаляем все категории, где нет места для цены... получаем окончательный список ценовых категорий
    Iterator<Map.Entry<SVGColor, SVGCategory>> iterator = categoryMap.entrySet().iterator();
    while (iterator.hasNext()) {
      SVGCategory svgCategory = iterator.next().getValue();
      if (svgCategory.getPriceElement() == null) iterator.remove();
    }

    //проверяем задан ли порядок категорий в документе
    boolean ordering = true;
    for (Map.Entry<SVGColor, SVGCategory> entry : categoryMap.entrySet()) {
      SVGCategory svgCategory = entry.getValue();
      Element labelElement = svgCategory.getLabelElement();
      if (labelElement == null) throw new IllegalStateException("label element is null");
      Element labelTitle = SVGParser.getUniqueChildElement(labelElement, "title");
      if (labelTitle == null) {
        ordering = false;
        break;
      }
      String labelOrdinal = labelTitle.getTextContent();
      try {
        int index = Integer.parseInt(labelOrdinal);
        svgCategory.setIndex(index);
      } catch (NumberFormatException e) {
        ordering = false;
        break;
      }
    }

    //если порядок задан, переупорядочиваем
    if (ordering) {
      List<SVGCategory> categoryList = new ArrayList<>(categoryMap.values());
      Collections.sort(categoryList, new Comparator<SVGCategory>() {
        @Override
        public int compare(SVGCategory o1, SVGCategory o2) {
          return Integer.compare(o1.getIndex(), o2.getIndex());
        }
      });
      categoryMap.clear();
      for (SVGCategory svgCategory : categoryList) {
        categoryMap.put(svgCategory.getColor(), svgCategory);
      }
    }

    //проставляем индексы в категориях
    int index = 1;
    for (Map.Entry<SVGColor, SVGCategory> entry : categoryMap.entrySet()) {
      SVGCategory svgCategory = entry.getValue();
      svgCategory.setIndex(index);
      svgCategory.updateIndex();
      svgCategory.updateOrdinal();
      svgCategory.updateLabelClass();
      index++;
    }
  }

  @NotNull
  public SVGCategory getNoneCategory() {
    return noneCategory;
  }

  @NotNull
  public List<SVGCategory> getCategoryList() {
    return new ArrayList<>(categoryMap.values());
  }

  @Nullable
  public SVGCategory get(SVGColor color) {
    return categoryMap.get(color);
  }

  @NotNull
  public String getStyle() {
    StringBuilder result = new StringBuilder();
    addStyle(result, noneCategory);
    for (SVGCategory svgCategory : categoryMap.values()) {
      addStyle(result, svgCategory);
    }
    result.append(".unused {visibility:hidden !important}\n");
    return result.toString();
  }

  private void addStyle(StringBuilder result, SVGCategory svgCategory) {
    result.append(".").append(svgCategory.getClassName()).append(" {fill:").append(svgCategory.getColor()).append(";stroke:#000000}\n");
  }
}
