package eventim.spl.blocks.structures;

import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 02.08.2016.
 */
public class ItemTag {
  private final byte ident;//1 байт
  private final long payloads;//4 байта

  public ItemTag(@NotNull ReadManager readManager) {
    ident = readManager.getByte();
    payloads = readManager.getUnsignedInt();
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  public void write(@NotNull WriteManager writeManager) {
    writeManager.putByte(ident);
    writeManager.putInt(payloads);
  }
}