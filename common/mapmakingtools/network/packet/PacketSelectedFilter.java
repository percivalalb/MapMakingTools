package mapmakingtools.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mapmakingtools.container.ContainerFilter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ProPercivalalb
 */
public class PacketSelectedFilter extends IPacket {

	public int selected;
	
	public PacketSelectedFilter() {}
	public PacketSelectedFilter(int selected) {
		this.selected = selected;
	}

	@Override
	public void read(ChannelHandlerContext ctx, ByteBuf bytes)  {
		this.selected = bytes.readInt();
	}

	@Override
	public void write(ChannelHandlerContext ctx, ByteBuf bytes) {
		bytes.writeInt(selected);
	}

	@Override
	public void execute(EntityPlayer player) {
		if(player.openContainer instanceof ContainerFilter) {
			ContainerFilter container = (ContainerFilter)player.openContainer;
			container.setSelected(this.selected);
		}
	}

}
