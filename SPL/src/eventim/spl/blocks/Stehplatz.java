package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.12.2016.
 */
public class Stehplatz extends AbstractBlock {
  private long id;//4 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final short width;//2 байта
  private final short height;//2 байта
  private final long bereichId;//4 байта
  private final long eingangId;//4 байта
  private final long[] infoIds;//4 * 7 байт
  private final long plaetze;//4 байта
  private final EventimString stehPlatzNo;//5 байт
  private final boolean link;//1 байт
  private final long preisKlasseId;//4 байта
  private final boolean horz;//1 байт
  private final long layerId;//4 байта
  private final long anzahlStehplatzverknupfungen;//4 байта
  private final long unused1;//4 байта
  private final byte indexBezTextReihe;//1 байт
  private final byte indexBezTextTisch;//1 байт
  private final byte indexBezTextPlatz;//1 байт
  private final byte printFlags;//1 байт
  private final long angle;//4 байта
  private final long unused;//8 байт
  private final long panoramaImageID;//4 байта
  private final byte[] unknown;//Неизвестные байты

  public Stehplatz(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    xPos = readManager.getX();
    yPos = readManager.getY();
    width = readManager.getWidth();
    height = readManager.getHeight();
    bereichId = readManager.getUnsignedInt();
    eingangId = readManager.getUnsignedInt();
    infoIds = new long[7];
    for (int i = 0; i < infoIds.length; i++) {
      infoIds[i] = readManager.getUnsignedInt();
    }
    plaetze = readManager.getUnsignedInt();
    stehPlatzNo = readManager.getString(5);
    link = readManager.getBool();
    preisKlasseId = readManager.getUnsignedInt();
    horz = readManager.getBool();
    layerId = readManager.getUnsignedInt();
    anzahlStehplatzverknupfungen = readManager.getUnsignedInt();
    unused1 = readManager.getUnsignedInt();
    indexBezTextReihe = readManager.getByte();
    indexBezTextTisch = readManager.getByte();
    indexBezTextPlatz = readManager.getByte();
    printFlags = readManager.getByte();
    angle = readManager.getUnsignedInt();
    unused = readManager.getLong();
    panoramaImageID = readManager.getUnsignedInt();
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
    writeManager.putInt(bereichId);
    writeManager.putInt(eingangId);
    for (long infoId : infoIds) {
      writeManager.putInt(infoId);
    }
    writeManager.putInt(plaetze);
    writeManager.putString(stehPlatzNo);
    writeManager.putBoolean(link);
    writeManager.putInt(preisKlasseId);
    writeManager.putBoolean(horz);
    writeManager.putInt(layerId);
    writeManager.putInt(anzahlStehplatzverknupfungen);
    writeManager.putInt(unused1);
    writeManager.putByte(indexBezTextReihe);
    writeManager.putByte(indexBezTextTisch);
    writeManager.putByte(indexBezTextPlatz);
    writeManager.putByte(printFlags);
    writeManager.putInt(angle);
    writeManager.putLong(unused);
    writeManager.putInt(panoramaImageID);
    writeManager.putBytes(unknown);
  }

  /**
   * Это блок безместовых мест
   */
  @Override
  public boolean isNplCategory() {
    return true;
  }

  /**
   * Получить блок безместовых мест
   */
  @Override
  public Stehplatz getNplCategory() {
    return this;
  }

  /**
   * Получить ид сектора
   */
  public long getSectorId() {
    return bereichId;
  }

  /**
   * Получить количество всего мест
   */
  public long getSeatCount() {
    return plaetze;
  }

  /**
   * Получить ИД
   */
  public long getId() {
    return id;
  }

  /**
   * Установить новый ИД
   */
  public void setId(long id) {
    this.id = id;
  }
}
