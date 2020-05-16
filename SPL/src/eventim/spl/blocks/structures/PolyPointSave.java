package eventim.spl.blocks.structures;

import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 15.11.2016.
 */
public final class PolyPointSave {
  private final short x;//2 байта
  private final short y;//2 байта

  public PolyPointSave(@NotNull ReadManager readManager) {
    x = readManager.getX();
    y = readManager.getY();
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  public void write(@NotNull WriteManager writeManager) {
    writeManager.putShort(x);
    writeManager.putShort(y);
  }
}