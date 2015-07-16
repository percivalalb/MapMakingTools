package mapmakingtools.network.packet;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.ClientData;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint1 extends IPacketPos {
	
	public PacketSetPoint1() {}
	public PacketSetPoint1(BlockPos pos) {
		super(pos);
	}
	
	@Override
	public void execute(EntityPlayer player) {
		ClientData.playerData.setFirstPoint(this.pos);
	}

}
