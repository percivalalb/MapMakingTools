package mapmakingtools.network.packet;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public abstract class IPacket {
	
	public void read(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		
	}
	
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) throws IOException {
		
	}
	
	public abstract void execute(EntityPlayer player);
}
