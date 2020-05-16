package client.editor.cache;

import client.editor.cache.entity.*;
import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 11.04.16
 */
public class IdleCache implements Cache {
  @Nullable
  @Override
  public VenueBigImage put(@NotNull VenueBigImage image) {
    return null;
  }

  @Nullable
  @Override
  public ActionBigImage put(@NotNull ActionBigImage image) {
    return null;
  }

  @Nullable
  @Override
  public ActionSmallImage put(@NotNull ActionSmallImage image) {
    return null;
  }

  @Nullable
  @Override
  public SeatingPlanSvg put(@NotNull SeatingPlanSvg svg) {
    return null;
  }

  @Nullable
  @Override
  public VenueBigImage getVenueBigImage(long venueId) {
    return null;
  }

  @Nullable
  @Override
  public ActionBigImage getActionBigImage(long actionId) {
    return null;
  }

  @Nullable
  @Override
  public ActionSmallImage getActionSmallImage(long actionId) {
    return null;
  }

  @Nullable
  @Override
  public SeatingPlanSvg getSeatingPlanSvg(long planId) {
    return null;
  }

  @Override
  public void shutdown() {

  }
}
