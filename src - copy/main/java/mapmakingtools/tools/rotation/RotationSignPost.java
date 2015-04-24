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
public class RotationSignPost implements IPasteRotate {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
	
	@Override
	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		int indexOf = DIRECTIONS.indexOf(meta);
		int direction = (indexOf - indexOf % 4) / 4;
		int type = indexOf % 4;
		
		direction += rotation.getMultiplier();
		direction %= 4;
				
		world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 4 + type), 2);
	}	
}
