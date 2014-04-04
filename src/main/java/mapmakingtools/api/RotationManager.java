package mapmakingtools.api;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.IPasteRotate;
import mapmakingtools.tools.CachedBlock;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


/**
 * @author ProPercivalalb
 */
public class RotationManager {
	
	private static final Map<Integer, IPasteRotate> map = new Hashtable<Integer, IPasteRotate>();
	
	public static void registerRotationHandler(int blockId, IPasteRotate handler) {
		map.put(blockId, handler);
	}
	
	public static void onBlockRotation(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int rotation) {
		String utfId = Block.blockRegistry.getNameForObject(block);
		if(map.containsKey(utfId))
			map.get(utfId).onRotate(block, meta, tileEntity, world, x, y, z, rotation);
	}
}
