package mapmakingtools.api.manager;

import java.util.Hashtable;

import mapmakingtools.api.interfaces.IPasteFlip;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class FlippedManager {
	
	private static final Hashtable<String, IPasteFlip> map = new Hashtable<String, IPasteFlip>();
	
	public static void registerFlippedHandler(Block block, IPasteFlip handler) {
		map.put(Block.blockRegistry.getNameForObject(block), handler);
	}
	
	public static void onBlockFlipped(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		String utfId = Block.blockRegistry.getNameForObject(block);
		if(map.containsKey(utfId))
			map.get(utfId).onFlip(block, meta, tileEntity, world, x, y, z, flipMode);
	}
}
