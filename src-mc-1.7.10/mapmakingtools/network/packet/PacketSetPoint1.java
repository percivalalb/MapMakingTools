package mapmakingtools.network.packet;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.BlockPos;
import mapmakingtools.tools.ClientData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint1 extends AbstractClientMessage {
	
	public BlockPos pos;
	
	public PacketSetPoint1() {}
	public PacketSetPoint1(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		if(buffer.readBoolean())
			this.pos = BlockPos.fromLong(buffer.readLong());
	}
	
	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(this.pos != null);
		if(this.pos != null)
			buffer.writeLong(this.pos.toLong());
	}
	
	@Override
	public IMessage process(EntityPlayer player, Side side) {
		ClientData.playerData.setFirstPoint(this.pos);
		return null;
	}

}
