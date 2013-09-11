package mapmakingtools.api;

import mapmakingtools.core.util.CachedBlockPlacement;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IPasteFlip {
	
	public void onFlip(CachedBlockPlacement cache, World world, int x, int y, int z, int flipMode);
}
