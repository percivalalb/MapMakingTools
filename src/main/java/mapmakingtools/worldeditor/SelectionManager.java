package mapmakingtools.worldeditor;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.network.PacketSelectionPoints;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SelectionManager {

    // Even though signature is ISelection implementation guarantees only Selection
    // is present
    private HashMap<UUID, ISelection> POSITION = new HashMap<>();
    private final Runnable markDirty;

    public SelectionManager(Runnable markDirty) {
        this.markDirty = markDirty;
    }

    public static SelectionManager read(CompoundTag nbt, Runnable markDirty) {
        SelectionManager selectionManager = new SelectionManager(markDirty);

        if (!nbt.contains("points", Tag.TAG_LIST)) {
            return selectionManager;
        }

        ListTag pointsList = nbt.getList("points", Tag.TAG_COMPOUND);

        for (int i = 0; i < pointsList.size(); i++) {
            CompoundTag pointNBT = pointsList.getCompound(i);

            if (!pointNBT.hasUUID("player_uuid")) {
                continue;
            }

            UUID uuid = pointNBT.getUUID("player_uuid");
            Selection selection = Selection.read(pointNBT);

            selectionManager.POSITION.put(uuid, selection);
        }

        return selectionManager;
    }

    public CompoundTag write(CompoundTag nbt) {
        ListTag pointsList = new ListTag();

        for (Map.Entry<UUID, ISelection> entry : this.POSITION.entrySet()) {
            CompoundTag pointNBT = new CompoundTag();

            entry.getValue().write(pointNBT);
            pointNBT.putUUID("player_uuid", entry.getKey());

            pointsList.add(pointNBT);
        }

        nbt.put("points", pointsList);

        return nbt;
    }

    public ISelection get(Player player) {
        return this.POSITION.getOrDefault(player.getUUID(), EmptySelection.INSTANCE);
    }

    /**
     * Sets the primary selection position for the given player.
     *
     * @param player The player
     * @param pos The position to set to
     * @return If the position changed
     */
    public boolean setPrimary(Player player, @Nullable BlockPos pos) {
        UUID uuid = player.getUUID();

        Selection selection = (Selection) this.POSITION.computeIfAbsent(uuid, (k) -> new Selection());
        if (Objects.equals(selection.points[0], pos)) {
            return false;
        }
        selection.points[0] = pos.immutable();
        this.markDirty.run();
        return true;
    }

    /**
     * Sets the secondary selection position for the given player.
     *
     * @param player The player
     * @param pos The position to set to
     * @return If the position changed
     */
    public boolean setSecondary(Player player, @Nullable BlockPos pos) {
        UUID uuid = player.getUUID();

        Selection selection = (Selection) this.POSITION.computeIfAbsent(uuid, (k) -> new Selection());
        if (Objects.equals(selection.points[1], pos)) {
            return false;
        }
        selection.points[1] = pos.immutable();
        this.markDirty.run();
        return true;
    }

    public void sync(Player player) {
        MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                new PacketSelectionPoints(this.get(player)));
    }
}
