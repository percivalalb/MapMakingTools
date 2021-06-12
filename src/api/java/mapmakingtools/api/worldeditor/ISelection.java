package mapmakingtools.api.worldeditor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface ISelection {

    /**
     * @return The {@link BlockPos} of the primary point, null if not set
     */
    @Nullable
    public BlockPos getPrimaryPoint();

    /**
     * @return The {@link BlockPos} of the secondary point, null if not set
     */
    @Nullable
    public BlockPos getSecondaryPoint();

    public long getChangeTime();

    public CompoundNBT write(CompoundNBT nbt);

    public PacketBuffer write(PacketBuffer buf);

    default boolean anySet() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        return primary != null || secondary != null;
    }

    default boolean isSet() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        return primary != null && secondary != null;
    }

    @Nullable
    default int[] getDimensions() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        if (primary == null || secondary == null) {
            return null;
        }

        int width = Math.abs(secondary.getX() - primary.getX()) + 1;
        int height = Math.abs(secondary.getY() - primary.getY()) + 1;
        int depth = Math.abs(secondary.getZ() - primary.getZ()) + 1;
        return new int[] {width, height, depth};
    }

    @Nullable
    default AxisAlignedBB getPrimaryBB() {
        BlockPos primary = this.getPrimaryPoint();
        if (primary != null) {
            return new AxisAlignedBB(primary);
        }
        return null;
    }

    @Nullable
    default AxisAlignedBB getSecondaryBB() {
        BlockPos secondary = this.getSecondaryPoint();
        if (secondary != null) {
            return new AxisAlignedBB(secondary);
        }
        return null;
    }

    default int getMinX() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.min(primary.getX(), secondary.getX());
    }

    default int getMinY() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.min(primary.getY(), secondary.getY());
    }

    default int getMinZ() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.min(primary.getZ(), secondary.getZ());
    }

    default int getMaxX() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.max(primary.getX(), secondary.getX());
    }

    default int getMaxY() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.max(primary.getY(), secondary.getY());
    }

    default int getMaxZ() {
        BlockPos primary = this.getPrimaryPoint();
        BlockPos secondary = this.getSecondaryPoint();

        // TODO What if any are null
        return Math.max(primary.getZ(), secondary.getZ());
    }
}
