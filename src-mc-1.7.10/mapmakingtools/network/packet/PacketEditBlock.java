package mapmakingtools.network.packet;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacketPos;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.BlockPos;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketEditBlock extends IPacketPos {
	
	public PacketEditBlock() {}
	public PacketEditBlock(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void execute(EntityPlayer player) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_BLOCK, player.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ());
	}

}
