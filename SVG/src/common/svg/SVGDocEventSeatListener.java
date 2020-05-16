package common.svg;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 23.12.16
 */
public interface SVGDocEventSeatListener extends EventListener {

  void seatClicked(long seatId, boolean shiftDown, boolean controlDown, boolean altDown);
}
