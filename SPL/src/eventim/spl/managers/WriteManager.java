package eventim.spl.managers;

import java.nio.ByteBuffer;

import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * Created by Inventor on 10.09.2016.
 */
public class WriteManager {
  @NotNull
  private final ByteBuffer buffer;

  WriteManager(int capacity) {
    buffer = ByteBuffer.allocate(capacity);
    buffer.order(LITTLE_ENDIAN);
  }

  /**
   * Сохранить bool значение
   */
  public void putBoolean(boolean value) {
    buffer.put(value ? (byte) 1 : 0);
  }

  /**
   * Сохранить беззнаковый byte
   */
  public void putByte(int value) {
    putByte((byte) value);
  }

  /**
   * Сохранить обычный byte
   */
  public void putByte(byte value) {
    buffer.put(value);
  }

  /**
   * Сохранить массив байтов
   */
  public void putBytes(@NotNull byte[] value) {
    buffer.put(value);
  }

  /**
   * Сохранить беззнаковый short
   */
  public void putShort(int value) {
    putShort((short) value);
  }

  /**
   * Сохранить обычный short
   */
  public void putShort(short value) {
    buffer.putShort(value);
  }

  /**
   * Сохранить беззнаковый int
   */
  public void putInt(long value) {
    putInt((int) value);
  }

  /**
   * Сохранить обычный int
   */
  public void putInt(int value) {
    buffer.putInt(value);
  }

  /**
   * Сохранить обычный long
   */
  public void putLong(long value) {
    buffer.putLong(value);
  }

  /**
   * Сохранить String
   */
  public void putString(@NotNull EventimString text) {
    buffer.put(text.getBytes());
  }

  /**
   * Получить сформированный массив байтов
   */
  @NotNull
  public byte[] getData() {
    return buffer.array();
  }
}
