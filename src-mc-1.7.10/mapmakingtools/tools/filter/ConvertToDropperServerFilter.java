package mapmakingtools.tools.filter;

import mapmakingtools.api.interfaces.IFilterServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import mapmakingtools.tools.BlockPos;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ConvertToDropperServerFilter extends IFilterServer {

	@Override
	public boolean isApplicable(EntityPlayer player, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
		if(tile != null && tile.getClass() == TileEntityDispenser.class)
			return true;
		return false;
	}
}
