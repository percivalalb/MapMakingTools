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
public class FlippedHopper implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(2, 5, 3, 4);
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		if(flipMode == 0) 
			return;
		if(meta < 2)
			return;
		
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
