package mapmakingtools.network;

import java.io.IOException;

import mapmakingtools.tools.BlockPos;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public abstract class IPacketPos extends IPacket {

	public BlockPos pos;
	
	public IPacketPos() {};
	public IPacketPos(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		if(packetbuffer.readBoolean())
			this.pos = this.readBlockPos(packetbuffer);
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBoolean(this.pos != null);
		
		if(this.pos != null)
			this.writeBlockPos(packetbuffer, this.pos);
	}
}
