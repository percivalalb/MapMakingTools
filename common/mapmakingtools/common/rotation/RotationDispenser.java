package mapmakingtools.common.rotation;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.helper.LogHelper;
import mapmakingtools.core.helper.QuickBuildHelper;
import mapmakingtools.core.util.CachedBlockPlacement;

public class RotationDispenser implements IPasteRotate {

	private static int[] NORTH = new int[] {2};
	private static int[] EAST = new int[] {5};
	private static int[] SOUTH = new int[] {3};
	private static int[] WEST = new int[] {4};
	private static int[][] directions = new int[][] {NORTH, EAST, SOUTH, WEST};
	
	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
		if(meta < 2) {
			world.setBlockMetadataWithNotify(x, y, z, meta, 2); 
			return;
		}
        int direction = metaToListType(meta);
        int newDirection = (direction + QuickBuildHelper.getTypeFromRotation(rotation)) & 3;
        LogHelper.logDebug("Old Direction: " + direction);
        LogHelper.logDebug("New Direction: " + newDirection);
		world.setBlockMetadataWithNotify(x, y, z, directions[newDirection][0], 2);
	}

	public int metaToListType(int meta) {
		int direction = (meta - 2) & 3;
		if(direction == 0) return 0;
		if(direction == 1) return 2;
		if(direction == 2) return 3;
		return 1;
	}
}