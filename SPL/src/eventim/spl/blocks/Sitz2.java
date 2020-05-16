package eventim.spl.blocks;

import eventim.spl.blocks.structures.*;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 02.08.2016.
 */
public class Sitz2 extends AbstractBlock {
  private long id;//4 байта
  private final int itemTagOffset;//2 байта
  private final short xPos;//2 байта
  private final short yPos;//2 байта
  private final long reiheId;//4 байта
  private final long bereichId;//4 байта
  private final long sitzgruppeId;//4 байта
  private final int seqNo;//2 байта
  private final EventimString sitzNr;//5 байтов
  private final boolean horz;//1 байт
  private final byte[] unknown;//Неизвестные байты
  private final ItemTag[] subBlocks;

  public Sitz2(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
    super(ioChunk, version);//6 байтов
    int startPosition = readManager.getPosition() - 6;
    id = readManager.getUnsignedInt();
    itemTagOffset = readManager.getUnsignedShort();
    xPos = readManager.getX();
    yPos = readManager.getY();
    reiheId = readManager.getUnsignedInt();
    bereichId = readManager.getUnsignedInt();
    sitzgruppeId = readManager.getUnsignedInt();
    seqNo = readManager.getUnsignedShort();
    sitzNr = readManager.getString(5);
    horz = readManager.getBool();
    int remaining = getSize() - itemTagOffset;
    if (remaining >= 5 && remaining % 5 == 0) {
      subBlocks = new ItemTag[remaining / 5];
      for (int i = 0; i < subBlocks.length; i++) {
        subBlocks[i] = new ItemTag(readManager);
      }
    } else {
      subBlocks = null;
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
    writeManager.putShort(itemTagOffset);
    writeManager.putShort(xPos);
    writeManager.putShort(yPos);
    writeManager.putInt(reiheId);
    writeManager.putInt(bereichId);
    writeManager.putInt(sitzgruppeId);
    writeManager.putShort(seqNo);
    writeManager.putString(sitzNr);
    writeManager.putBoolean(horz);
    writeManager.putBytes(unknown);
    if (subBlocks != null) {
      for (ItemTag itemTag : subBlocks) {
        itemTag.write(writeManager);
      }
    }
  }

  /**
   * Это блок места
   */
  @Override
  public boolean isSeat() {
    return true;
  }

  /**
   * Получить блок места
   */
  @Override
  public Sitz2 getSeat() {
    return this;
  }

  /**
   * Получить ид сектора, в котором это место
   */
  public long getSectorId() {
    return bereichId;
  }

  /**
   * Получить ид ряда, в котором это место
   */
  public long getRowId() {
    return reiheId;
  }

  /**
   * Получить порядковый номер места
   */
  public int getSeatNumber() {
    return seqNo;
  }

  /**
   * Получить номер места
   */
  public String getSeatName() {
    return sitzNr.getText();
  }

  /**
   * Получить ИД места
   */
  public long getSeatId() {
    return id;
  }

  /**
   * Установить новый ИД места
   */
  public void setSeatId(long id) {
    this.id = id;
  }
}
