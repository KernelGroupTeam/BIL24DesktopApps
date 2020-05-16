package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public final class Sektor extends AbstractBlock {
  private final long id;//4 байта
  private final int color;//4 байта
  private final long parentId;//4 байта
  private final EventimString text;//31 байт
  private final byte[] unknown;//Неизвестные байты

  public Sektor(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    color = readManager.getColor();
    parentId = readManager.getUnsignedInt();
    text = readManager.getString(31);
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
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
    writeManager.putString(text);
    writeManager.putBytes(unknown);
  }
}
