package mapmakingtools.common.rotation;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationRedstoneComparator implements IPasteRotate {

	private static int[] NORTH = new int[] {0, 4, 8, 12};
	private static int[] EAST = new int[] {1, 5, 9, 13};
	private static int[] SOUTH = new int[] {2, 6, 10, 14};
	private static int[] WEST = new int[] {3, 7, 11, 15};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
        int direction = meta & 3;
        int state = (meta - (meta % 4)) / 4;
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
        LogHelper.logDebug("State: " + state);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][state], 2);
	}

}
