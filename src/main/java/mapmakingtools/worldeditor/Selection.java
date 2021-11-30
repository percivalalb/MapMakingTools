package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class Selection implements ISelection {

    protected final BlockPos[] points = new BlockPos[2];

    @Nullable
    @Override
    public BlockPos getPrimaryPoint() {
        return this.points[0];
    }

    @Nullable
    @Override
    public BlockPos getSecondaryPoint() {
        return this.points[1];
    }

    @Override
    public boolean isSet() {
        return this.points[0] != null && this.points[1] != null;
    }

    @Override
    public long getChangeTime() {
        return 0;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        putBlockPos(nbt, "primary_pos", this.points[0]);
        putBlockPos(nbt, "secondary_pos", this.points[1]);
        return nbt;
    }

    @Override
    public FriendlyByteBuf write(FriendlyByteBuf buf) {
        writeBlockPos(buf, this.points[0]);
        writeBlockPos(buf, this.points[1]);
        return buf;
    }

    public static Selection read(CompoundTag nbt) {
        Selection selection = new Selection();
        selection.points[0] = getBlockPos(nbt, "primary_pos");
        selection.points[1] = getBlockPos(nbt, "secondary_pos");
        return selection;
    }

    public static Selection read(FriendlyByteBuf buf) {
        Selection selection = new Selection();
        selection.points[0] = readBlockPos(buf);
        selection.points[1] = readBlockPos(buf);
        return selection;
    }

    private static void putBlockPos(CompoundTag nbt, String key, @Nullable BlockPos pos) {
        if (pos != null) {
            nbt.putLong(key, pos.asLong());
        }
    }

    @Nullable
    private static BlockPos getBlockPos(CompoundTag nbt, String key) {
        if (!nbt.contains(key, Tag.TAG_LONG)) {
            return null;
        }

        return BlockPos.of(nbt.getLong(key));
    }

    private static void writeBlockPos(FriendlyByteBuf buf, @Nullable BlockPos pos) {
        if (pos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(pos);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Nullable
    private static BlockPos readBlockPos(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readBlockPos();
        }

        return null;
    }
}
