package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class WorldData extends SavedData {

    private CommandTracker lastCommand;

    public WorldData() {
        super(Constants.STORAGE_WORLD);
        this.lastCommand = new CommandTracker(this::setDirty);
    }

    public static WorldData get(Level world) {
        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException(String.format("Tried to access %s data on client.", Constants.STORAGE_WORLD));
        }

        return WorldData.get(world.getServer());
    }

    public static WorldData get(MinecraftServer server) {
        return server
                .getLevel(Level.OVERWORLD)
                .getDataStorage()
                .computeIfAbsent(WorldData::new, Constants.STORAGE_WORLD);
    }

    public CommandTracker getCommandTracker() {
        return this.lastCommand;
    }

    @Override
    public void load(CompoundTag nbt) {
        this.lastCommand = CommandTracker.read(nbt, this::setDirty);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        this.lastCommand.write(nbt);
        return nbt;
    }
}
