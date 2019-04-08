package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * @author ProPercivalalb
 */
public class PacketEditEntity {

	public int entityId;
	
	public PacketEditEntity(int entityId) {
		this.entityId = entityId;
	}
	
	public PacketEditEntity(Entity entity) {
		this(entity.getEntityId());
	}
	
	public static void encode(PacketEditEntity msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static PacketEditEntity decode(PacketBuffer buf) {
		int entityId = buf.readInt();
		return new PacketEditEntity(entityId);
	}
	
	public static class Handler {
        public static void handle(final PacketEditEntity msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		//TODO player.openGui(MapMakingTools.INSTANCE, CommonProxy.ID_FILTER_ENTITY, player.world, this.entityId, 0, 0);
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
