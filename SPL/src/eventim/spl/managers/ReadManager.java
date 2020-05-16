package eventim.spl.managers;

import java.nio.ByteBuffer;

import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * Created by Inventor on 22.07.2016.
 */
public class ReadManager {
  @NotNull
  private final ByteBuffer buffer;

  ReadManager(@NotNull byte[] array) {
    buffer = ByteBuffer.wrap(array);
    buffer.order(LITTLE_ENDIAN);
  }

  /**
   * Получить обычный байт
   */
  public byte getByte() {
    return readByte();
  }

  /**
   * Получить беззнаковый байт
   */
  public int getUnsignedByte() {
    return readByte() & 0xff;
  }

  /**
   * Получить обычный int
   */
  public int getInt() {
    return buffer.getInt();
  }

  /**
   * Получить беззнаковый int
   */
  public long getUnsignedInt() {
    return buffer.getInt() & 0xffffffffL;
  }

  /**
   * Получить обычный boolean
   */
  public boolean getBool() {
    return readByte() == 1;
  }

  /**
   * Получить обычный short
   */
  public short getShort() {
    return buffer.getShort();
  }

  /**
   * Получить беззнаковый short
   */
  public int getUnsignedShort() {
    return buffer.getShort() & 0xffff;
  }

  /**
   * Получить String длиной count в кодировке cp1251
   * Причем String по длине может оказаться меньше, если во время считывания мы встетим байт с 0 значением
   */
  @NotNull
  public EventimString getString(int count) {
    return new EventimString(readBytes(count));
  }

  /**
   * Получить обычный int
   */
  public int getColor() {
    return buffer.getInt();
  }

  /**
   * Получить обычный long
   */
  public long getLong() {
    return buffer.getLong();
  }

  /**
   * Получить обычный short
   */
  public short getX() {
    return buffer.getShort();
  }

  /**
   * Получить обычный short
   */
  public short getY() {
    return buffer.getShort();
  }

  /**
   * Получить обычный short
   */
  public short getWidth() {
    return buffer.getShort();
  }

  /**
   * Получить обычный short
   */
  public short getHeight() {
    return buffer.getShort();
  }

  /**
   * Получить массив байтов длиной count
   */
  @NotNull
  public byte[] getBytes(int count) {
    return readBytes(count);
  }

  /**
   * Получить текущую позицию чтения байтов
   */
  public int getPosition() {
    return buffer.position();
  }

  /**
   * Получить значения оставшегося количества байтов до конца массива
   */
  public int getRemaining() {
    return buffer.remaining();
  }

  /**
   * Получить размер массива байтов
   */
  public int getSize() {
    return buffer.capacity();
  }

  /**
   * Пропустить байты в количестве count
   */
  public void skip(int count) {
    for (int i = 0; i < count; i++) {
      readByte();
    }
  }

  /**
   * Считать 1 обычный байт
   */
  private byte readByte() {
    return buffer.get();
  }

  /**
   * Считать массив байтов в размере count
   */
  @NotNull
  private byte[] readBytes(int count) {
    byte[] array = new byte[count];
    for (int i = 0; i < count; i++) {
      array[i] = readByte();
    }
    return array;
  }
}
