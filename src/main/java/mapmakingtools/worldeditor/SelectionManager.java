package mapmakingtools.worldeditor;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.worldeditor.ISelection;
import mapmakingtools.network.PacketSelectionPoints;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

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

    public static SelectionManager read(CompoundNBT nbt, Runnable markDirty) {
        SelectionManager selectionManager = new SelectionManager(markDirty);

        if (!nbt.contains("points", Constants.NBT.TAG_LIST)) {
            return selectionManager;
        }

        ListNBT pointsList = nbt.getList("points", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < pointsList.size(); i++) {
            CompoundNBT pointNBT = pointsList.getCompound(i);

            if (!pointNBT.hasUUID("player_uuid")) {
                continue;
            }

            UUID uuid = pointNBT.getUUID("player_uuid");
            Selection selection = Selection.read(pointNBT);

            selectionManager.POSITION.put(uuid, selection);
        }

        return selectionManager;
    }

    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT pointsList = new ListNBT();

        for (Map.Entry<UUID, ISelection> entry : this.POSITION.entrySet()) {
            CompoundNBT pointNBT = new CompoundNBT();

            entry.getValue().write(pointNBT);
            pointNBT.putUUID("player_uuid", entry.getKey());

            pointsList.add(pointNBT);
        }

        nbt.put("points", pointsList);

        return nbt;
    }

    public ISelection get(PlayerEntity player) {
        return this.POSITION.getOrDefault(player.getUUID(), EmptySelection.INSTANCE);
    }

    /**
     * Sets the primary selection position for the given player.
     *
     * @param player The player
     * @param pos The position to set to
     * @return If the position changed
     */
    public boolean setPrimary(PlayerEntity player, @Nullable BlockPos pos) {
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
    public boolean setSecondary(PlayerEntity player, @Nullable BlockPos pos) {
        UUID uuid = player.getUUID();

        Selection selection = (Selection) this.POSITION.computeIfAbsent(uuid, (k) -> new Selection());
        if (Objects.equals(selection.points[1], pos)) {
            return false;
        }
        selection.points[1] = pos.immutable();
        this.markDirty.run();
        return true;
    }

    public void sync(PlayerEntity player) {
        MapMakingTools.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                new PacketSelectionPoints(this.get(player)));
    }
}
