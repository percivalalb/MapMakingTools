package mapmakingtools.network;

import java.io.IOException;

import mapmakingtools.MapMakingTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public abstract class IPacket {
	
	public abstract void read(PacketBuffer packetbuffer) throws IOException;
	public abstract void write(PacketBuffer packetbuffer) throws IOException;
	
	public abstract void execute(EntityPlayer player);
	
	public Packet getPacket() {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return MapMakingTools.NETWORK_MANAGER.clientOutboundChannel.generatePacketFrom(this);
		else
			return MapMakingTools.NETWORK_MANAGER.serverOutboundChannel.generatePacketFrom(this);
	}
}
