package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 15.11.2016.
 */
public final class Ellipse extends AbstractBlock {
  private final long id;//4 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final short width;//2 байта
  private final short height;//2 байта
  private final int penWidth;//2 байта
  private final boolean filled;//1 байт
  private final int colFrame;//4 байта
  private final int colBk;//4 байта
  private final long layerId;//4 байта
  private final long bereichId;//4 байта
  private final boolean isStage;//1 байт
  private final long angle;//4 байта
  private final byte[] unknown;//Неизвестные байты

  public Ellipse(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    xPos = readManager.getX();
    yPos = readManager.getY();
    width = readManager.getWidth();
    height = readManager.getHeight();
    penWidth = readManager.getUnsignedShort();
    filled = readManager.getBool();
    colFrame = readManager.getColor();
    colBk = readManager.getColor();
    layerId = readManager.getUnsignedInt();
    bereichId = readManager.getUnsignedInt();
    isStage = readManager.getBool();
    angle = readManager.getUnsignedInt();
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
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
    writeManager.putShort(width);
    writeManager.putShort(height);
    writeManager.putShort(penWidth);
    writeManager.putBoolean(filled);
    writeManager.putInt(colFrame);
    writeManager.putInt(colBk);
    writeManager.putInt(layerId);
    writeManager.putInt(bereichId);
    writeManager.putBoolean(isStage);
    writeManager.putInt(angle);
    writeManager.putBytes(unknown);
  }
}
