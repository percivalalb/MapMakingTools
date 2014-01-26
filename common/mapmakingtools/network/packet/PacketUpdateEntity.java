package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
import mapmakingtools.network.ChannelOutBoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateEntity extends MMTPacket {

	public int entityId;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateEntity() {}
	public PacketUpdateEntity(Entity entity) {
		this.entityId = entity.func_145782_y();
		this.tagCompound = new NBTTagCompound();
		entity.writeToNBT(this.tagCompound);
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		this.entityId = dis.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(dis);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException{
		dos.writeInt(this.entityId);
		PacketHelper.writeNBTTagCompound(this.tagCompound, dos);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.theWorld;
		Entity entity = world.getEntityByID(this.entityId);
		
		if(entity == null)
			return;
		
		entity.readFromNBT(this.tagCompound);
		
		ChannelOutBoundHandler.sendPacketToServer(new PacketEditEntity(entity));
	}

}
