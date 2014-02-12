package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.api.FakeWorldManager;
import mapmakingtools.helper.ClientHelper;
import mapmakingtools.helper.PacketHelper;
import mapmakingtools.network.IPacket;
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
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
		this.tagCompound = PacketHelper.readNBTTagCompound(data);
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
		PacketHelper.writeNBTTagCompound(this.tagCompound, data);
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
