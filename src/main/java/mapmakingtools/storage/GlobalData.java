package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class GlobalData extends WorldSavedData {

    public GlobalData(String name) {
        super(Constants.STORAGE_GLOBAL);
    }

    @Override
    public void load(CompoundNBT nbt) {

    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        return null;
    }
}
