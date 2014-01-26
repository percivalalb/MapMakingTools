package mapmakingtools.tools.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import mapmakingtools.api.IFilterServer;

/**
 * @author ProPercivalalb
 */
public class MobTypeServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.func_147438_o(x, y, z);
		if(tile instanceof TileEntityMobSpawner)
			return true;
		return super.isApplicable(player, world, x, y, z);
	}
}
