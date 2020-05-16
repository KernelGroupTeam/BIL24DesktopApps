package eventim.spl.blocks;

import eventim.spl.blocks.structures.*;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 12.11.2017.
 */
public final class Layer extends AbstractBlock {
  private final long id;//4 байта
  private final int color;//4 байта
  private final long parentId;//4 байта
  private final byte[] unknown;//Неизвестные байты
  private final ItemSaveText subBlock;

  public Layer(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    color = readManager.getColor();
    parentId = readManager.getUnsignedInt();
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
    subBlock = new ItemSaveText(readManager);
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  @Override
  public void write(@NotNull WriteManager writeManager) {
    super.write(writeManager);
    writeManager.putInt(id);
    writeManager.putInt(color);
    writeManager.putInt(parentId);
    writeManager.putBytes(unknown);
    subBlock.write(writeManager);
  }
}
