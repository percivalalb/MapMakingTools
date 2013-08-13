package mapmakingtools.common;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationAnvil implements IPasteRotate {

	private static int[] NORTH = new int[] {1};
	private static int[] EAST = new int[] {2};
	private static int[] SOUTH = new int[] {3};
	private static int[] WEST = new int[] {0};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
        int direction = metaToListType(meta);
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][0], 2);
	}

	public int metaToListType(int meta) {
		int direction = meta & 3;
		if(direction == 0) return 3;
		if(direction == 1) return 0;
		if(direction == 2) return 1;
		return 2;
	}
}
