package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 05.09.2016.
 */
public class Reihe extends AbstractBlock {
  private final long id;//4 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final boolean horz;//1 байт
  private final boolean left;//1 байт
  private final int textColor;//4 байта
  private final EventimString reiheBez;//5 байтов
  private final EventimString firstSitzBez;//5 байтов
  private final int sitzInkrement;//4 байта
  private final long layerId;//4 байта
  private final EventimString tischBez;//5 байтов
  private final long tischTextObjectId;//4 байта
  private final boolean tischTextObjectHidden;//1 байт
  private final byte indexBezTextReihe;//1 байт
  private final byte indexBezTextTisch;//1 байт
  private final byte indexBezTextPlatz;//1 байт
  private final byte printFlags;//1 байт
  private final long angle;//4 байта
  private final boolean valueLeap;//1 байт
  private long unused1;//4 байта
  private long snakeGroupID;//4 байта
  private final byte[] unknown;//Неизвестные байты

  public Reihe(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    xPos = readManager.getX();
    yPos = readManager.getY();
    horz = readManager.getBool();
    left = readManager.getBool();
    textColor = readManager.getColor();
    reiheBez = readManager.getString(5);
    firstSitzBez = readManager.getString(5);
    sitzInkrement = readManager.getInt();
    layerId = readManager.getUnsignedInt();
    tischBez = readManager.getString(5);
    tischTextObjectId = readManager.getUnsignedInt();
    tischTextObjectHidden = readManager.getBool();
    indexBezTextReihe = readManager.getByte();
    indexBezTextTisch = readManager.getByte();
    indexBezTextPlatz = readManager.getByte();
    printFlags = readManager.getByte();
    angle = readManager.getUnsignedInt();
    valueLeap = readManager.getBool();
    if (getVersion() >= 4) {
      unused1 = readManager.getUnsignedInt();
      snakeGroupID = readManager.getUnsignedInt();
    }
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
    writeManager.putBoolean(horz);
    writeManager.putBoolean(left);
    writeManager.putInt(textColor);
    writeManager.putString(reiheBez);
    writeManager.putString(firstSitzBez);
    writeManager.putInt(sitzInkrement);
    writeManager.putInt(layerId);
    writeManager.putString(tischBez);
    writeManager.putInt(tischTextObjectId);
    writeManager.putBoolean(tischTextObjectHidden);
    writeManager.putByte(indexBezTextReihe);
    writeManager.putByte(indexBezTextTisch);
    writeManager.putByte(indexBezTextPlatz);
    writeManager.putByte(printFlags);
    writeManager.putInt(angle);
    writeManager.putBoolean(valueLeap);
    if (getVersion() >= 4) {
      writeManager.putInt(unused1);
      writeManager.putInt(snakeGroupID);
    }
    writeManager.putBytes(unknown);
  }

  /**
   * Это блок ряда
   */
  @Override
  public boolean isRow() {
    return true;
  }

  /**
   * Получить блок ряда
   */
  @Override
  public Reihe getRow() {
    return this;
  }

  /**
   * Получить ид ряда
   */
  public long getId() {
    return id;
  }

  /**
   * Получить название ряда
   */
  public String getRowName() {
    return reiheBez.getText();
  }

  /**
   * Получить номер 1-ого места в ряде
   */
  public String getRowFirstNumber() {
    return firstSitzBez.getText();
  }
}
