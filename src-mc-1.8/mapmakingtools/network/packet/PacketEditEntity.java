package mapmakingtools.network.packet;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public class PacketEditEntity extends IPacket {

	public int entityId;
	
	public PacketEditEntity() {}
	public PacketEditEntity(Entity entity) {
		this.entityId = entity.getEntityId();
	}
	
	@Override
	public void read(PacketBuffer packetbuffer) throws IOException {
		this.entityId = packetbuffer.readInt();
	}

	@Override
	public void write(PacketBuffer packetbuffer) throws IOException {
		packetbuffer.writeInt(this.entityId);
	}

	@Override
	public void execute(EntityPlayer player) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_ENTITY, player.worldObj, this.entityId, 0, 0);
	}

}
