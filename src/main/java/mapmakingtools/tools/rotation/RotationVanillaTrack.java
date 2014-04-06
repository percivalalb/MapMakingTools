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
public class RotationVanillaTrack implements IPasteRotate {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 6, 5, 9, 3, 7, 4, 8);
	
	@Override
	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation) {
		DIRECTIONS = Arrays.asList(2, 9, 5, 6, 3, 7, 4, 8);
		if(meta == 0 || meta == 1) {
			if(rotation == Rotation._090_ || rotation == Rotation._270_)
				world.setBlockMetadataWithNotify(x, y, z, meta == 0 ? 1 : 0, 2);
			return;
		}

		int indexOf = DIRECTIONS.indexOf(meta);
		int direction = (indexOf - indexOf % 2) / 2;
		int type = indexOf % 2;
		
		direction += rotation.getMultiplier();
		direction %= 4;
		
		world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2 + type), 2);
	}	
}
