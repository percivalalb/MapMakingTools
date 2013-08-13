package mapmakingtools.api;

import mapmakingtools.core.util.CachedBlockPlacement;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IPasteRotate {
	
	public void onPaste(CachedBlockPlacement cache, World world, int x, int y, int z, int rotation);
}
