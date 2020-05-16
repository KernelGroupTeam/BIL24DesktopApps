package common.svg;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.swing.svg.JSVGComponent;
import org.jetbrains.annotations.*;
import org.w3c.dom.Element;
import org.w3c.dom.events.*;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGDocument;

import static common.svg.SVGPlan.CLASS_STATE_MY;
import static common.svg.SVGPlan.CLASS_STATE_NONE;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 22.12.16
 */
public class SVGDocEvent extends SVGDoc {
  private final boolean combined;
  @NotNull
  private final SVGDocument document;
  @NotNull
  private final BigDecimal version;
  @NotNull
  private final Map<Long, Element> seatMap = new HashMap<>();//seatId -> seatElement
  @NotNull
  private Set<Long> ebsNotAvailIdSet = Collections.emptySet();
  private boolean ebsView = false;
  @NotNull
  private Set<Long> selectedSet = Collections.emptySet();
  @Nullable
  private SVGDocEventSeatListener seatListener;

  public SVGDocEvent(@NotNull byte[] svgData, boolean combined) throws IOException, SVGPlanException {
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

    List<Element> seatList = SVGParser.getElementListByAttrNSPresent(document, SBT_NAMESPACE_URI, "seat");
    SeatEventListener seatEventListener = new SeatEventListener();
    for (Element seatElement : seatList) {
      try {
        long seatId = Long.parseLong(seatElement.getAttributeNS(SBT_NAMESPACE_URI, "id"));
        seatMap.put(seatId, seatElement);
        ((EventTarget) seatElement).addEventListener("click", seatEventListener, false);
      } catch (NumberFormatException e) {
        throw new SVGPlanException("sbt:id format", e);
      }
    }
  }

  @NotNull
  @Override
  public SVGDocument getDocument() {
    return document;
  }

  @Override
  public int getDocumentState() {
    return JSVGComponent.ALWAYS_DYNAMIC;
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

  public void setEbsNotAvailIdSet(@NotNull Set<Long> ebsNotAvailIdSet) {
    this.ebsNotAvailIdSet = ebsNotAvailIdSet;
  }

  public void setEbsView(boolean ebsView) {
    if (this.ebsView == ebsView) return;
    this.ebsView = ebsView;
    for (Long seatId : ebsNotAvailIdSet) {
      Element seatElement = seatMap.get(seatId);
      if (seatElement == null) continue;//throw new SVGPlanException("seatId=" + seatId + " is not found");
      if (ebsView) SVGParser.addFirstClassName(seatElement, CLASS_STATE_NONE);
      else SVGParser.replaceFirstClassName(seatElement, CLASS_STATE_NONE, "");
    }
  }

  public void setSeatListener(@Nullable SVGDocEventSeatListener seatListener) {
    this.seatListener = seatListener;
  }

  public void setSelected(@NotNull Set<Long> selectedSeatSet) {
    for (Long seatId : selectedSeatSet) {
      if (!selectedSet.remove(seatId)) {//если не выделено
        Element seatElement = seatMap.get(seatId);
        if (seatElement == null) continue;//throw new SVGPlanException("seatId=" + seatId + " is not found");
        SVGParser.addClassName(seatElement, CLASS_STATE_MY);
      }
    }
    for (Long seatId : selectedSet) {//снять выделение
      Element seatElement = seatMap.get(seatId);
      if (seatElement == null) continue;//throw new SVGPlanException("seatId=" + seatId + " is not found");
      SVGParser.replaceClassName(seatElement, CLASS_STATE_MY, "");
    }
    selectedSet = selectedSeatSet;
  }

  private class SeatEventListener implements EventListener {
    @Override
    public void handleEvent(Event evt) {
      if (evt instanceof DOMMouseEvent && ((DOMMouseEvent) evt).getButton() == 0 && seatListener != null) {
        Element seatElement = (Element) evt.getCurrentTarget();
        try {
          long seatId = Long.parseLong(seatElement.getAttributeNS(SBT_NAMESPACE_URI, "id"));
          DOMMouseEvent event = (DOMMouseEvent) evt;
          seatListener.seatClicked(seatId, event.getShiftKey(), event.getCtrlKey(), event.getAltKey());
        } catch (NumberFormatException ignored) {
        }
      }
    }
  }
}
