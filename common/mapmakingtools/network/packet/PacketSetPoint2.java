package mapmakingtools.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import mapmakingtools.tools.ClientData;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint2 extends MMTPacket {

	public int x;
	public int y;
	public int z;
	
	public PacketSetPoint2() {}
	public PacketSetPoint2(int x, int y, int z) {
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
	public void write(DataOutputStream dos) throws IOException{
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		ClientData.playerData.setSecondPoint(x, y, z);
	}

}
