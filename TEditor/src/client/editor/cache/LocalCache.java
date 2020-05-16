package client.editor.cache;

import java.io.File;

import client.editor.cache.entity.*;
import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import org.jetbrains.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 12.01.16
 */
public class LocalCache implements Cache {
  private static final int CURRENT_REVISION = 3;
  private final Environment env;
  private final EntityStore store;
  private final boolean readOnly;
  private final PrimaryIndex<Long, VenueBigImage> venueBigImageIndex;
  private final PrimaryIndex<Long, ActionBigImage> actionBigImageIndex;
  private final PrimaryIndex<Long, ActionSmallImage> actionSmallImageIndex;
  private final PrimaryIndex<Long, SeatingPlanSvg> seatingPlanSvgIndex;

  public LocalCache(@NotNull File cacheFolder, String storeName, boolean autoCreation) {
    if (autoCreation && !cacheFolder.exists()) {
      //noinspection ResultOfMethodCallIgnored
      cacheFolder.mkdirs();
    }
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreateVoid(autoCreation);
    Environment environment;
    try {
      environment = new Environment(cacheFolder, envConfig);
    } catch (EnvironmentLockedException e) {
      envConfig.setReadOnlyVoid(true);
      environment = new Environment(cacheFolder, envConfig);
    }
    env = environment;
    readOnly = envConfig.getReadOnly();

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreateVoid(autoCreation);
    storeConfig.setReadOnlyVoid(readOnly);
    store = new EntityStore(env, storeName, storeConfig);

    if (!readOnly) {//проверка номера ревизии для очистки старых классов в базе
      PrimaryIndex<Integer, Revision> revisionIndex = store.getPrimaryIndex(Integer.class, Revision.class);
      Revision revision = revisionIndex.get(0);
      if (revision == null || revision.getRevision() < CURRENT_REVISION) {
        store.truncateClass(SeatingPlanSvg.class);
        revisionIndex.put(new Revision(CURRENT_REVISION));
      }
    }

    venueBigImageIndex = store.getPrimaryIndex(Long.class, VenueBigImage.class);
    actionBigImageIndex = store.getPrimaryIndex(Long.class, ActionBigImage.class);
    actionSmallImageIndex = store.getPrimaryIndex(Long.class, ActionSmallImage.class);
    seatingPlanSvgIndex = store.getPrimaryIndex(Long.class, SeatingPlanSvg.class);
  }

  @Nullable
  @Override
  public VenueBigImage put(@NotNull VenueBigImage image) {
    if (readOnly) return null;
    return venueBigImageIndex.put(image);
  }

  @Nullable
  @Override
  public ActionBigImage put(@NotNull ActionBigImage image) {
    if (readOnly) return null;
    return actionBigImageIndex.put(image);
  }

  @Nullable
  @Override
  public ActionSmallImage put(@NotNull ActionSmallImage image) {
    if (readOnly) return null;
    return actionSmallImageIndex.put(image);
  }

  @Nullable
  @Override
  public SeatingPlanSvg put(@NotNull SeatingPlanSvg svg) {
    if (readOnly) return null;
    return seatingPlanSvgIndex.put(svg);
  }

  @Nullable
  @Override
  public VenueBigImage getVenueBigImage(long venueId) {
    return venueBigImageIndex.get(venueId);
  }

  @Nullable
  @Override
  public ActionBigImage getActionBigImage(long actionId) {
    return actionBigImageIndex.get(actionId);
  }

  @Nullable
  @Override
  public ActionSmallImage getActionSmallImage(long actionId) {
    return actionSmallImageIndex.get(actionId);
  }

  @Nullable
  @Override
  public SeatingPlanSvg getSeatingPlanSvg(long planId) {
    return seatingPlanSvgIndex.get(planId);
  }

  @Override
  public synchronized void shutdown() {
    store.close();
    env.close();
  }
}
