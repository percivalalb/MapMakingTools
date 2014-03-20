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
public class FlippedStairs implements IPasteFlip {

	/** Meta of block at different rotations, NORTH-EAST-SOUTH-WEST **/
	public static List<Integer> DIRECTIONS = Arrays.asList();
	
	@Override
	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode) {
		
	}
}
