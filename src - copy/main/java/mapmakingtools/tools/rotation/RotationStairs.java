package mapmakingtools.tools.rotation;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.api.enums.Rotation;
import mapmakingtools.api.interfaces.IPasteRotate;

/**
 * @author ProPercivalalb
 */
public class RotationStairs implements IPasteRotate {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 6, 1, 5, 3, 7, 0, 4);
	
	@Override
	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		int indexOf = DIRECTIONS.indexOf(meta);
		int direction = (indexOf - indexOf % 2) / 2;
		int type = indexOf % 2;
		
		direction += rotation.getMultiplier();
		direction %= 4;
				
		world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2 + type), 2);
	}	
}
