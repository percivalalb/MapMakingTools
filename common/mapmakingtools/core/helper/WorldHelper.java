package mapmakingtools.core.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * @author ProPercivalalb
 */
public class WorldHelper {
	
	//Convenience functions that make for cleaner code mod side. They all drill down to #setBlock(World, Integer, Integer, Integer, Integer, Integer, Integer)
	
	/** No Block Updates **/
	public static boolean setBlockCheaply(World world, int x, int y, int z, int blockId) { return setBlockCheaply(world, x, y, z, blockId, 0); }
	public static boolean setBlockCheaply(World world, int x, int y, int z, int blockId, int blockMeta) { return setBlock(world, x, y, z, blockId, blockMeta, 2); }
	
	/** Block Updates **/
	public static boolean setBlock(World world, int x, int y, int z, int blockId) { return setBlock(world, x, y, z, blockId, 0); }
	public static boolean setBlock(World world, int x, int y, int z, int blockId, int blockMeta) { return setBlock(world, x, y, z, blockId, blockMeta, 3); }
	
	/**
	 * Sets the block using all the variables given
	 * @param world The world
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param z The Z coordinate
	 * @param blockId The Block ID 
	 * @param blockMeta The Block Metadata
	 * @param flag
	 * 		case 1: Will cause a block update.
	 * 		case 2: Will send the change to clients (You almost always want this)
	 * 		case 3: Will cause a block update and send the changes to the clients
	 * 		case 4: Prevents the block from being re-rendered.
	 * @return If the block was successfully placed
	 */
	public static boolean setBlock(World world, int x, int y, int z, int blockId, int blockMeta, int flag) {
		try {
			if(world == null) return false;
			if(Block.blocksList[blockId] != null && Block.blocksList[blockId] instanceof BlockTorch && blockMeta == 0) {
				return world.setBlock(x, y, z, blockId, 5, flag);
			}
			return world.setBlock(x, y, z, blockId, blockMeta, flag);
		}
		catch(Exception e) {
			e.printStackTrace();
			LogHelper.logWarning("Something caused a block to not be placed at "+x+", "+y+", "+z+", in dimension ");
			return false;
		}
	}
	/**
	//Custom setBlock from World.class
	public boolean setBlock(World world, int x, int y, int z, int blockId, int blockMeta, int flag) {
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
	        if (y < 0) {
	            return false;
	        }
	        else if (y >= 256) {
	            return false;
	        }
	        else {
	            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
	            int k1 = 0;
	            if ((flag & 1) != 0) {
	                k1 = chunk.getBlockID(x & 15, y, z & 15);
	            }
	            chunk.setBlockIDWithMetadata(par1, par2, par3, par4, par5);
	            boolean succesful = chunk.setBlockIDWithMetadata(x & 15, y, z & 15, blockId, blockMeta);
	            world.theProfiler.startSection("checkLight");
	            world.updateAllLightTypes(x, y, z);
	            world.theProfiler.endSection();
	            if (succesful) {
	                if ((flag & 2) != 0 && (!world.isRemote || (flag & 4) == 0)) {
	                    world.markBlockForUpdate(x, y, z);
	                }
	                if (!world.isRemote && (flag & 1) != 0) {
	                    world.notifyBlockChange(x, y, z, k1);
	                    Block block = Block.blocksList[blockId];
	                    if (block != null && block.hasComparatorInputOverride()) {
	                        world.func_96440_m(x, y, z, blockId);
	                    }
	                }
	            }
	            return flag;
	        }
	    }
	    else {
	        return false;
	    }
	}
	**/
	/**
	 * Gets the world time cycle from the world
	 * @param world The world that contains the time
	 * @return The time in longs from 0L to 24000L
	 */
	public static long getWorldTime(World world) {
		return world == null ? 0L : world.getWorldTime();
	}
	
	/**
	 * Gets the total world time cycle from the world
	 * @param world The world that contains the time
	 * @return The time in longs from 0L to 24000L
	 */
	public static long getTotalWorldTime(World world) {
		return world == null ? 0L : world.getTotalWorldTime();
	}
}
