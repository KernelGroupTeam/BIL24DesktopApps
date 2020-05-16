package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public class VBSave extends AbstractBlock {
  private final long nextId;//4 байта Следующий ид блока(почему-то равен последнему, и к каким именно блокам это относится полностью не известно)
  private final long nextBereichColor;//4 байта
  private final long nextEingangColor;//4 байта
  private final long nextSitzGruppeColor;//4 байта
  private final long nextSitzGruppeNr;//4 байта
  private final int nextInfoGruppeIndex;//4 байта Следующее значение индекс блока Infogruppe
  private long splId;//8 байтов Ид схемы
  private final int colorBkGnd;//4 байта RGB int цвет заднего фона схемы
  private final boolean layerMode;//1 байт
  private final boolean externPk;//1 байт
  private final long saveFlags;//4 байта
  private final long nextSitzId;//4 байта Следующее значение ид блока Sitz2
  private final long nextStehPlatzId;//4 байта
  private final short gridWith;//2 байта Ширина ячейки сетки
  private final short gridHeight;//2 байта Высота ячейки сетки
  private final short defaultSitzWidth;//2 байта Ширина кружочка места
  private final short defaultSitzHeight;//2 байта Высота кружочка места
  private final long version;//4 байта Версия схемы
  private final int importSource;//1 байт
  private final long updateNo;//4 байта
  private final int createGridStepY;//4 байта
  private final int createGridStepX;//4 байта
  private final int createIndexBezTextReihe;//1 байт
  private final int createIndexBezTextTisch;//1 байт
  private final int createIndexBezTextPlatz;//1 байт
  private final int createPrintFlags;//1 байт
  private final int infoGruppePopup;//1 байт
  private final int infoGruppeShoppingCard;//1 байт
  private final long infoGruppeExcludeFromPrint;//4 байта
  private int spViewMode;//4 байта
  private final long nextPanoramaBildColor;//4 байта
  private long nextSnakingGroupColor;//4 байта
  private long nextSnakingGroupNr;//4 байта
  private final byte[] unknown;//Неизвестные байты

  public VBSave(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    nextId = readManager.getUnsignedInt();
    nextBereichColor = readManager.getUnsignedInt();
    nextEingangColor = readManager.getUnsignedInt();
    nextSitzGruppeColor = readManager.getUnsignedInt();
    nextSitzGruppeNr = readManager.getUnsignedInt();
    nextInfoGruppeIndex = readManager.getInt();
    splId = readManager.getLong();
    colorBkGnd = readManager.getColor();
    layerMode = readManager.getBool();
    externPk = readManager.getBool();
    saveFlags = readManager.getUnsignedInt();
    nextSitzId = readManager.getUnsignedInt();
    nextStehPlatzId = readManager.getUnsignedInt();
    gridWith = readManager.getWidth();
    gridHeight = readManager.getHeight();
    defaultSitzWidth = readManager.getWidth();
    defaultSitzHeight = readManager.getHeight();
    this.version = readManager.getUnsignedInt();
    importSource = readManager.getUnsignedByte();
    updateNo = readManager.getUnsignedInt();
    createGridStepY = readManager.getInt();
    createGridStepX = readManager.getInt();
    createIndexBezTextReihe = readManager.getUnsignedByte();
    createIndexBezTextTisch = readManager.getUnsignedByte();
    createIndexBezTextPlatz = readManager.getUnsignedByte();
    createPrintFlags = readManager.getUnsignedByte();
    infoGruppePopup = readManager.getUnsignedByte();
    infoGruppeShoppingCard = readManager.getUnsignedByte();
    infoGruppeExcludeFromPrint = readManager.getUnsignedInt();
    if (this.version >= 3) spViewMode = readManager.getInt();
    nextPanoramaBildColor = readManager.getUnsignedInt();
    if (this.version >= 4) {
      nextSnakingGroupColor = readManager.getUnsignedInt();
      nextSnakingGroupNr = readManager.getUnsignedInt();
    }
    unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  @Override
  public void write(@NotNull WriteManager writeManager) {
    super.write(writeManager);
    writeManager.putInt(nextId);
    writeManager.putInt(nextBereichColor);
    writeManager.putInt(nextEingangColor);
    writeManager.putInt(nextSitzGruppeColor);
    writeManager.putInt(nextSitzGruppeNr);
    writeManager.putInt(nextInfoGruppeIndex);
    writeManager.putLong(splId);
    writeManager.putInt(colorBkGnd);
    writeManager.putBoolean(layerMode);
    writeManager.putBoolean(externPk);
    writeManager.putInt(saveFlags);
    writeManager.putInt(nextSitzId);
    writeManager.putInt(nextStehPlatzId);
    writeManager.putShort(gridWith);
    writeManager.putShort(gridHeight);
    writeManager.putShort(defaultSitzWidth);
    writeManager.putShort(defaultSitzHeight);
    writeManager.putInt(version);
    writeManager.putByte(importSource);
    writeManager.putInt(updateNo);
    writeManager.putInt(createGridStepY);
    writeManager.putInt(createGridStepX);
    writeManager.putByte(createIndexBezTextReihe);
    writeManager.putByte(createIndexBezTextTisch);
    writeManager.putByte(createIndexBezTextPlatz);
    writeManager.putByte(createPrintFlags);
    writeManager.putByte(infoGruppePopup);
    writeManager.putByte(infoGruppeShoppingCard);
    writeManager.putInt(infoGruppeExcludeFromPrint);
    writeManager.putInt(spViewMode);
    writeManager.putInt(nextPanoramaBildColor);
    if (version >= 4) {
      writeManager.putInt(nextSnakingGroupColor);
      writeManager.putInt(nextSnakingGroupNr);
    }
    writeManager.putBytes(unknown);
  }

  /**
   * Это блок VBSave
   */
  @Override
  public boolean isVBSave() {
    return true;
  }

  /**
   * Получить блок VBSave
   */
  @Override
  public VBSave getVBSave() {
    return this;
  }

  /**
   * Получить ид схемы зала
   */
  public long getSplId() {
    return splId;
  }

  /**
   * Устанавливаем ид схемы зала
   */
  public void setSplId(long splId) {
    this.splId = splId;
  }

  public long getNextId() {
    return nextId;
  }

  public long getNextBereichColor() {
    return nextBereichColor;
  }

  public long getNextEingangColor() {
    return nextEingangColor;
  }

  public long getNextSitzGruppeColor() {
    return nextSitzGruppeColor;
  }

  public long getNextSitzGruppeNr() {
    return nextSitzGruppeNr;
  }

  public int getNextInfoGruppeIndex() {
    return nextInfoGruppeIndex;
  }

  public int getColorBkGnd() {
    return colorBkGnd;
  }

  public boolean isLayerMode() {
    return layerMode;
  }

  public boolean isExternPk() {
    return externPk;
  }

  public long getSaveFlags() {
    return saveFlags;
  }

  public long getNextSitzId() {
    return nextSitzId;
  }

  public long getNextStehPlatzId() {
    return nextStehPlatzId;
  }

  public short getGridWith() {
    return gridWith;
  }

  public short getGridHeight() {
    return gridHeight;
  }

  public short getDefaultSitzWidth() {
    return defaultSitzWidth;
  }

  public short getDefaultSitzHeight() {
    return defaultSitzHeight;
  }

  @Override
  public long getVersion() {
    return version;
  }

  public int getImportSource() {
    return importSource;
  }

  public long getUpdateNo() {
    return updateNo;
  }

  public int getCreateGridStepY() {
    return createGridStepY;
  }

  public int getCreateGridStepX() {
    return createGridStepX;
  }

  public int getCreateIndexBezTextReihe() {
    return createIndexBezTextReihe;
  }

  public int getCreateIndexBezTextTisch() {
    return createIndexBezTextTisch;
  }

  public int getCreateIndexBezTextPlatz() {
    return createIndexBezTextPlatz;
  }

  public int getCreatePrintFlags() {
    return createPrintFlags;
  }

  public int getInfoGruppePopup() {
    return infoGruppePopup;
  }

  public int getInfoGruppeShoppingCard() {
    return infoGruppeShoppingCard;
  }

  public long getInfoGruppeExcludeFromPrint() {
    return infoGruppeExcludeFromPrint;
  }

  public int getSpViewMode() {
    return spViewMode;
  }

  public long getNextPanoramaBildColor() {
    return nextPanoramaBildColor;
  }

  public long getNextSnakingGroupColor() {
    return nextSnakingGroupColor;
  }

  public long getNextSnakingGroupNr() {
    return nextSnakingGroupNr;
  }
}
