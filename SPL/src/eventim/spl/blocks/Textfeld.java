package eventim.spl.blocks;

import eventim.spl.blocks.structures.*;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 15.11.2016.
 */
public final class Textfeld extends AbstractBlock {
  private final long id;//4 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final boolean horz;//1 байт
  private final boolean left;//1 байт
  private final int textColor;//4 байта
  private final int bkColor;//4 байта
  private final boolean transparent;//1 байт
  private final LogFont logFont;//60 байтов
  private final long layerId;//4 байта
  private final long bereichId;//4 байта
  private final boolean china;//1 байт
  private final boolean multiline;//1 байт
  private final boolean anzFreieText;//1 байт
  private final long angle;//4 байта
  private final byte[] unknown;//Неизвестные байты
  private final ItemSaveText subBlock;

  public Textfeld(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    xPos = readManager.getX();
    yPos = readManager.getY();
    horz = readManager.getBool();
    left = readManager.getBool();
    textColor = readManager.getColor();
    bkColor = readManager.getColor();
    transparent = readManager.getBool();
    logFont = new LogFont(readManager);
    layerId = readManager.getUnsignedInt();
    bereichId = readManager.getUnsignedInt();
    china = readManager.getBool();
    multiline = readManager.getBool();
    anzFreieText = readManager.getBool();
    angle = readManager.getUnsignedInt();
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
    writeManager.putShort(xPos);
    writeManager.putShort(yPos);
    writeManager.putBoolean(horz);
    writeManager.putBoolean(left);
    writeManager.putInt(textColor);
    writeManager.putInt(bkColor);
    writeManager.putBoolean(transparent);
    logFont.write(writeManager);
    writeManager.putInt(layerId);
    writeManager.putInt(bereichId);
    writeManager.putBoolean(china);
    writeManager.putBoolean(multiline);
    writeManager.putBoolean(anzFreieText);
    writeManager.putInt(angle);
    writeManager.putBytes(unknown);
    subBlock.write(writeManager);
  }
}
