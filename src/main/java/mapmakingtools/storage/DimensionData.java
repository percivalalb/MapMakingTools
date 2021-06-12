package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import mapmakingtools.worldeditor.EditHistoryManager;
import mapmakingtools.worldeditor.SelectionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class DimensionData extends WorldSavedData {

    private SelectionManager selectionManager;
    private EditHistoryManager editHistoryManager;

    public DimensionData() {
        super(Constants.STORAGE_DIMENSION);
        // Defaults required since read is not called if no data exists
        this.selectionManager = new SelectionManager(this::markDirty);
        this.editHistoryManager = new EditHistoryManager(this::markDirty);
    }

    public static DimensionData get(World world) {
        if (!(world instanceof ServerWorld)) {
            throw new RuntimeException(String.format("Tried to access %s data on client.", Constants.STORAGE_DIMENSION));
        }

        return ((ServerWorld) world).getSavedData()
                .getOrCreate(DimensionData::new, Constants.STORAGE_DIMENSION);
    }

    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    public EditHistoryManager getEditHistoryManager() {
        return this.editHistoryManager;
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.selectionManager = SelectionManager.read(nbt.getCompound("selection"), this::markDirty);
        this.editHistoryManager = EditHistoryManager.read(nbt.getCompound("history"), this::markDirty);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("selection", this.selectionManager.write(new CompoundNBT()));
        nbt.put("history", this.editHistoryManager.write(new CompoundNBT()));
        return nbt;
    }
}
