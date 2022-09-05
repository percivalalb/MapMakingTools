package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

public final class EmptySelection implements ISelection {

    public static final EmptySelection INSTANCE = new EmptySelection();

    private EmptySelection() {}

    @Nullable
    @Override
    public BlockPos getPrimaryPoint() {
        return null;
    }

    @Nullable
    @Override
    public BlockPos getSecondaryPoint() {
        return null;
    }

    @Override
    public boolean isSet() {
        return false;
    }

    @Override
    public long getChangeTime() {
        return 0;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {
        return nbt;
    }

    @Override
    public FriendlyByteBuf write(FriendlyByteBuf buf) {
        buf.writeBoolean(false);
        buf.writeBoolean(false);
        return buf;
    }

    public int[] getDimensions() {
        return null;
    }
}
