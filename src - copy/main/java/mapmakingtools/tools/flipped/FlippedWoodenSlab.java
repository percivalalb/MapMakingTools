package mapmakingtools.tools.flipped;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.api.interfaces.IPasteFlip;

/**
 * @author ProPercivalalb
 */
public class FlippedWoodenSlab implements IPasteFlip {
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
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
