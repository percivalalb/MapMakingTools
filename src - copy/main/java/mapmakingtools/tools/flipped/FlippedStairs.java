package mapmakingtools.tools.flipped;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mapmakingtools.api.interfaces.IPasteFlip;

/**
 * @author ProPercivalalb
 */
public class FlippedStairs implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 6, 1, 5, 3, 7, 0, 4);
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		int indexOf = DIRECTIONS.indexOf(meta);
		int direction = (indexOf - indexOf % 2) / 2;
		int type = indexOf % 2;
		
		if(flipMode == 0) {
			if(type == 1)
				world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2), 2);
			else if(type == 0)
				world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2 + 1), 2);
		}
		else if(flipMode == 1 || flipMode == 2) {
			
			if(flipMode == 1 && (direction == 0 || direction == 2)) 
				return;
			if(flipMode == 2 && (direction == 1 || direction == 3)) 
				return;
			
			direction += 2;
			direction %= 4;
					
			world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2 + type), 2);
		}

	}
}
