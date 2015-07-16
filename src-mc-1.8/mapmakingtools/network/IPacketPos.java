package mapmakingtools.network;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;

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
			this.pos = packetbuffer.readBlockPos();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeBoolean(this.pos != null);
		
		if(this.pos != null)
			packetbuffer.writeBlockPos(this.pos);
	}
}
