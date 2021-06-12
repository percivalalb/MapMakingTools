package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

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
    public CompoundNBT write(CompoundNBT nbt) {
        return nbt;
    }

    @Override
    public PacketBuffer write(PacketBuffer buf) {
        buf.writeBoolean(false);
        buf.writeBoolean(false);
        return buf;
    }

    public int[] getDimensions() {
        return null;
    }
}
