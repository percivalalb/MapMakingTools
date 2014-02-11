package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.entity.player.EntityPlayer;
import mapmakingtools.network.IPacket;
import mapmakingtools.tools.ClientData;

/**
 * @author ProPercivalalb
 */
public class PacketSetPoint2 extends IPacket {

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
		FMLLog.info("Recive 2 , " + x + " " + y + " " + z);
		ClientData.playerData.setSecondPoint(x, y, z);
	}

}
