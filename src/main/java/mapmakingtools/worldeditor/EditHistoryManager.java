package mapmakingtools.worldeditor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditHistoryManager {

    private final Runnable markDirty;
    private HashMap<UUID, EditHistory> POSITION = new HashMap<>();

    public EditHistoryManager(Runnable markDirty) {
        this.markDirty = markDirty;
    }

    public static EditHistoryManager read(CompoundTag nbt, Runnable markDirty) {
        EditHistoryManager editHistoryManager = new EditHistoryManager(markDirty);

        if (!nbt.contains("histories", Tag.TAG_LIST)) {
            return editHistoryManager;
        }

        ListTag pointsList = nbt.getList("histories", Tag.TAG_COMPOUND);

        for (int i = 0; i < pointsList.size(); i++) {
            CompoundTag historyNBT = pointsList.getCompound(i);

            if (!historyNBT.hasUUID("player_uuid")) {
                continue;
            }

            UUID uuid = historyNBT.getUUID("player_uuid");
            EditHistory selection = EditHistory.read(historyNBT);

            editHistoryManager.POSITION.put(uuid, selection);
        }

        return editHistoryManager;
    }

    public EditHistory get(Player player) {
        return this.POSITION.computeIfAbsent(player.getUUID(), k -> new EditHistory());
    }

    public CompoundTag write(CompoundTag nbt) {
        ListTag pointsList = new ListTag();

        for (Map.Entry<UUID, EditHistory> entry : this.POSITION.entrySet()) {
            CompoundTag historyNBT = new CompoundTag();

            entry.getValue().write(historyNBT);
            historyNBT.putUUID("player_uuid", entry.getKey());

            pointsList.add(historyNBT);
        }

        nbt.put("histories", pointsList);

        return nbt;
    }
}
