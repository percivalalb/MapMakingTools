package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
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
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		this.entityId = bytes.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(bytes);
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		bytes.writeInt(this.entityId);
		PacketHelper.writeNBTTagCompound(this.tagCompound, bytes);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.theWorld;
		Entity entity = world.getEntityByID(this.entityId);
		
		if(entity == null)
			return;
		
		entity.readFromNBT(this.tagCompound);
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketEditEntity(entity));
	}

}
