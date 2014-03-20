package mapmakingtools.api;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IPasteFlip {

	public void onFlip(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, int flipMode);

}
