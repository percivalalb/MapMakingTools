package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class CachedCuboidArea implements ICachedArea {

    private final BlockPos pos;
    private final int width, height, depth; // x, y, z cords
    private final CachedBlock[] blocks;

    public CachedCuboidArea(BlockPos pos1, BlockPos pos2) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());

        this.pos = new BlockPos(minX, minY, minZ);
        this.width = Math.abs(pos2.getX() - pos1.getX()) + 1;
        this.height = Math.abs(pos2.getY() - pos1.getY()) + 1;
        this.depth = Math.abs(pos2.getZ() - pos1.getZ()) + 1;
        this.blocks = new CachedBlock[this.width * this.height * this.depth];
    }

    public CachedCuboidArea(IWorldReader worldIn, BlockPos pos1, BlockPos pos2) {
        this(pos1, pos2);
        int i = 0;
        for (BlockPos pos : BlockPos.betweenClosed(pos1, pos2)) {
            this.blocks[i++] = CachedBlock.from(worldIn, pos);
        }
    }

    @Override
    public void restore(World world) {
        int i = 0;
        // Assumes that getAllInBoxMutable generates BlockPos
        for (BlockPos pos : BlockPos.betweenClosed(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + this.width - 1, this.pos.getY()  + this.height - 1, this.pos.getZ() + this.depth - 1)) {
            this.blocks[i++].place(world, pos);
        }
    }

    @Override
    public int getSize() {
        return this.blocks.length;
    }

    @Override
    public ICachedArea cacheLive(IWorldReader world) {
        return new CachedCuboidArea(world, this.pos, new BlockPos(this.pos.getX() + this.width - 1, this.pos.getY() + this.height - 1, this.pos.getZ() + this.depth - 1));
    }

    public static CachedCuboidArea read(CompoundNBT nbt) {
        if (!nbt.contains("base_pos", Constants.NBT.TAG_LONG) || !nbt.contains("other_pos", Constants.NBT.TAG_LONG)) {
            // TODO Throw
        }

        BlockPos pos = BlockPos.of(nbt.getLong("base_pos"));
        BlockPos otherPos = BlockPos.of(nbt.getLong("other_pos"));
        CachedCuboidArea area = new CachedCuboidArea(pos, otherPos);
        ListNBT blockList = nbt.getList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundNBT blockNBT = blockList.getCompound(i);
            area.blocks[i] = CachedBlock.read(blockNBT);
        }
        return area;
    }

    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putLong("base_pos", this.pos.asLong());
        nbt.putLong("other_pos", BlockPos.asLong(this.pos.getX() + this.width - 1, this.pos.getY() + this.height - 1, this.pos.getZ() + this.depth - 1));
        ListNBT blockList = new ListNBT();
        for (int i = 0; i < this.blocks.length; i++) {
            blockList.add(this.blocks[i].write(new CompoundNBT()));
        }
        nbt.put("blocks", blockList);
        return nbt;
    }

    public static CachedCuboidArea from(IWorldReader world, ISelection selection) {
        // TODO when selection is not set
        return new CachedCuboidArea(world, selection.getPrimaryPoint(), selection.getSecondaryPoint());
    }

    public static CachedCuboidArea from(IWorldReader world, BlockPos pos1, BlockPos pos2) {
        // TODO when selection is not set
        return new CachedCuboidArea(world, pos1, pos2);
    }
}
