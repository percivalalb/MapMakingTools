package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

/**
 * @author ProPercivalalb
 */
public class PacketTest extends IPacket {
	
	public PacketTest() {}
	
	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes)  {
		
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) {
		
	}

	@Override
	public void execute(EntityPlayer player) {
		FMLLog.info("Yay");
	}

}
