package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.network.AbstractMessage.AbstractClientMessage;
import mapmakingtools.tools.ClientData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

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
			this.pos = buffer.readBlockPos();
	}
	
	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(this.pos != null);
		if(this.pos != null)
			buffer.writeBlockPos(this.pos);
	}
	
	@Override
	public void process(EntityPlayer player, Side side) {
		ClientData.playerData.setFirstPoint(this.pos);
	}

}
