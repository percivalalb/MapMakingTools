package mapmakingtools.worldeditor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditHistoryManager {

    private HashMap<UUID, EditHistory> POSITION = new HashMap<>();
    private final Runnable markDirty;

    public EditHistoryManager(Runnable markDirty) {
        this.markDirty = markDirty;
    }

    public EditHistory get(PlayerEntity player) {
        return this.POSITION.computeIfAbsent(player.getUniqueID(), k -> new EditHistory());
    }

    public static EditHistoryManager read(CompoundNBT nbt, Runnable markDirty) {
        EditHistoryManager editHistoryManager = new EditHistoryManager(markDirty);

        if (!nbt.contains("histories", Constants.NBT.TAG_LIST)) {
            return editHistoryManager;
        }

        ListNBT pointsList = nbt.getList("histories", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < pointsList.size(); i++) {
            CompoundNBT historyNBT = pointsList.getCompound(i);

            if (!historyNBT.hasUniqueId("player_uuid")) {
                continue;
            }

            UUID uuid = historyNBT.getUniqueId("player_uuid");
            EditHistory selection = EditHistory.read(historyNBT);

            editHistoryManager.POSITION.put(uuid, selection);
        }

        return editHistoryManager;
    }

    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT pointsList = new ListNBT();

        for (Map.Entry<UUID, EditHistory> entry : this.POSITION.entrySet()) {
            CompoundNBT historyNBT = new CompoundNBT();

            entry.getValue().write(historyNBT);
            historyNBT.putUniqueId("player_uuid", entry.getKey());

            pointsList.add(historyNBT);
        }

        nbt.put("histories", pointsList);

        return nbt;
    }
}
