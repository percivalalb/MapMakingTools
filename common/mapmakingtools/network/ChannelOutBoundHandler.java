package mapmakingtools.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import mapmakingtools.helper.ClientHelper;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.packet.MMTPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

/**
 * @author ProPercivalalb
 */
public class ChannelOutBoundHandler {

	public static void sendPacketToClient(EntityPlayer player, MMTPacket packet) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
	    if(side != Side.SERVER)
	    	return;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(PacketType.getIdFromPacket(packet));
			packet.write(dos);
			
			Packet vanillaPacket = new S3FPacketCustomPayload(Reference.CHANNEL_NAME, bos.toByteArray());
			((EntityPlayerMP)player).playerNetServerHandler.func_147359_a(vanillaPacket);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPacketToServer(MMTPacket packet) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
	    if(side != Side.CLIENT)
	    	return;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(PacketType.getIdFromPacket(packet));
			dos.writeInt(ClientHelper.mc.thePlayer.func_145782_y());
			dos.writeInt(ClientHelper.mc.thePlayer.worldObj.provider.dimensionId);
			packet.write(dos);
			
			Packet vanillaPacket = new S3FPacketCustomPayload(Reference.CHANNEL_NAME, bos.toByteArray());
			ClientHelper.mc.func_147114_u().func_147297_a(vanillaPacket);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
