package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.MapMakingTools;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketEditBlock {
	
	public BlockPos pos;
	
	public PacketEditBlock(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(PacketEditBlock msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
	}
	
	public static PacketEditBlock decode(PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		return new PacketEditBlock(pos);
	}
	
	public static class Handler {
        public static void handle(final PacketEditBlock msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		if(!player.world.isRemote) {
        			MapMakingTools.LOGGER.info("Logging times");
        			//TODO player.openGui(MapMakingTools.INSTANCE, CommonProxy.ID_FILTER_BLOCK, player.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
        		}
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
