package mapmakingtools.worldeditor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandTracker {

    private Map<UUID, String> lastCommand = new HashMap<>();
    private final Runnable markDirty;

    public CommandTracker(Runnable markDirty) {
        this.markDirty = markDirty;
    }

    public String getLastCommand(Player entity) {
        return this.lastCommand.get(entity.getUUID());
    }

    public void setLastCommand(Player entity, String command) {
        this.lastCommand.put(entity.getUUID(), command);
        this.markDirty.run();
    }

    public static CommandTracker read(CompoundTag nbt, Runnable markDirty) {
        CommandTracker tracker = new CommandTracker(markDirty);

        if (!nbt.contains("commands", Constants.NBT.TAG_LIST)) {
            return tracker;
        }

        ListTag commandsList = nbt.getList("commands", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < commandsList.size(); i++) {
            CompoundTag commandNBT = commandsList.getCompound(i);

            if (!commandNBT.hasUUID("player_uuid")) {
                continue;
            }

            UUID uuid = commandNBT.getUUID("player_uuid");
            String lastCommand = commandNBT.getString("command");

            tracker.lastCommand.put(uuid, lastCommand);
        }

        return tracker;
    }

    public CompoundTag write(CompoundTag nbt) {
        ListTag commandsList = new ListTag();

        for (Map.Entry<UUID, String> entry : this.lastCommand.entrySet()) {
            CompoundTag pointNBT = new CompoundTag();

            pointNBT.putUUID("player_uuid", entry.getKey());
            pointNBT.putString("command", entry.getValue());

            commandsList.add(pointNBT);
        }

        nbt.put("commands", commandsList);

        return nbt;
    }
}
