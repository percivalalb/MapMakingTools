package mapmakingtools.api.manager;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.IPasteFlip;
import mapmakingtools.core.util.CachedBlockPlacement;
import net.minecraft.world.World;


/**
 * @author ProPercivalalb
 */
public class FlippedManager {
	
	private static final Map<Integer, IPasteFlip> map = new Hashtable<Integer, IPasteFlip>();
	
	public static void registerFlippedHandler(int blockId, IPasteFlip handler) {
		map.put(blockId, handler);
	}
	
	public static void onBlockFlipped(CachedBlockPlacement cache, int blockId, World world, int x, int y, int z, int rotation) {
		if(map.containsKey(blockId)) {
			map.get(blockId).onFlip(cache, world, x, y, z, rotation);
		}
	}
}
