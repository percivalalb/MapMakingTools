package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketEditEntity extends MMTPacket {

	public int entityId;
	
	public PacketEditEntity() {}
	public PacketEditEntity(Entity entity) {
		this.entityId = entity.func_145782_y();
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		this.entityId = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.entityId);
	}

	@Override
	public void execute(EntityPlayer player) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_ENTITY, player.worldObj, entityId, 0, 0);
	}

}
