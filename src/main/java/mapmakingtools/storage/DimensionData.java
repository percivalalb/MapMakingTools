package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import mapmakingtools.worldeditor.EditHistoryManager;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class DimensionData extends SavedData {

    private SelectionManager selectionManager;
    private EditHistoryManager editHistoryManager;

    public DimensionData() {
        super(Constants.STORAGE_DIMENSION);
        // Defaults required since read is not called if no data exists
        this.selectionManager = new SelectionManager(this::setDirty);
        this.editHistoryManager = new EditHistoryManager(this::setDirty);
    }

    public static DimensionData get(Level world) {
        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException(String.format("Tried to access %s data on client.", Constants.STORAGE_DIMENSION));
        }

        return ((ServerLevel) world).getDataStorage()
                .computeIfAbsent(DimensionData::new, Constants.STORAGE_DIMENSION);
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public EditHistoryManager getEditHistoryManager() {
        return this.editHistoryManager;
    }

    @Override
    public void load(CompoundTag nbt) {
        this.selectionManager = SelectionManager.read(nbt.getCompound("selection"), this::setDirty);
        this.editHistoryManager = EditHistoryManager.read(nbt.getCompound("history"), this::setDirty);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.put("selection", this.selectionManager.write(new CompoundTag()));
        nbt.put("history", this.editHistoryManager.write(new CompoundTag()));
        return nbt;
    }
}
