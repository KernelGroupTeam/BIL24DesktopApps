package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public final class BezeichnungsTextText extends AbstractBlock {
  private final int index;//1 байт
  private final int textLength;//1 байт
  private final EventimString text;//байтов столько сколько было в значении предыдущей переменной
  private final byte[] unknown;//Неизвестные байты

  public BezeichnungsTextText(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);
    int startPosition = readManager.getPosition() - 6;
    index = readManager.getUnsignedByte();
    textLength = readManager.getUnsignedByte();
    text = readManager.getString(textLength);
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  @Override
  public void write(@NotNull WriteManager writeManager) {
    super.write(writeManager);
    writeManager.putByte(index);
    writeManager.putByte(textLength);
    writeManager.putString(text);
    writeManager.putBytes(unknown);
  }
}
