package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 02.08.2016.
 */
public final class BkGndBMP extends AbstractBlock {
  private final long id;//4 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final short width;//2 байта
  private final short height;//2 байта
  private final long layerId;//4 байта
  private final EventimString dateiname;//260 байт
  private final boolean storedImageData;//1 байт
  private final int imageType;//4 байта
  private final byte[] unknown;//Неизвестные байты

  public BkGndBMP(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    xPos = readManager.getX();
    yPos = readManager.getY();
    width = readManager.getWidth();
    height = readManager.getHeight();
    layerId = readManager.getUnsignedInt();
    dateiname = readManager.getString(260);
    storedImageData = readManager.getBool();
    imageType = readManager.getInt();
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
    writeManager.putInt(layerId);
    writeManager.putString(dateiname);
    writeManager.putBoolean(storedImageData);
    writeManager.putInt(imageType);
    writeManager.putBytes(unknown);
  }
}
