package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.manager.FakeWorldManager;
import mapmakingtools.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateEntity extends IPacket {

	public int entityId;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateEntity() {}
	public PacketUpdateEntity(Entity entity) {
		this.entityId = entity.getEntityId();
		this.tagCompound = new NBTTagCompound();
		entity.writeToNBT(this.tagCompound);
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.entityId = packetbuffer.readInt();
		this.tagCompound = packetbuffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.entityId);
		packetbuffer.writeNBTTagCompoundToBuffer(this.tagCompound);
	}

	@Override
	public void execute(EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(this.entityId);
		
		if(entity == null)
			return;
		
		FakeWorldManager.putEntity(entity, this.tagCompound);
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketEditEntity(entity));
	}

}
