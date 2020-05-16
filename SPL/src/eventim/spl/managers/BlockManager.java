package eventim.spl.managers;

import eventim.spl.blocks.*;
import eventim.spl.blocks.structures.IOChunk;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
class BlockManager {
  /*НАБОР ЕЩЕ ВОЗМОЖНЫХ БЛОКОВ
        (1, "Sitz");
        (1005, "StehplatzVerknüpfungen");
        (28, "ImageData");
        (28, "ImageData");
        (1006, "ImageData1");
        (27, "Image");
        (1002, "Text");
        (32, "Panorama Image");
        (33, "SnakeGroup");*/

  private BlockManager() {
  }

  /**
   * В зависимости от ид(типа) IOChunk создаем и возвращаем блок
   */
  @NotNull
  static AbstractBlock getBlock(long version, @NotNull ReadManager readManager) {
    IOChunk ioChunk = new IOChunk(readManager);
    switch (ioChunk.getIdent()) {
      case 351513241: {
        return new VBSave(ioChunk, version, readManager);
      }
      case 24: {
        return new Sektor(ioChunk, version, readManager);
      }
      case 6: {
        return new Infogruppe(ioChunk, version, readManager);
      }
      case 26: {
        return new BezeichnungsText(ioChunk, version, readManager);
      }
      case 2: {
        return new Bereich(ioChunk, version, readManager);
      }
      case 5: {
        return new Sitzgruppe(ioChunk, version, readManager);
      }
      case 3: {
        return new Reihe(ioChunk, version, readManager);
      }
      case 29: {
        return new BkGndBMP(ioChunk, version, readManager);
      }
      case 1008: {
        return new ImageData2(ioChunk, version, readManager);
      }
      case 23: {
        return new Sitz2(ioChunk, version, readManager);
      }
      case 4: {
        return new Eingang(ioChunk, version, readManager);
      }
      case 8: {
        return new Rechteck(ioChunk, version, readManager);
      }
      case 19: {
        return new Ellipse(ioChunk, version, readManager);
      }
      case 10: {
        return new Polygon(ioChunk, version, readManager);
      }
      case 20: {
        return new Polyline(ioChunk, version, readManager);
      }
      case 12:
      case 30: {
        return new Textfeld(ioChunk, version, readManager);
      }
      case 7: {
        return new Info(ioChunk, version, readManager);
      }
      case 9: {
        return new Stehplatz(ioChunk, version, readManager);
      }
      case 18: {
        return new Layer(ioChunk, version, readManager);
      }
      default: {
        return new UnknownBlock(ioChunk, version, readManager);
      }
    }
  }
}
