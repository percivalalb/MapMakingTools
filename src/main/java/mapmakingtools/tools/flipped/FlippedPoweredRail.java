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
public class FlippedPoweredRail implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(1, 2, 9, 10, 1, 5, 9, 13, 0, 3, 8, 11, 0, 4, 8, 12);
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		if(flipMode == 0) 
			return;
		
		int indexOf = DIRECTIONS.lastIndexOf(meta);
		int direction = (indexOf - indexOf % 4) / 4;
		int type = indexOf % 4;
		//TODO
		if(flipMode == 1 && (direction == 1 || direction == 3) && !(type == 1 || type == 3)) 
			return;
		if(flipMode == 2 && (direction == 0 || direction == 2) && !(type == 1 || type == 3)) 
			return;
		if(flipMode == 1 && (direction == 0 || direction == 2) && (type == 1 || type == 3)) 
			return;
		if(flipMode == 2 && (direction == 1 || direction == 3)  && (type == 1 || type == 3)) 
			return;
			
		direction += 2;
		direction %= 4;
					
		world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 4 + type), 2);
	}
}
