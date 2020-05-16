package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.WriteManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public abstract class AbstractBlock {
  @NotNull
  private final IOChunk ioChunk;
  private final long version;

  protected AbstractBlock(@NotNull IOChunk ioChunk, long version) {
    this.ioChunk = ioChunk;
    this.version = version;
  }

  /**
   * Получить ид(тип) IOChunk
   */
  public int getIdent() {
    return ioChunk.getIdent();
  }

  /**
   * Получить размер блока
   */
  public int getSize() {
    return ioChunk.getSize();
  }

  /**
   * Получить версию схемы
   */
  public long getVersion() {
    return version;
  }

  /**
   * Записываем значения переменных в новый массив байтов
   * В первую очередь записываем блок IOChunk
   */
  public void write(@NotNull WriteManager writeManager) {
    ioChunk.write(writeManager);
  }

  /**
   * Это блок сектора?
   * использую этот метод вместо instanceof
   */
  public boolean isSector() {
    return false;
  }

  /**
   * Получить блок сектора
   * использую этот метод вместо приведения типов
   */
  public Bereich getSector() {
    return null;
  }

  /**
   * Это блок ряда?
   * использую этот метод вместо instanceof
   */
  public boolean isRow() {
    return false;
  }

  /**
   * Получить блок ряда
   * использую этот метод вместо приведения типов
   */
  public Reihe getRow() {
    return null;
  }

  /**
   * Это блок места?
   * использую этот метод вместо instanceof
   */
  public boolean isSeat() {
    return false;
  }

  /**
   * Получить блок места
   * использую этот метод вместо приведения типов
   */
  public Sitz2 getSeat() {
    return null;
  }

  /**
   * Это блок безместовых мест?
   * использую этот метод вместо instanceof
   */
  public boolean isNplCategory() {
    return false;
  }

  /**
   * Получить блок безместовых мест
   * использую этот метод вместо приведения типов
   */
  public Stehplatz getNplCategory() {
    return null;
  }

  /**
   * Это блок VBSave?
   * использую этот метод вместо instanceof
   */
  public boolean isVBSave() {
    return false;
  }

  /**
   * Получить блок VBSave
   * использую этот метод вместо приведения типов
   */
  public VBSave getVBSave() {
    return null;
  }
}
