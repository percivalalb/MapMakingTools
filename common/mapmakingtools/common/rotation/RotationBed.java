package mapmakingtools.common.rotation;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationBed implements IPasteRotate {

	private static int[] NORTH = new int[] {2, 10};
	private static int[] EAST = new int[] {3, 11};
	private static int[] SOUTH = new int[] {0, 8};
	private static int[] WEST = new int[] {1, 9};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
		boolean isTop = meta > 7;
        int direction = metaToListType(meta);
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][isTop ? 1 : 0], 2);
	}

	public int metaToListType(int meta) {
		int direction = meta & 3;
		if(direction == 0) return 2;
		if(direction == 1) return 3;
		if(direction == 2) return 0;
		return 1;
	}
}
