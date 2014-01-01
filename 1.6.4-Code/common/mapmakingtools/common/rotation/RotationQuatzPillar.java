package mapmakingtools.common.rotation;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.util.CachedBlockPlacement;

/**
 * @author ProPercivalalb
 */
public class RotationQuatzPillar implements IPasteRotate {

	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
		if(meta < 2) return;
		int orientation = meta - 2;
        if(orientation == 0 || rotation == 0 || rotation == 180) return;
		orientation = orientation == 1 ? 2 : 1;
		world.setBlockMetadataWithNotify(x, y, z, orientation + 2, 2);
	}
		
}
