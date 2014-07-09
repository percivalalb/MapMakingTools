package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.network.IPacket;
import mapmakingtools.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

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
	public void read(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.entityId);
	}

	@Override
	public void execute(EntityPlayer player) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_ENTITY, player.worldObj, entityId, 0, 0);
	}

}
