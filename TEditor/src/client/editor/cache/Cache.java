package client.editor.cache;

import client.editor.cache.entity.*;
import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 11.04.16
 */
public interface Cache {
//  ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {//на будущее, если обращения к базе станут долгими
//    final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
//
//    @Override
//    public Thread newThread(@NotNull Runnable r) {
//      Thread thread = defaultFactory.newThread(r);
//      thread.setName("JECache-" + thread.getName());
//      return thread;
//    }
//  });

  @Nullable
  VenueBigImage put(@NotNull VenueBigImage image);

  @Nullable
  ActionBigImage put(@NotNull ActionBigImage image);

  @Nullable
  ActionSmallImage put(@NotNull ActionSmallImage image);

  @Nullable
  SeatingPlanSvg put(@NotNull SeatingPlanSvg svg);

  @Nullable
  VenueBigImage getVenueBigImage(long venueId);

  @Nullable
  ActionBigImage getActionBigImage(long actionId);

  @Nullable
  ActionSmallImage getActionSmallImage(long actionId);

  @Nullable
  SeatingPlanSvg getSeatingPlanSvg(long planId);

  void shutdown();
}
