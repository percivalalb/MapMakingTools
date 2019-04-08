package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.ClientData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint2 {
	
	public BlockPos pos;
	
	public PacketSetPoint2(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(PacketSetPoint2 msg, PacketBuffer buf) {
		buf.writeBoolean(msg.pos != null);
		if(msg.pos != null)
			buf.writeBlockPos(msg.pos);
	}
	
	public static PacketSetPoint2 decode(PacketBuffer buf) {
		boolean isNull = !buf.readBoolean();
		return new PacketSetPoint2(isNull ? null : buf.readBlockPos());
	}
	
	public static class Handler {
        public static void handle(final PacketSetPoint2 msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	ClientData.playerData.setSecondPoint(msg.pos);
            });
            ClientData.playerData.setSecondPoint(msg.pos);
            ctx.get().setPacketHandled(true);
        }
	}
}
