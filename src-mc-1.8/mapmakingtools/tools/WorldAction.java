package mapmakingtools.tools;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldAction {

	 public static boolean setBlock(World world, BlockPos pos, IBlockState blockState, boolean notifyAndLight) {
	    Chunk chunk = world.getChunkFromBlockCoords(pos);

	    IBlockState lastState = chunk.setBlockState(pos, blockState);
	    world.markBlockForUpdate(pos);
	    if(notifyAndLight) {
	         world.markBlockForUpdate(pos);

	         world.notifyNeighborsRespectDebug(pos, lastState.getBlock());

	         if(blockState.getBlock().hasComparatorInputOverride())
	        	 world.updateComparatorOutputLevel(pos, blockState.getBlock());
	    	
	    }

	    return lastState != null;
	}

	public static boolean setBlockToAir(World world, BlockPos pos, boolean notifyAndLight) {
		return setBlock(world, pos, Blocks.air.getDefaultState(), notifyAndLight);
	}
}
