package common.svg;

import java.io.*;
import java.math.BigDecimal;

import org.apache.batik.anim.dom.*;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.svg.SVGDocument;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.12.16
 */
public abstract class SVGDoc {
  public static final String SBT_NAMESPACE_URI = "http://www.w3.org/2015/sbt/1.0";
  private final int size;

  public SVGDoc(@NotNull byte[] svgData) {
    size = svgData.length;
  }

  @NotNull
  public abstract SVGDocument getDocument();

  public abstract int getDocumentState();

  @NotNull
  public abstract BigDecimal getVersion();

  public abstract boolean isCombined();

  @NotNull
  public String getVersionDesc() {
    BigDecimal version = getVersion();
    String verStr = "Версия схемы: " + version.toString();
    if (version.compareTo(SVGPlan.VER_11) < 0) verStr += " (управление категориями не поддерживается)";
    if (isCombined()) verStr += " (комбинированная)";
    return verStr;
  }

  public int getSize() {
    return size;
  }

  @NotNull
  public static SVGDocument createEmptyDocument() {
    return (SVGDocument) SVGDOMImplementation.getDOMImplementation().createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
  }

  @NotNull
  protected SVGDocument createDocument(byte[] svgData) throws IOException {
    SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
    return factory.createSVGDocument("", new ByteArrayInputStream(svgData));
  }
}
