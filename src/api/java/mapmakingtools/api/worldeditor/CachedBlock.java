package mapmakingtools.api.worldeditor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Clearable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class CachedBlock {

    private final BlockState state;
    private final CompoundTag tag;

    public CachedBlock(BlockState state, @Nullable CompoundTag nbt) {
        this.state = state;
        this.tag = nbt;
    }

    public void place(Level world, BlockPos pos) {
        // Clear the last tile entity
        Clearable.tryClear(world.getBlockEntity(pos));

        if (world.setBlock(pos, this.state, Constants.BlockFlags.BLOCK_UPDATE)) {
            if (this.tag != null) {
                BlockEntity tileentity = world.getBlockEntity(pos);
                if (tileentity != null) {
                    CompoundTag compoundnbt = this.tag.copy();
                    compoundnbt.putInt("x", pos.getX());
                    compoundnbt.putInt("y", pos.getY());
                    compoundnbt.putInt("z", pos.getZ());
                    tileentity.load(compoundnbt);
                }
            }
        }
    }

    public static CachedBlock read(CompoundTag nbt) {
        BlockState state = NbtUtils.readBlockState(nbt);
        CompoundTag tileNBT = null;
        if (nbt.contains("tag", Constants.NBT.TAG_COMPOUND)) {
            tileNBT = nbt.getCompound("tag");
        }

        return new CachedBlock(state, tileNBT);
    }

    public CompoundTag write(CompoundTag nbt) {
        CompoundTag state = nbt.merge(NbtUtils.writeBlockState(this.state));
        if (this.tag != null) {
            nbt.put("tag", this.tag);
        }

        return nbt;
    }

    public static CachedBlock from(LevelReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockEntity tileEntity = world.getBlockEntity(pos);
        CompoundTag nbt = tileEntity != null ? tileEntity.save(new CompoundTag()) : null;
        return new CachedBlock(state, nbt);
    }
}
