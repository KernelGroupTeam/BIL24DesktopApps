package eventim.spl.managers;

import java.util.*;

import eventim.spl.blocks.*;
import eventim.spl.models.*;
import org.jetbrains.annotations.*;

/**
 * Created by Inventor on 07.09.2016.
 */
public class EventimManager {
  @NotNull
  private byte[] data;
  @NotNull
  private final List<EventimSeat> seatList = new ArrayList<>();
  @NotNull
  private final List<EventimNplCategory> nplCategoryList = new ArrayList<>();
  @NotNull
  private final List<AbstractBlock> blockList = new ArrayList<>();
  @Nullable
  private VBSave vbSave;

  public EventimManager(@NotNull byte[] data) {
    this.data = data;
  }

  @NotNull
  public static EventimManager read(@NotNull byte[] data) {
    EventimManager manager = new EventimManager(data);
    manager.read();
    return manager;
  }

  /**
   * Разбираем полученный массив
   */
  public void read() {
    ReadManager readManager = new ReadManager(data);
    Map<Long, Bereich> sectorMap = new HashMap<>();
    Map<Long, Reihe> rowMap = new HashMap<>();
    List<Sitz2> seatList = new ArrayList<>();
    List<Stehplatz> categoryList = new ArrayList<>();
    vbSave = (VBSave) BlockManager.getBlock(0, readManager);//Сразу получаем первый блок
    blockList.add(vbSave);
    //Считываем из массива поблочно и сохраняем блоки в лист в том же порядке в котором считали, чтобы потом смочь собрать все обратно
    AbstractBlock block;
    while (readManager.getRemaining() > 0) {
      blockList.add(block = BlockManager.getBlock(vbSave.getVersion(), readManager));
      if (block.isSector()) {//Отдельно сохраняем блок сектора
        sectorMap.put(block.getSector().getId(), block.getSector());
      } else if (block.isRow()) {//Отдельно сохраняем блок ряда
        rowMap.put(block.getRow().getId(), block.getRow());
      } else if (block.isSeat()) {//Отдельно сохраняем блок ряда
        seatList.add(block.getSeat());
      } else if (block.isNplCategory()) {//Отдельно сохраняем блок безместовых мест
        categoryList.add(block.getNplCategory());
      }
    }
    //Формируем список местовых мест
    for (Sitz2 seat : seatList) {
      this.seatList.add(new EventimSeat(sectorMap.get(seat.getSectorId()), rowMap.get(seat.getRowId()), seat));
    }
    //Формируем список безместовых мест
    for (Stehplatz category : categoryList) {
      this.nplCategoryList.add(new EventimNplCategory(sectorMap.get(category.getSectorId()), category));
    }
  }

  /**
   * Собираем все блоки воедино
   */
  public void write() {
    WriteManager writeManager = new WriteManager(data.length);
    for (AbstractBlock block : blockList) {
      block.write(writeManager);
    }
    data = writeManager.getData();
  }

  /**
   * Получить байт массив схемы
   */
  @NotNull
  public byte[] getData() {
    return data;
  }

  /**
   * Получить список блоков
   */
  @NotNull
  public List<AbstractBlock> getBlockList() {
    if (vbSave == null) throw new IllegalStateException("read first");
    return blockList;
  }

  /**
   * Получить список местовых мест
   */
  @NotNull
  public List<EventimSeat> getSeatList() {
    if (vbSave == null) throw new IllegalStateException("read first");
    return seatList;
  }

  /**
   * Получить список безместовых мест
   */
  @NotNull
  public List<EventimNplCategory> getNplCategoryList() {
    if (vbSave == null) throw new IllegalStateException("read first");
    return nplCategoryList;
  }

  public void setSplId(long splId) {
    if (vbSave == null) throw new IllegalStateException("read first");
    vbSave.setSplId(splId);
  }
}
