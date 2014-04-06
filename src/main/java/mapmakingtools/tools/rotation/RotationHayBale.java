package mapmakingtools.tools.rotation;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.api.IPasteRotate;
import mapmakingtools.api.Rotation;

/**
 * @author ProPercivalalb
 */
public class RotationHayBale implements IPasteRotate {

	@Override
	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		int orientation = meta & 12;
        int type = meta & 3;
        if(orientation == 0 || rotation == Rotation._180_) return;
		orientation = orientation == 4 ? 8 : 4;
		world.setBlockMetadataWithNotify(x, y, z, type | orientation, 2);
	}	
}
