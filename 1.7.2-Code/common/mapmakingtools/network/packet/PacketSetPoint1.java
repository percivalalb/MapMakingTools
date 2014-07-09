package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.network.IPacket;
import mapmakingtools.tools.ClientData;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint1 extends IPacket {

	public int x;
	public int y;
	public int z;
	
	public PacketSetPoint1() {}
	public PacketSetPoint1(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void read(DataInputStream data) throws IOException {
		this.x = data.readInt();
		this.y = data.readInt();
		this.z = data.readInt();
	}

	@Override
	public void write(DataOutputStream data) throws IOException {
		data.writeInt(this.x);
		data.writeInt(this.y);
		data.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		FMLLog.info("Recive 1 , " + x + " " + y + " " + z);
		ClientData.playerData.setFirstPoint(x, y, z);
	}

}
