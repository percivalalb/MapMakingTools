package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

import mapmakingtools.tools.ClientData;

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
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) {
		bytes.writeInt(this.x);
		bytes.writeInt(this.y);
		bytes.writeInt(this.z);
	}

	@Override
	public void execute(EntityPlayer player) {
		FMLLog.info("Recive 1 , " + x + " " + y + " " + z);
		ClientData.playerData.setFirstPoint(x, y, z);
	}

}
