package mapmakingtools.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class GlobalData extends SavedData {

    public GlobalData() {}

    public static GlobalData load(CompoundTag nbt) {
        GlobalData savedData = new GlobalData();
        return savedData;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        return compound;
    }
}
