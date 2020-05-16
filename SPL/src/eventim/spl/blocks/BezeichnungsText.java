package eventim.spl.blocks;

import eventim.spl.blocks.structures.IOChunk;
import eventim.spl.managers.*;
import eventim.spl.models.EventimString;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 22.07.2016.
 */
public final class BezeichnungsText extends AbstractBlock {
    private final long id;//4 байта
    private final int bezTextType;//4 байта
    private final int maxLen;//1 байт
    private final EventimString bezeichnung;//31 байт
    private final byte[] unknown;//Неизвестные байты
    private final BezeichnungsTextText subBlock;

    public BezeichnungsText(@NotNull IOChunk ioChunk, long version, @NotNull ReadManager readManager) {
        super(ioChunk, version);//6 байтов
        int startPosition = readManager.getPosition() - 6;
        id = readManager.getUnsignedInt();
        bezTextType = readManager.getInt();
        maxLen = readManager.getUnsignedByte();
        bezeichnung = readManager.getString(31);
        unknown = readManager.getBytes(getSize() + startPosition - readManager.getPosition());
        subBlock = new BezeichnungsTextText(new IOChunk(readManager), version, readManager);
    }

    /**
     * Записываем значения переменных в новый массив байтов
     */
    @Override
    public void write(@NotNull WriteManager writeManager) {
        super.write(writeManager);
        writeManager.putInt(id);
        writeManager.putInt(bezTextType);
        writeManager.putByte(maxLen);
        writeManager.putString(bezeichnung);
        writeManager.putBytes(unknown);
        if (subBlock != null) {
            subBlock.write(writeManager);
        }
    }
}
