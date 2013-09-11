package mapmakingtools.common.flipped;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteFlip;
import mapmakingtools.core.util.CachedBlockPlacement;

/**
 * @author ProPercivalalb
 */
public class FlippedChest implements IPasteFlip {

	private static int[] NORTH = new int[] {2};
	private static int[] EAST = new int[] {5};
	private static int[] SOUTH = new int[] {3};
	private static int[] WEST = new int[] {4};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onFlip(CachedBlockPlacement cache, World world, int x, int y, int z, int flipMode) {
		int meta = cache.getBlockMeta();
		if(flipMode == 1 || flipMode == 2) {
			int newMeta = meta;
			int[] currentDirection = null;
			int index = 0;
			int index2 = 0;
			for(int ind = 0; ind < directions.length; ++ind) {
				int[] arr = directions[ind];
				for(int ind2 = 0; ind2 < arr.length; ++ind2) {
					int metadata = arr[ind2];
					if(metadata == meta) {
						currentDirection = arr;
						index = ind;
						index2 = ind2;
					}
				}
			}
			
			if(flipMode == 1 && (index == 0 || index == 2)) 
				return;
			if(flipMode == 2 && (index == 1 || index == 3)) 
				return;
			
			index += 2;
			index = index & 3;
					
			newMeta = directions[index][index2];
			world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
		}
	}
}
