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
public class FlippedVanillaTrack implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList(5, 7, 2, 9, 4, 8, 3, 6);
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		DIRECTIONS = Arrays.asList(5, 7, 3, 8, 4, 6, 2, 9);
		if(flipMode == 0)
			return;
		if(meta <= 1)
			return;
		
			int indexOf = DIRECTIONS.indexOf(meta);
			int direction = (indexOf - indexOf % 2) / 2;
			int type = indexOf % 2;
				
			//if(flipMode == 1 && (direction == 0 || direction == 2)) 
				//return;
			//if(flipMode == 2 && (direction == 1 || direction == 3)) 
				//return;
				
			direction += 2;
			direction %= 4;
						
			world.setBlockMetadataWithNotify(x, y, z, DIRECTIONS.get(direction * 2 + type), 2);
		//}
	}
}
