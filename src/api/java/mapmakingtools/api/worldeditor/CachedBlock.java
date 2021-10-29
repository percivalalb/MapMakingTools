package mapmakingtools.api.worldeditor;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class CachedBlock {

    private final BlockState state;
    private final CompoundNBT tag;

    public CachedBlock(BlockState state, @Nullable CompoundNBT nbt) {
        this.state = state;
        this.tag = nbt;
    }

    public void place(World world, BlockPos pos) {
        // Clear the last tile entity
        IClearable.tryClear(world.getBlockEntity(pos));

        if (world.setBlock(pos, this.state, Constants.BlockFlags.BLOCK_UPDATE)) {
            if (this.tag != null) {
                TileEntity tileentity = world.getBlockEntity(pos);
                if (tileentity != null) {
                    CompoundNBT compoundnbt = this.tag.copy();
                    compoundnbt.putInt("x", pos.getX());
                    compoundnbt.putInt("y", pos.getY());
                    compoundnbt.putInt("z", pos.getZ());
                    tileentity.load(this.state, compoundnbt);
                }
            }
        }
    }

    public static CachedBlock read(CompoundNBT nbt) {
        BlockState state = NBTUtil.readBlockState(nbt);
        CompoundNBT tileNBT = null;
        if (nbt.contains("tag", Constants.NBT.TAG_COMPOUND)) {
            tileNBT = nbt.getCompound("tag");
        }

        return new CachedBlock(state, tileNBT);
    }

    public CompoundNBT write(CompoundNBT nbt) {
        CompoundNBT state = nbt.merge(NBTUtil.writeBlockState(this.state));
        if (this.tag != null) {
            nbt.put("tag", this.tag);
        }

        return nbt;
    }

    public static CachedBlock from(IWorldReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        TileEntity tileEntity = world.getBlockEntity(pos);
        CompoundNBT nbt = tileEntity != null ? tileEntity.save(new CompoundNBT()) : null;
        return new CachedBlock(state, nbt);
    }
}
