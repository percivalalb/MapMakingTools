package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.ClientData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint {
	
	public BlockPos pos;
	
	public PacketSetPoint(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(PacketSetPoint msg, PacketBuffer buf) {
		buf.writeBoolean(msg.pos != null);
		if(msg.pos != null)
			buf.writeBlockPos(msg.pos);
	}
	
	public static PacketSetPoint decode(PacketBuffer buf) {
		boolean isNull = !buf.readBoolean();
		return new PacketSetPoint(isNull ? null : buf.readBlockPos());
	}
	
	public static class Handler {
        public static void handle(final PacketSetPoint msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	ClientData.playerData.setFirstPoint(msg.pos);
            });
            ClientData.playerData.setFirstPoint(msg.pos);
            ctx.get().setPacketHandled(true);
        }
	}
}
