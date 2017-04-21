package mapmakingtools.helper;

import net.minecraft.util.math.BlockPos;

public class BlockPosHelper {

	public static BlockPos add(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(pos1.getX() + pos2.getX(), pos1.getY() + pos2.getY(), pos1.getZ() + pos2.getZ());
    }
	
    public static BlockPos subtract(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ());
    }
	
}
