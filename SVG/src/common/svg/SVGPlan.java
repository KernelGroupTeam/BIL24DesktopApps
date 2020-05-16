package common.svg;

import java.io.*;
import java.math.*;
import java.text.NumberFormat;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.08.15
 */
public abstract class SVGPlan {
  private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  private static final NumberFormat priceFormat = NumberFormat.getInstance(new Locale("ru", "RU"));
  public static final BigDecimal VER_10 = new BigDecimal("1.0");
  public static final BigDecimal VER_11 = new BigDecimal("1.1");
  static final String ID_CATEGORY = "PriceCategory";
  static final String ID_CATEGORY_TEXT = "PriceCategoryText";
  static final String ID_LEGEND = "Legend";
  static final String ID_STATE_NONE = "None";
  static final String ID_STATE_RESERVED = "Reserved";
  static final String ID_STATE_SOLD = "Sold";
  static final String ID_STATE_MY = "MyTickets";
  static final Set<String> RESERVED_ID = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ID_CATEGORY, ID_CATEGORY_TEXT, ID_LEGEND, ID_STATE_NONE, ID_STATE_RESERVED, ID_STATE_SOLD, ID_STATE_MY)));
  static final String CLASS_STATE_NONE = "st1";
  static final String CLASS_STATE_RESERVED = "st2";
  static final String CLASS_STATE_SOLD = "st3";
  static final String CLASS_STATE_MY = "st4";
  static final String CLASS_CAT = "cat";
  static final String CLASS_UNUSED = "unused";
  static final String VAR_PRICE = "%price%";

  static {
    documentBuilderFactory.setIgnoringComments(true);
  }

  @NotNull
  protected final byte[] svgData;

  public SVGPlan(@NotNull byte[] svgData) {
    this.svgData = svgData;
  }

  @NotNull
  public byte[] getSvgData() {
    return svgData;
  }

  @NotNull
  public abstract BigDecimal getVersion();

  public abstract boolean isProcessed();

  @NotNull
  public abstract byte[] getProcessedSvgData() throws SVGPlanException;

  @NotNull
  protected Document createDocument() throws IOException, SVGPlanException {
    try {
      DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
      return builder.parse(new ByteArrayInputStream(svgData));
    } catch (ParserConfigurationException | SAXException e) {
      throw new SVGPlanException("SVG document parsing error", e);
    }
  }

  @NotNull
  protected Document cloneDocument(Document doc) throws SVGPlanException {
    try {
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      DOMResult result = new DOMResult();
      transformer.transform(source, result);
      return (Document) result.getNode();
    } catch (TransformerException e) {
      throw new SVGPlanException("SVG document cloning error", e);
    }
  }

  @NotNull
  protected byte[] toByteArray(Document doc) throws SVGPlanException {
    return toByteArray(doc, false);
  }

  @NotNull
  static byte[] toByteArray(Document doc, boolean addIndent) throws SVGPlanException {
    try {
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      if (addIndent) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
      }
      transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "style");
      DOMSource source = new DOMSource(doc);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(outputStream);
      transformer.transform(source, result);
      return outputStream.toByteArray();
    } catch (TransformerException e) {
      throw new SVGPlanException("SVG document transforming error", e);
    }
  }

  protected void removeWhitespaces(Document doc) {
    removeWhitespaces((Node) doc);
  }

  private void removeWhitespaces(Node node) {
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = len - 1; i >= 0; i--) {
      Node currentNode = nodeList.item(i);
      if (currentNode.getNodeType() == Node.TEXT_NODE) {
        if (currentNode.getNodeValue().trim().isEmpty()) currentNode.getParentNode().removeChild(currentNode);
      } else if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        removeWhitespaces(currentNode);
      }
    }
  }

  protected void clean(Document doc) {
    clean((Node) doc);
  }

  private void clean(Node node) {
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = len - 1; i >= 0; i--) {
      Node currentNode = nodeList.item(i);
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) currentNode;
        NamedNodeMap attrMap = element.getAttributes();
        int attrLen = attrMap.getLength();
        for (int j = attrLen - 1; j >= 0; j--) {
          Attr attr = (Attr) attrMap.item(j);
          String attrName = attr.getName();
          if (attrName.startsWith("inkscape:") || attrName.startsWith("sodipodi:") ||
              attrName.equals("tc-sector-name") || attrName.equals("tc-row-no") ||
              attrName.equals("tc-seat-no")) element.removeAttributeNode(attr);
        }
        clean(currentNode);
      }
    }
  }

  protected void roundValues(Document doc, int scale) {
    roundValues((Node) doc, scale);
  }

  private void roundValues(Node node, int scale) {
    NodeList nodeList = node.getChildNodes();
    int len = nodeList.getLength();
    for (int i = len - 1; i >= 0; i--) {
      Node currentNode = nodeList.item(i);
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) currentNode;
        NamedNodeMap attrMap = element.getAttributes();
        int attrLen = attrMap.getLength();
        for (int j = attrLen - 1; j >= 0; j--) {
          Attr attr = (Attr) attrMap.item(j);
          String attrName = attr.getName();
          if (attrName.equals("cx") || attrName.equals("cy") || attrName.equals("r") ||
              attrName.equals("x") || attrName.equals("y")) {
            String attrValue = attr.getValue();
            try {
              BigDecimal val = new BigDecimal(attrValue);
              if (val.scale() > scale) {
                val = val.setScale(scale, RoundingMode.HALF_UP);
                attr.setValue(val.toString());
              }
            } catch (NumberFormatException ignored) {
            }
          }
        }
        roundValues(currentNode, scale);
      }
    }
  }

  //убираем стили, которые по умолчанию имеют такие же значения
  //возможные проблемы: стиль по умолчанию переопределяет такой же стиль, объявленный ранее
  protected void removeDefaultStyles(Document doc) {
    List<Element> elementList = SVGParser.getElementListByAttrPresent(doc, "style");
    for (Element element : elementList) {
      String style = element.getAttribute("style");
      style = style.replace("fill-opacity:1", "").replace("stroke-opacity:1", "");
      style = style.replace("stroke-linecap:butt", "").replace("stroke-linejoin:miter", "");
      style = style.replace("stroke-dasharray:none", "").replace("stroke-miterlimit:4", "");
      style = style.replace("font-style:normal", "").replace("font-variant:normal", "");
      style = style.replace("font-weight:normal", "").replace("font-stretch:normal", "");
      style = style.replace("writing-mode:lr-tb", "").replace("text-anchor:start", "");
      style = style.replace("letter-spacing:0px", "").replace("word-spacing:0px", "");
      style = style.replace("display:inline", "");
      StringBuilder str = new StringBuilder(style);
      int index;
      while ((index = str.indexOf("-inkscape-font-specification")) != -1) {
        int endIndex = str.indexOf(";", index);
        if (endIndex == -1) endIndex = str.length();
        str.replace(index, endIndex, "");
      }
      while ((index = str.indexOf(";;")) != -1) {
        str.deleteCharAt(index);
      }
      if (str.length() > 0 && str.charAt(0) == ';') str.deleteCharAt(0);
      if (str.length() > 0 && str.charAt(str.length() - 1) == ';') str.deleteCharAt(str.length() - 1);
      if (str.length() > 0) element.setAttribute("style", str.toString());
      else element.removeAttribute("style");
    }
  }

  protected void renameIds(Document doc) {
    int num = 1;
    List<Element> elementList = SVGParser.getElementListByAttrPresent(doc, "id");
    for (Element element : elementList) {
      String id = element.getAttribute("id");
      if (RESERVED_ID.contains(id)) continue;
      element.setAttribute("id", "id" + Integer.toString(num, Character.MAX_RADIX));
      num++;
    }
  }

  protected void removeIds(Document doc) {
    List<Element> elementList = SVGParser.getElementListByAttrPresent(doc, "id");
    for (Element element : elementList) {
      String id = element.getAttribute("id");
      if (RESERVED_ID.contains(id)) continue;
      element.removeAttribute("id");
    }
  }

  protected void setIds(Document doc) {
    List<Element> elementList = SVGParser.getElementListByAttrPresent(doc, "sbt:seat");
    int counter = 0;
    for (Element element : elementList) {
      counter++;
      element.setAttribute("id", "s" + Integer.toString(counter, Character.MAX_RADIX));
    }
  }

  @NotNull
  static String priceFormat(BigDecimal price) {
    String priceStr;
    synchronized (priceFormat) {
      priceStr = priceFormat.format(price);
    }
    return priceStr;
  }

  @NotNull
  public static byte[] svgToSvgIndentXML(byte[] svgData) throws SVGPlanException {
    try {
      DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(svgData));
      return toByteArray(doc, true);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new SVGPlanException("SVG document transforming error", e);
    }
  }
}
