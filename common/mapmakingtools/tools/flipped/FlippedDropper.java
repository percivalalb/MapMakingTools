package mapmakingtools.tools.flipped;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.api.IPasteFlip;

/**
 * @author ProPercivalalb
 */
public class FlippedDropper implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 5, 3, 4);
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		if(flipMode == 0) {
			if(meta == 1)
				world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			else if(meta == 0)
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
		else if(meta >= 2 && (flipMode == 1 || flipMode == 2)) {
			int indexOf = DIRECTIONS.indexOf(meta);
			int direction = indexOf;
			
			if(flipMode == 1 && (direction == 0 || direction == 2)) 
				return;
			if(flipMode == 2 && (direction == 1 || direction == 3)) 
				return;
			
			direction += 2;
			direction %= 4;
			
			world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction), 2);
		}
	}
}
