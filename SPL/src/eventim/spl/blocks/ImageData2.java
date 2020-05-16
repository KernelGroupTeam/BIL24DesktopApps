package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 02.08.2016.
 */
public final class ImageData2 extends AbstractBlock {
    private final byte[] bilddaten;//байтов в зависимости от значения размера блока в IOChunk

    public ImageData2(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
        super(ioChunk, version);//6 байтов
        bilddaten = readManager.getBytes(getSize() - 6);//Сохроняем все байты в количестве -6, т.к. размер блока IOChunk 6 байтов
    }

    /**
     * Записываем значения переменных в новый массив байтов
     */
    @Override
    public void write(@NotNull WriteManager writeManager) {
        super.write(writeManager);
        writeManager.putBytes(bilddaten);
    }
}
