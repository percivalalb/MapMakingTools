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
public class RotationButton implements IPasteRotate {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(3, 11, 2, 10, 4, 12, 1, 9);
	
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
