package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class GlobalData extends SavedData {

    public GlobalData(String name) {
        super(Constants.STORAGE_GLOBAL);
    }

    @Override
    public void load(CompoundTag nbt) {

    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        return null;
    }
}
