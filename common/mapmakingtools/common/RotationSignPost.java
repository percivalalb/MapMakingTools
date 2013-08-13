package mapmakingtools.common;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationSignPost implements IPasteRotate {

	private static int[] NORTH = new int[] {0, 1, 2, 3};
	private static int[] EAST = new int[] {4, 5, 6, 7};
	private static int[] SOUTH = new int[] {8, 9, 10, 11};
	private static int[] WEST = new int[] {12, 13, 14, 15};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
        int direction = (meta - (meta % 4)) / 4;
        int state = meta & 3;
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
        LogHelper.logDebug("State: " + state);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][state], 2);
	}

}
