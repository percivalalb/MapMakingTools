package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
import mapmakingtools.network.IPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	public void read(DataInputStream data) throws IOException {
		this.entityId = data.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(data);
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.entityId);
		PacketHelper.writeNBTTagCompound(this.tagCompound, data);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.thePlayer.worldObj;
		Entity entity = world.getEntityByID(this.entityId);
		
		if(entity == null)
			return;
		
		entity.readFromNBT(this.tagCompound);
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketEditEntity(entity));
	}

}
