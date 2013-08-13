package mapmakingtools.common;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.core.util.CachedBlockPlacement;

/**
 * @author ProPercivalalb
 */
public class RotationHayBale implements IPasteRotate {

	@Override
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation) {
		int meta = cache.getBlockMeta();
		int orientation = meta & 12;
        final int type = meta & 3;
        if(orientation == 0 || rotation == 0 || rotation == 180) return;
		orientation = orientation == 4 ? 8 : 4;
		world.setBlockMetadataWithNotify(x, y, z, type | orientation, 2);
	}
}
