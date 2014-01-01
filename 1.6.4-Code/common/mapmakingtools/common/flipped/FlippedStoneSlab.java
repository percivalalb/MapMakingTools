package mapmakingtools.common.flipped;

import net.minecraft.world.World;
import mapmakingtools.api.IPasteFlip;
import mapmakingtools.core.util.CachedBlockPlacement;

/**
 * @author ProPercivalalb
 */
public class FlippedStoneSlab implements IPasteFlip {

	@Override
	public void onFlip(CachedBlockPlacement cache, World world, int x, int y, int z, int flipMode) {
		int meta = cache.getBlockMeta();
		if(flipMode == 0) {
			int newMeta = meta;
			
			if(meta >= 8)
				newMeta -= 8;
			if(meta <= 7)
				newMeta += 8;
			world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
		}
	}
}
