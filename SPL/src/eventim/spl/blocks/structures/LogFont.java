package eventim.spl.blocks.structures;

import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 15.11.2016.
 */
public final class LogFont {
  private final long lfHeight;//4 байта
  private final long lfWidth;//4 байта
  private final long lfEscapement;//4 байта
  private final long lfOrientation;//4 байта
  private final long lfWeight;//4 байта
  private final byte lfItalic;//1 байт
  private final byte lfUnderline;//1 байт
  private final byte lfStrikeOut;//1 байт
  private final byte lfCharSet;//1 байт
  private final byte lfOutPrecision;//1 байт
  private final byte lfClipPrecision;//1 байт
  private final byte lfQuality;//1 байт
  private final byte lfPitchAndFamily;//1 байт
  private final EventimString lfFaceName;//32 байта

  public LogFont(@NotNull ReadManager readManager) {
    lfHeight = readManager.getUnsignedInt();
    lfWidth = readManager.getUnsignedInt();
    lfEscapement = readManager.getUnsignedInt();
    lfOrientation = readManager.getUnsignedInt();
    lfWeight = readManager.getUnsignedInt();
    lfItalic = readManager.getByte();
    lfUnderline = readManager.getByte();
    lfStrikeOut = readManager.getByte();
    lfCharSet = readManager.getByte();
    lfOutPrecision = readManager.getByte();
    lfClipPrecision = readManager.getByte();
    lfQuality = readManager.getByte();
    lfPitchAndFamily = readManager.getByte();
    lfFaceName = readManager.getString(32);
  }

  /**
   * Записываем значения переменных в новый массив байтов
   */
  public void write(@NotNull WriteManager writeManager) {
    writeManager.putInt(lfHeight);
    writeManager.putInt(lfWidth);
    writeManager.putInt(lfEscapement);
    writeManager.putInt(lfOrientation);
    writeManager.putInt(lfWeight);
    writeManager.putByte(lfItalic);
    writeManager.putByte(lfUnderline);
    writeManager.putByte(lfStrikeOut);
    writeManager.putByte(lfCharSet);
    writeManager.putByte(lfOutPrecision);
    writeManager.putByte(lfClipPrecision);
    writeManager.putByte(lfQuality);
    writeManager.putByte(lfPitchAndFamily);
    writeManager.putString(lfFaceName);
  }
}
