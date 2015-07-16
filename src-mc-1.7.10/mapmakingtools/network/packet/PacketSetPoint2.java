package mapmakingtools.network.packet;

import mapmakingtools.network.IPacketPos;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.ClientData;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint2 extends IPacketPos {
	
	public PacketSetPoint2() {}
	public PacketSetPoint2(BlockPos pos) {
		super(pos);
	}
	
	@Override
	public void execute(EntityPlayer player) {
		ClientData.playerData.setSecondPoint(this.pos);
	}

}
