package mapmakingtools.common;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationTrapdoor implements IPasteRotate {

	private static int[] NORTH = new int[] {0, 4, 8, 12};
	private static int[] EAST = new int[] {3, 7, 11, 15};
	private static int[] SOUTH = new int[] {1, 5, 9, 13};
	private static int[] WEST = new int[] {2, 6, 10, 14};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
		boolean isOpen = meta > 3;
        int direction = metaToListType(meta);
        int state = getStateFromMap(directions[direction], meta);
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][state], 2);
	}
	
	/**
	 * Gets the state 0 = bottom closed, 1 = bottom open, 2 = top closed, 3 = top open
	 */
	public int getStateFromMap(int[] map, int meta) {
		int stateCount = 0;
		for(int state : map) {
			if(state == meta) {
				return stateCount;
			}
			++stateCount;
		}
		return meta;
	}

	public int metaToListType(int meta) {
		int direction = meta & 3;
		if(direction == 0) return 0;
		if(direction == 1) return 2;
		if(direction == 2) return 3;
		return 1;
	}
}
