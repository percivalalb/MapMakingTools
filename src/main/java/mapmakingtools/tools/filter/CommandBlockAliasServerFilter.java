package mapmakingtools.tools.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.api.IFilterServer;

/**
 * @author ProPercivalalb
 */
public class CommandBlockAliasServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = FakeWorldManager.getTileEntity(world, x, y, z);
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, x, y, z);
	}
}
