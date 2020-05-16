package eventim.spl.blocks.structures;

import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 07.09.2016.
 */
public class ItemSaveText {
  private final int len;//1 байт
  private final EventimString text;//байтов столько сколько было в значении предыдущей переменной

  public ItemSaveText(@NotNull ReadManager readManager) {
    len = readManager.getUnsignedByte();
    text = readManager.getString(len);
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  public void write(@NotNull WriteManager writeManager) {
    writeManager.putByte(len);
    writeManager.putString(text);
  }

  public String getText() {
    return text.getText();
  }
}
