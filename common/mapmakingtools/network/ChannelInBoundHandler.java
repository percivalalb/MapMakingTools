package mapmakingtools.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.network.packet.MMTPacket;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author ProPercivalalb
 */
@ChannelHandler.Sharable
public class ChannelInBoundHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FMLProxyPacket fmlPacket = (FMLProxyPacket)msg;
		byte[] data = fmlPacket.payload().array();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		int id = dis.readInt();
		PacketType type = PacketType.getPacketFromId(id);
		
		EntityPlayer player;
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if(side == Side.CLIENT)
			player = ClientHelper.mc.thePlayer;
		else {
			int playerId = dis.readInt();
			int dimId = dis.readInt();
			player = (EntityPlayer)DimensionManager.getWorld(dimId).getEntityByID(playerId);
		}
		
		MMTPacket packet = type.createInstance();
		packet.read(dis);
		
		packet.execute(player);
		super.channelRead(ctx, msg);
	}
}