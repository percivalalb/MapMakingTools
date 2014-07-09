package mapmakingtools.tools.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import mapmakingtools.api.interfaces.IFilterServer;

/**
 * @author ProPercivalalb
 */
public class ChestSymmetrifyServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityChest)
			return true;
		return false;
	}
}
