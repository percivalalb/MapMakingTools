package mapmakingtools.tools.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;
import mapmakingtools.api.interfaces.IFilterServer;

/**
 * @author ProPercivalalb
 */
public class ConvertToDropperServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile.getClass() == TileEntityDispenser.class)
			return true;
		return false;
	}
}
