package mapmakingtools.worldeditor;

import mapmakingtools.api.worldeditor.CachedBlock;
import mapmakingtools.api.worldeditor.ICachedArea;
import mapmakingtools.api.worldeditor.ISelection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
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

    public CachedCuboidArea(LevelReader worldIn, BlockPos pos1, BlockPos pos2) {
        this(pos1, pos2);
        int i = 0;
        for (BlockPos pos : BlockPos.betweenClosed(pos1, pos2)) {
            this.blocks[i++] = CachedBlock.from(worldIn, pos);
        }
    }

    @Override
    public void restore(Level world) {
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
    public ICachedArea cacheLive(LevelReader world) {
        return new CachedCuboidArea(world, this.pos, new BlockPos(this.pos.getX() + this.width - 1, this.pos.getY() + this.height - 1, this.pos.getZ() + this.depth - 1));
    }

    public static CachedCuboidArea read(CompoundTag nbt) {
        if (!nbt.contains("base_pos", Constants.NBT.TAG_LONG) || !nbt.contains("other_pos", Constants.NBT.TAG_LONG)) {
            // TODO Throw
        }

        BlockPos pos = BlockPos.of(nbt.getLong("base_pos"));
        BlockPos otherPos = BlockPos.of(nbt.getLong("other_pos"));
        CachedCuboidArea area = new CachedCuboidArea(pos, otherPos);
        ListTag blockList = nbt.getList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockNBT = blockList.getCompound(i);
            area.blocks[i] = CachedBlock.read(blockNBT);
        }
        return area;
    }

    public CompoundTag write(CompoundTag nbt) {
        nbt.putLong("base_pos", this.pos.asLong());
        nbt.putLong("other_pos", BlockPos.asLong(this.pos.getX() + this.width - 1, this.pos.getY() + this.height - 1, this.pos.getZ() + this.depth - 1));
        ListTag blockList = new ListTag();
        for (int i = 0; i < this.blocks.length; i++) {
            blockList.add(this.blocks[i].write(new CompoundTag()));
        }
        nbt.put("blocks", blockList);
        return nbt;
    }

    public static CachedCuboidArea from(LevelReader world, ISelection selection) {
        // TODO when selection is not set
        return new CachedCuboidArea(world, selection.getPrimaryPoint(), selection.getSecondaryPoint());
    }

    public static CachedCuboidArea from(LevelReader world, BlockPos pos1, BlockPos pos2) {
        // TODO when selection is not set
        return new CachedCuboidArea(world, pos1, pos2);
    }
}
