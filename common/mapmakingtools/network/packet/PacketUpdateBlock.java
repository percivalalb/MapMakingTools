package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
import mapmakingtools.network.ChannelOutBoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class PacketUpdateBlock extends MMTPacket {

	public int x, y, z;
	public NBTTagCompound tagCompound;
	
	public PacketUpdateBlock() {}
	public PacketUpdateBlock(TileEntity tileEntity, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tagCompound = new NBTTagCompound();
		tileEntity.func_145841_b(this.tagCompound);
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		this.x = dis.readInt();
		this.y = dis.readInt();
		this.z = dis.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(dis);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException{
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
		PacketHelper.writeNBTTagCompound(this.tagCompound, dos);
	}

	@Override
	public void execute(EntityPlayer player) {
		World world = ClientHelper.mc.theWorld;
		TileEntity tileEntity = world.func_147438_o(this.x, this.y, this.z);
		
		if(tileEntity == null)
			return;
		
		tileEntity.func_145839_a(this.tagCompound);
		
		ChannelOutBoundHandler.sendPacketToServer(new PacketEditBlock(this.x, this.y, this.z));
	}

}
