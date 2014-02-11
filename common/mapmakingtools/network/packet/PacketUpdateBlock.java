package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock extends IPacket {

	public int x, y, z;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tagCompound = new NBTTagCompound();
		tileEntity.writeToNBT(this.tagCompound);
	}
	
	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(bytes);
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		bytes.writeInt(this.x);
		bytes.writeInt(this.y);
		bytes.writeInt(this.z);
		PacketHelper.writeNBTTagCompound(this.tagCompound, bytes);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.theWorld;
		TileEntity tileEntity = world.getTileEntity(this.x, this.y, this.z);
		
		if(tileEntity == null)
			return;
		
		FakeWorldManager.putTileEntity(tileEntity, world, this.x, this.y, this.z, this.tagCompound);
		
		MapMakingTools.NETWORK_MANAGER.sendPacketToServer(new PacketEditBlock(this.x, this.y, this.z));
	}

}
