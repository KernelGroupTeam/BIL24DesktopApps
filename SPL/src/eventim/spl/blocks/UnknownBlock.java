package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public class UnknownBlock extends AbstractBlock {
  private final byte[] unknown;//Неизвестные байты

  public UnknownBlock(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    unknown = readManager.getBytes(getSize() - 6);//Сохраняем все байты в количестве -6, т.к. размер блока IOChunk 6 байтов
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  @Override
  public void write(@NotNull WriteManager writeManager) {
    super.write(writeManager);
    writeManager.putBytes(unknown);
  }
}
