package eventim.spl.blocks;

import eventim.spl.blocks.structures.*;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.07.2016.
 */
public class Bereich extends AbstractBlock {
  private final long id;//4 байта
  private final int color;//4 байта
  private final long parentId;//4 байта
  private final long layerId;//4 байта
  private final EventimString kuerzel;//5 байтов
  private final long sektorId;//4 байта
  private final byte[] unknown;//Неизвестные байты
  private final ItemSaveText subBlock;

  public Bereich(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    color = readManager.getColor();
    parentId = readManager.getUnsignedInt();
    layerId = readManager.getUnsignedInt();
    kuerzel = readManager.getString(5);
    sektorId = readManager.getUnsignedInt();
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
    writeManager.putInt(layerId);
    writeManager.putString(kuerzel);
    writeManager.putInt(sektorId);
    writeManager.putBytes(unknown);
    subBlock.write(writeManager);
  }

  /**
   * Это блок сектора
   */
  @Override
  public boolean isSector() {
    return true;
  }

  /**
   * Получить блок сектора
   */
  @Override
  public Bereich getSector() {
    return this;
  }

  /**
   * Получить ид сектора
   */
  public long getId() {
    return id;
  }

  /**
   * Получить название сектора
   */
  public String getSectorName() {
    return subBlock.getText();
  }
}
