package mapmakingtools.api.interfaces;

import mapmakingtools.api.enums.Rotation;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public interface IPasteRotate {

	public void onRotate(Block block, int meta, TileEntity tileEntity, World world, int x, int y, int z, Rotation rotation);

}
