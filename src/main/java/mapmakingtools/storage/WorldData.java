package mapmakingtools.storage;

import mapmakingtools.lib.Constants;
import mapmakingtools.worldeditor.CommandTracker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class WorldData extends WorldSavedData {

    private CommandTracker lastCommand;

    public WorldData() {
        super(Constants.STORAGE_WORLD);
        this.lastCommand = new CommandTracker(this::setDirty);
    }

    public static WorldData get(World world) {
        if (!(world instanceof ServerWorld)) {
            throw new RuntimeException(String.format("Tried to access %s data on client.", Constants.STORAGE_WORLD));
        }

        return WorldData.get(world.getServer());
    }

    public static WorldData get(MinecraftServer server) {
        return server
                .getLevel(World.OVERWORLD)
                .getDataStorage()
                .computeIfAbsent(WorldData::new, Constants.STORAGE_WORLD);
    }

    public CommandTracker getCommandTracker() {
        return this.lastCommand;
    }

    @Override
    public void load(CompoundNBT nbt) {
        this.lastCommand = CommandTracker.read(nbt, this::setDirty);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        this.lastCommand.write(nbt);
        return nbt;
    }
}
