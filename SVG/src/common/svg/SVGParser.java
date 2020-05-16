package common.svg;

import java.awt.geom.*;
import java.math.BigDecimal;
import java.util.*;

import org.apache.batik.dom.svg.*;
import org.apache.batik.parser.*;
import org.jetbrains.annotations.*;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 27.08.15
 */
class SVGParser {
  private static final String ATTR_ID = "id";
  private static final String ATTR_CLASS = "class";
  private static final String ATTR_STYLE = "style";
  private static final String ATTR_TRANSFORM = "transform";
  private static final String ATTR_CX = "cx";
  private static final String ATTR_CY = "cy";
  private static final String ATTR_R = "r";

  private SVGParser() {
  }

  @Nullable
  public static Element getElementById(@NotNull Document document, @NotNull String tagName, @NotNull String value) {
    return getElementById(document.getDocumentElement(), tagName, value);
  }

  @Nullable
  public static Element getElementById(@NotNull Element element, @NotNull String tagName, @NotNull String value) {
    NodeList nodeList = element.getElementsByTagName(tagName);
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element childElement = (Element) node;
      if (childElement.getAttribute(ATTR_ID).equals(value)) return childElement;
    }
    return null;
  }

  @NotNull
  public static List<Element> getChildElementList(@NotNull Node node) {
    return getChildElementList(node, null);
  }

  @NotNull
  public static List<Element> getChildElementList(@NotNull Node node, @Nullable String tagName) {
    List<Element> result = new ArrayList<>();
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node childNode = nodeList.item(i);
      if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
      Element element = (Element) childNode;
      if (tagName != null && !tagName.equals(element.getTagName())) continue;
      result.add(element);
    }
    return result;
  }

  @NotNull
  public static List<CDATASection> getChildCDATAList(@NotNull Node node) {
    List<CDATASection> result = new ArrayList<>();
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node childNode = nodeList.item(i);
      if (childNode.getNodeType() != Node.CDATA_SECTION_NODE) continue;
      CDATASection element = (CDATASection) childNode;
      result.add(element);
    }
    return result;
  }

  @Nullable
  public static Element getUniqueChildElement(@NotNull Node node) {
    return getUniqueChildElement(node, null);
  }

  @Nullable
  public static Element getUniqueChildElement(@NotNull Node node, @Nullable String tagName) {
    Element result = null;
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node childNode = nodeList.item(i);
      if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
      Element element = (Element) childNode;
      if (tagName != null && !tagName.equals(element.getTagName())) continue;
      if (result != null) return null;//не уникальный
      result = element;
    }
    return result;
  }

  @NotNull
  public static List<Element> getElementListByAttrPresent(@NotNull Document document, @NotNull String attr) {
    return getElementListByAttrPresent(document.getDocumentElement(), attr);
  }

  @NotNull
  public static List<Element> getElementListByAttrNSPresent(@NotNull Document document, @NotNull String namespaceURI, @NotNull String attr) {
    return getElementListByAttrNSPresent(document.getDocumentElement(), namespaceURI, attr);
  }

  @NotNull
  public static List<Element> getElementListByAttrPresent(@NotNull Element element, @NotNull String attr) {
    NodeList nodeList = element.getElementsByTagName("*");
    List<Element> result = new ArrayList<>();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element childElement = (Element) node;
      if (childElement.hasAttribute(attr)) result.add(childElement);
    }
    return result;
  }

  @NotNull
  public static List<Element> getElementListByAttrNSPresent(@NotNull Element element, @NotNull String namespaceURI, @NotNull String attr) {
    NodeList nodeList = element.getElementsByTagName("*");
    List<Element> result = new ArrayList<>();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) continue;
      Element childElement = (Element) node;
      if (childElement.hasAttributeNS(namespaceURI, attr)) result.add(childElement);
    }
    return result;
  }

  @Nullable
  public static Element getUniqueElementByAttr(@NotNull Element element, @NotNull String attr, @NotNull String value) {
    Element result = null;
    List<Element> elementList = getElementListByAttrPresent(element, attr);
    for (Element childElement : elementList) {
      if (childElement.getAttribute(attr).equals(value)) {
        if (result != null) return null;//не уникальный
        result = childElement;
      }
    }
    return result;
  }

  @Nullable
  public static Element getUniqueElementByAttrNS(@NotNull Element element, @NotNull String namespaceURI, @NotNull String attr, @NotNull String value) {
    Element result = null;
    List<Element> elementList = getElementListByAttrNSPresent(element, namespaceURI, attr);
    for (Element childElement : elementList) {
      if (childElement.getAttributeNS(namespaceURI, attr).equals(value)) {
        if (result != null) return null;//не уникальный
        result = childElement;
      }
    }
    return result;
  }

  @Nullable
  public static Text getUniqueChildText(@NotNull Node node) {
    Text result = null;
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      Node childNode = nodeList.item(i);
      if (childNode.getNodeType() != Node.TEXT_NODE) continue;
      Text text = (Text) childNode;
      if (result != null) return null;//не уникальный
      result = text;
    }
    return result;
  }

  @NotNull
  public static List<Node> toList(@NotNull NodeList nodeList) {
    List<Node> result = new ArrayList<>();
    int len = nodeList.getLength();
    for (int i = 0; i < len; i++) {
      result.add(nodeList.item(i));
    }
    return result;
  }

  @Nullable
  public static String getStyleByName(@NotNull Element element, @NotNull String name) {
    String style = element.getAttribute(ATTR_STYLE);
    if (style.isEmpty()) return null;
    String value = null;
    String[] styles = style.split(";");
    for (String s : styles) {
      if (s.startsWith(name)) {
        String[] parts = s.split(":", 2);
        if (parts.length < 2) continue;
        if (!parts[0].trim().equals(name)) continue;
        value = parts[1].trim();
      }
    }
    return value;
  }

  public static boolean removeStyleByName(@NotNull Element element, @NotNull String name) {
    String style = element.getAttribute(ATTR_STYLE);
    if (style.isEmpty()) return false;
    boolean result = false;
    StringBuilder str = new StringBuilder();
    String[] styles = style.split(";");
    for (String s : styles) {
      if (s.startsWith(name)) {
        String[] parts = s.split(":", 2);
        if (parts.length == 2) {
          if (parts[0].trim().equals(name)) {
            result = true;
            continue;
          }
        }
      }
      str.append(s).append(";");
    }
    if (str.length() > 0 && str.charAt(str.length() - 1) == ';') str.deleteCharAt(str.length() - 1);
    element.setAttribute(ATTR_STYLE, str.toString());
    return result;
  }

  public static void setStyleByName(@NotNull Element element, @NotNull String name, @NotNull String value) {
    removeStyleByName(element, name);
    String style = element.getAttribute(ATTR_STYLE);
    if (!style.isEmpty()) style += ";";
    style += name + ":" + value;
    element.setAttribute(ATTR_STYLE, style);
  }

  public static void addClassName(@NotNull Element element, @NotNull String value) {
    String classValue = element.getAttribute(ATTR_CLASS);
    if (classValue.isEmpty()) classValue = value;
    else classValue += " " + value;
    element.setAttribute(ATTR_CLASS, classValue);
  }

  public static void addFirstClassName(@NotNull Element element, @NotNull String value) {
    String classValue = element.getAttribute(ATTR_CLASS);
    if (classValue.isEmpty()) classValue = value;
    else classValue = value + " " + classValue;
    element.setAttribute(ATTR_CLASS, classValue);
  }

  public static void replaceClassName(@NotNull Element element, @NotNull String oldValue, @NotNull String newValue) {
    String classValue = element.getAttribute(ATTR_CLASS);
    classValue = classValue.replace(oldValue, newValue);
    element.setAttribute(ATTR_CLASS, classValue);
  }

  public static void replaceFirstClassName(@NotNull Element element, @NotNull String oldValue, @NotNull String newValue) {
    String classValue = element.getAttribute(ATTR_CLASS);
    int index = classValue.indexOf(oldValue);
    if (index == -1) return;
    classValue = classValue.substring(0, index) + newValue + classValue.substring(index + oldValue.length());
    element.setAttribute(ATTR_CLASS, classValue);
  }

  public static boolean removeClassCat(@NotNull Element element) {
    String classValue = element.getAttribute(ATTR_CLASS);
    if (classValue.isEmpty()) return false;
    boolean result = false;
    StringBuilder str = new StringBuilder();
    String[] classes = classValue.split(" ");
    for (String s : classes) {
      if (s.matches("cat\\d+")) {
        result = true;
        continue;
      }
      str.append(s).append(" ");
    }
    element.setAttribute(ATTR_CLASS, str.toString().trim());
    return result;
  }

  public static void sortVertically(@NotNull List<Element> elementList, @NotNull String yAttr) {
    TreeMap<Float, Element> map = new TreeMap<>();
    for (Element element : elementList) {
      String yAttrValue = element.getAttribute(yAttr);
      float y;
      try {
        y = Float.parseFloat(yAttrValue);
      } catch (NumberFormatException e) {
        map.put(Float.MAX_VALUE, element);
        continue;
      }
      List<SVGTransform> transformList = getDeepSVGTransformList(element);
      SVGMatrix matrix = getSVGMatrix(transformList);
      if (matrix == null) {
        map.put(y, element);
        continue;
      }
      SVGPoint point = new SVGOMPoint(0.0f, y);
      SVGPoint transformPoint = point.matrixTransform(matrix);
      map.put(transformPoint.getY(), element);
    }
    elementList.clear();
    for (Map.Entry<Float, Element> entry : map.entrySet()) {
      elementList.add(entry.getValue());
    }
  }

  @Nullable
  public static SVGMatrix getSVGMatrix(@NotNull List<SVGTransform> transformList) {
    if (transformList.isEmpty()) return null;
    if (transformList.size() == 1) return transformList.get(0).getMatrix();
    SVGMatrix result = transformList.get(0).getMatrix();
    for (int i = 1; i < transformList.size(); i++) {
      SVGTransform transform = transformList.get(i);
      result = result.multiply(transform.getMatrix());
    }
    return result;
  }

  @NotNull
  public static List<SVGTransform> getSVGTransformList(@NotNull Element element) {
    String transformValue = element.getAttribute(ATTR_TRANSFORM);
    if (transformValue.isEmpty()) return Collections.emptyList();
    List<SVGTransform> result = new ArrayList<>();
    TransformListBuilder builder = new TransformListBuilder(result);
    TransformListParser transformListParser = new TransformListParser();
    transformListParser.setTransformListHandler(builder);
    transformListParser.parse(transformValue);
    return result;
  }

  @NotNull
  public static List<SVGTransform> getDeepSVGTransformList(@NotNull Element element) {
    List<SVGTransform> result = new ArrayList<>(getSVGTransformList(element));
    Node parentNode = element.getParentNode();
    if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
      result.addAll(0, getDeepSVGTransformList((Element) parentNode));
    }
    return result;
  }

  @NotNull
  public static BigDecimal[] getAbsCoordinatesCircle(@NotNull Element element) {
    if (!element.getTagName().equals("circle")) throw new IllegalArgumentException();
    String cxAttr = element.getAttribute(ATTR_CX);
    String cyAttr = element.getAttribute(ATTR_CY);
    float cx = (cxAttr.isEmpty() ? 0.0f : Float.parseFloat(cxAttr));
    float cy = (cyAttr.isEmpty() ? 0.0f : Float.parseFloat(cyAttr));
    SVGMatrix matrix = getSVGMatrix(getDeepSVGTransformList(element));
    BigDecimal x;
    BigDecimal y;
    if (matrix == null) {
      x = new BigDecimal(String.valueOf(cx));
      y = new BigDecimal(String.valueOf(cy));
    } else {
      SVGPoint newCenter = new SVGOMPoint(cx, cy).matrixTransform(matrix);
      x = new BigDecimal(String.valueOf(newCenter.getX()));
      y = new BigDecimal(String.valueOf(newCenter.getY()));
    }
    return new BigDecimal[]{x, y};
  }

  public static boolean applyTransformCircle(@NotNull Element element) {
    if (!element.getTagName().equals("circle")) return false;
    SVGMatrix matrix = getSVGMatrix(getSVGTransformList(element));
    if (matrix == null) return false;
    //если искажение формы круга
    if (Math.abs(Math.abs(matrix.getA()) - Math.abs(matrix.getD())) > 0.0000001) return false;
    String cxAttr = element.getAttribute(ATTR_CX);
    String cyAttr = element.getAttribute(ATTR_CY);
    String rAttr = element.getAttribute(ATTR_R);
    float cx = (cxAttr.isEmpty() ? 0.0f : Float.parseFloat(cxAttr));
    float cy = (cyAttr.isEmpty() ? 0.0f : Float.parseFloat(cyAttr));
    float r = (rAttr.isEmpty() ? 0.0f : Float.parseFloat(rAttr));

    SVGPoint newCenter = new SVGOMPoint(cx, cy).matrixTransform(matrix);
    SVGPoint newRadius = new SVGOMPoint(cx + r, cy).matrixTransform(matrix);
    float newR = (float) Point2D.distance(newCenter.getX(), newCenter.getY(), newRadius.getX(), newRadius.getY());
    element.setAttribute(ATTR_CX, String.valueOf(newCenter.getX()));
    element.setAttribute(ATTR_CY, String.valueOf(newCenter.getY()));
    element.setAttribute(ATTR_R, String.valueOf(newR));
    String strokeWidthStyle = getStyleByName(element, "stroke-width");
    if (strokeWidthStyle != null) {
      float strokeWidth = Float.parseFloat(strokeWidthStyle);
      float halfStrokeWidth = strokeWidth / 2;
      SVGPoint point1 = new SVGOMPoint(cx + r - halfStrokeWidth, cy).matrixTransform(matrix);
      SVGPoint point2 = new SVGOMPoint(cx + r + halfStrokeWidth, cy).matrixTransform(matrix);
      float newStrokeWidth = (float) Point2D.distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
      float factor = newStrokeWidth / strokeWidth;
      //для лучшей оптимизации меняем ширину, только если она изменилась более чем в 2 раза
      if (factor < 0.5f || factor > 2.0f) setStyleByName(element, "stroke-width", String.valueOf(newStrokeWidth));
    }
    element.removeAttribute(ATTR_TRANSFORM);
    return true;
  }

  private static String toString(SVGMatrix m) {
    if (m == null) return null;
    return "[" + m.getA() + ", " + m.getB() + ", " + m.getC() + ", " + m.getD() + ", " + m.getE() + ", " + m.getF() + "]";
  }

  private static class TransformListBuilder implements TransformListHandler {
    @NotNull
    private final List<SVGTransform> list;

    private TransformListBuilder(@NotNull List<SVGTransform> list) {
      this.list = list;
    }

    @Override
    public void startTransformList() throws ParseException {
      list.clear();
    }

    @Override
    public void matrix(float a, float b, float c, float d, float e, float f) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setMatrix(new SVGOMMatrix(new AffineTransform(a, b, c, d, e, f)));
      list.add(transform);
    }

    @Override
    public void rotate(float theta) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setRotate(theta, 0.0f, 0.0f);
      list.add(transform);
    }

    @Override
    public void rotate(float theta, float cx, float cy) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setRotate(theta, cx, cy);
      list.add(transform);
    }

    @Override
    public void translate(float tx) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setTranslate(tx, 0.0f);
      list.add(transform);
    }

    @Override
    public void translate(float tx, float ty) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setTranslate(tx, ty);
      list.add(transform);

    }

    @Override
    public void scale(float sx) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setScale(sx, sx);
      list.add(transform);
    }

    @Override
    public void scale(float sx, float sy) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setScale(sx, sy);
      list.add(transform);
    }

    @Override
    public void skewX(float skx) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setSkewX(skx);
      list.add(transform);
    }

    @Override
    public void skewY(float sky) throws ParseException {
      SVGOMTransform transform = new SVGOMTransform();
      transform.setSkewY(sky);
      list.add(transform);
    }

    @Override
    public void endTransformList() throws ParseException {
    }
  }
}