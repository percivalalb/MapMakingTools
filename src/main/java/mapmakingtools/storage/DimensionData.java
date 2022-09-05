package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import mapmakingtools.worldeditor.EditHistoryManager;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class DimensionData extends SavedData {

    private SelectionManager selectionManager;
    private EditHistoryManager editHistoryManager;

    public DimensionData() {
        this.selectionManager = new SelectionManager(this::setDirty);
        this.editHistoryManager = new EditHistoryManager(this::setDirty);
    }

    public static DimensionData get(Level world) {
        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException(String.format("Tried to access %s data on client.", Constants.STORAGE_DIMENSION));
        }

        return ((ServerLevel) world).getDataStorage()
                .computeIfAbsent(DimensionData::load, DimensionData::new, Constants.STORAGE_DIMENSION);
    }

    public static DimensionData load(CompoundTag nbt) {
        DimensionData savedData = new DimensionData();
        savedData.selectionManager = SelectionManager.read(nbt.getCompound("selection"), savedData::setDirty);
        savedData.editHistoryManager = EditHistoryManager.read(nbt.getCompound("history"), savedData::setDirty);
        return savedData;
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public EditHistoryManager getEditHistoryManager() {
        return this.editHistoryManager;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.put("selection", this.selectionManager.write(new CompoundTag()));
        nbt.put("history", this.editHistoryManager.write(new CompoundTag()));
        return nbt;
    }
}
