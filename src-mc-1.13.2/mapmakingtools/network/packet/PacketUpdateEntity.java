package mapmakingtools.network.packet;

import java.util.function.Supplier;

import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.network.PacketHandler;
import mapmakingtools.tools.PlayerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateEntity {

	public int entityId;
	public NBTTagCompound tagCompound;
	public boolean onlyUpdate;
	
	public PacketUpdateEntity(Entity entity, boolean onlyUpdate) {
		this(entity.getEntityId(), entity.serializeNBT(), onlyUpdate);
	}
	
	public PacketUpdateEntity(int entityId, NBTTagCompound tagCompound, boolean onlyUpdate) {
		this.entityId = entityId;
		this.tagCompound = tagCompound;
		this.onlyUpdate = onlyUpdate;
	}
	

	public static void encode(PacketUpdateEntity msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
		buf.writeCompoundTag(msg.tagCompound);
		buf.writeBoolean(msg.onlyUpdate);
	}
	
	public static PacketUpdateEntity decode(PacketBuffer buf) {
		int entityId = buf.readInt();
		NBTTagCompound tagCompound = buf.readCompoundTag();
		boolean onlyUpdate = buf.readBoolean();
		return new PacketUpdateEntity(entityId, tagCompound, onlyUpdate);
	}
	
	public static class Handler {
        public static void handle(final PacketUpdateEntity msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
            	EntityPlayer player = ctx.get().getSender();
            	if(!PlayerAccess.canEdit(player))
        			return;
        		
        		Entity entity = player.world.getEntityByID(msg.entityId);
        		
        		if(entity == null)
        			return;
        		
        		FakeWorldManager.putEntity(entity, msg.tagCompound);
        		
        		//if(!msg.onlyUpdate)
        			PacketHandler.send(PacketDistributor.SERVER.noArg(), new PacketEditEntity(entity));
            });

            ctx.get().setPacketHandled(true);
        }
	}
}
