package mapmakingtools.api.manager;

import java.util.Hashtable;
import java.util.Map;

import mapmakingtools.api.enums.Rotation;
import mapmakingtools.api.interfaces.IPasteRotate;
import mapmakingtools.tools.CachedBlock;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


/**
 * @author ProPercivalalb
 */
public class RotationManager {
	
	private static final Map<String, IPasteRotate> map = new Hashtable<String, IPasteRotate>();
	
	public static void registerRotationHandler(Block block, IPasteRotate handler) {
		map.put(Block.blockRegistry.getNameForObject(block), handler);
	}
	
	public static void onBlockRotation(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		String utfId = Block.blockRegistry.getNameForObject(block);
		if(map.containsKey(utfId))
			map.get(utfId).onRotate(block, meta, tileEntity, world, x, y, z, rotation);
	}
}
