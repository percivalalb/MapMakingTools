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
public class RotationQuatzPillar implements IPasteRotate {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 5, 3, 4);
	
	@Override
	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		if(meta < 2) 
			return;
		int orientation = meta - 2;
        if(orientation == 0 || rotation == Rotation._180_) return;
		orientation = orientation == 1 ? 2 : 1;
		world.setBlockMetadataWithNotify(x, y, z, orientation + 2, 2);
	}	
}
