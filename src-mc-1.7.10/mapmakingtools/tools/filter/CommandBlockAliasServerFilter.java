package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.IFilterServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import mapmakingtools.tools.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class CommandBlockAliasServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
		if(tileEntity != null && tileEntity instanceof TileEntityCommandBlock)
			return true;
		return super.isApplicable(player, world, pos);
	}
}
