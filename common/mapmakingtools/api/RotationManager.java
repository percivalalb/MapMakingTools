package mapmakingtools.api;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.core.util.CachedBlockPlacement;
import net.minecraft.world.World;


/**
 * @author ProPercivalalb
 */
public class RotationManager {
	
	private static Map<Integer, IPasteRotate> map = new Hashtable<Integer, IPasteRotate>();
	
	public static void registerRotationHandler(int blockId, IPasteRotate handler) {
		map.put(blockId, handler);
	}
	
	public static void onBlockRotation(CachedBlockPlacement cache, int blockId, World world, int x, int y, int z, int rotation) {
		if(map.containsKey(blockId)) {
			map.get(blockId).onPaste(cache, world, x, y, z, rotation);
		}
	}
}
