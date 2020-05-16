package eventim.spl.blocks.structures;

import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public class IOChunk {
  private final int ident;//4 байта
  private final int size;//2 байта

  public IOChunk(@NotNull ReadManager readManager) {
    //Считываем значения переменных
    ident = readManager.getInt();
    size = readManager.getUnsignedShort();
  }

  /**
   * Получить ид(тип) IOChunk
   */
  public int getIdent() {
    return ident;
  }

  /**
   * Получить размер блока
   */
  public int getSize() {
    return size;
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  public void write(@NotNull WriteManager writeManager) {
    writeManager.putInt(getIdent());
    writeManager.putShort(getSize());
  }
}
