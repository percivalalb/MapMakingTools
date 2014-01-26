package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.MapMakingTools;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.util.SpawnerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

/**
 * @author ProPercivalalb
 */
public class PacketEditBlock extends MMTPacket {

	public int x, y, z;
	
	public PacketEditBlock() {}
	public PacketEditBlock(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void read(DataInputStream dis) throws IOException {
		this.x = dis.readInt();
		this.y = dis.readInt();
		this.z = dis.readInt();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		player.openGui(MapMakingTools.instance, CommonProxy.ID_FILTER_BLOCK, player.worldObj, this.x, this.y, this.z);
	}

}
