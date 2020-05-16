package common.svg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.apache.batik.swing.svg.JSVGComponent;
import org.jetbrains.annotations.*;
import org.w3c.dom.*;
import org.w3c.dom.svg.SVGDocument;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.12.16
 */
public class SVGDocVenue extends SVGDoc {
  private final boolean combined;
  @NotNull
  private final SVGDocument document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final Map<String, Element> idPriceMap = new HashMap<>();//categoryId -> priceElement

  public SVGDocVenue(@NotNull byte[] svgData, boolean combined) throws IOException, SVGPlanException {
    super(svgData);
    this.combined = combined;
    document = createDocument(svgData);

    Element svgElement = SVGParser.getUniqueChildElement(document, "svg");
    if (svgElement == null) throw new SVGPlanException("<svg> is not found");

    Element metaElement = SVGParser.getUniqueChildElement(svgElement, "metadata");
    if (metaElement == null) throw new SVGPlanException("<metadata> is not found");
    Element docElement = SVGParser.getUniqueChildElement(metaElement, "sbt:document");
    if (docElement == null) throw new SVGPlanException("<sbt:document> is not found");
    version = new BigDecimal(docElement.getAttributeNS(SBT_NAMESPACE_URI, "version"));
    Element categoriesElement = SVGParser.getUniqueChildElement(metaElement, "sbt:categories");
    if (categoriesElement == null) throw new SVGPlanException("<sbt:categories> is not found");
    Element priceListElement = SVGParser.getElementById(document, "text", SVGPlan.ID_CATEGORY_TEXT);
    if (priceListElement == null) throw new SVGPlanException("Список цен не найден");
    List<Element> catList = SVGParser.getChildElementList(categoriesElement, "sbt:category");
    if (catList.isEmpty()) throw new SVGPlanException("Не найдено ни одной категории");
    for (Element catElement : catList) {
      String id = catElement.getAttributeNS(SBT_NAMESPACE_URI, "id");
      Element priceElement = SVGParser.getUniqueElementByAttrNS(priceListElement, SBT_NAMESPACE_URI, "id", id);
      if (priceElement == null) throw new SVGPlanException("price element id=" + id + " is not found");
      idPriceMap.put(id, priceElement);
    }
  }

  @NotNull
  @Override
  public SVGDocument getDocument() {
    return document;
  }

  @Override
  public int getDocumentState() {
    return JSVGComponent.ALWAYS_INTERACTIVE;
  }

  @NotNull
  @Override
  public BigDecimal getVersion() {
    return version;
  }

  @Override
  public boolean isCombined() {
    return combined;
  }

  public void setPrice(long categoryId, @Nullable BigDecimal price) throws SVGPlanException {
    if (price == null) return;
    Element priceElement = idPriceMap.get(String.valueOf(categoryId));
    if (priceElement == null) throw new SVGPlanException("categoryId=" + categoryId + " is not found");
    Text priceText = SVGParser.getUniqueChildText(priceElement);
    if (priceText == null) throw new SVGPlanException("price place is not found");
    String priceTextStr = priceText.getNodeValue();
    priceTextStr = priceTextStr.replace(SVGPlan.VAR_PRICE, SVGPlan.priceFormat(price));
    priceText.setNodeValue(priceTextStr);
  }
}
