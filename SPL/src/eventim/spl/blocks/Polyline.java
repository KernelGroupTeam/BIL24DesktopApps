package eventim.spl.blocks;

import eventim.spl.blocks.structures.*;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 15.11.2016.
 */
public final class Polyline extends AbstractBlock {
  private final long id;//4 байта
  private final int penWidth;//2 байта
  private final int polyPointCnt;//2 байта
  private final int colFrame;//4 байта
  private final long layerId;//4 байта
  private final long bereichId;//4 байта
  private final boolean isStage;//1 байт
  private final byte[] unknown;//Неизвестные байты
  private final PolyPointSave[] subBlocks;

  public Polyline(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    penWidth = readManager.getUnsignedShort();
    polyPointCnt = readManager.getUnsignedShort();
    colFrame = readManager.getColor();
    layerId = readManager.getUnsignedInt();
    bereichId = readManager.getUnsignedInt();
    isStage = readManager.getBool();
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
    if (polyPointCnt == 0) {
      subBlocks = null;
    } else {
      subBlocks = new PolyPointSave[polyPointCnt];
      for (int i = 0; i < subBlocks.length; i++) {
        subBlocks[i] = new PolyPointSave(readManager);
      }
    }
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  @Override
  public void write(@NotNull WriteManager writeManager) {
    super.write(writeManager);
    writeManager.putInt(id);
    writeManager.putShort(penWidth);
    writeManager.putShort(polyPointCnt);
    writeManager.putInt(colFrame);
    writeManager.putInt(layerId);
    writeManager.putInt(bereichId);
    writeManager.putBoolean(isStage);
    writeManager.putBytes(unknown);
    if (subBlocks != null) {
      for (PolyPointSave polyPointSave : subBlocks) {
        polyPointSave.write(writeManager);
      }
    }
  }
}
