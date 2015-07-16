package mapmakingtools.network;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import mapmakingtools.MapMakingTools;
import mapmakingtools.tools.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

/**
 * @author ProPercivalalb
 */
public abstract class IPacket {
	
	public abstract void read(PacketBuffer packetbuffer) throws IOException;
	public abstract void write(PacketBuffer packetbuffer) throws IOException;
	
	public static BlockPos readBlockPos(PacketBuffer packetbuffer) {
        return BlockPos.fromLong(packetbuffer.readLong());
    }
	
	public static void writeBlockPos(PacketBuffer packetbuffer, BlockPos pos) {
		packetbuffer.writeLong(pos.toLong());
	}
	
	public abstract void execute(EntityPlayer player);
	
	public Packet getPacket() {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			return MapMakingTools.NETWORK_MANAGER.clientOutboundChannel.generatePacketFrom(this);
		else
			return MapMakingTools.NETWORK_MANAGER.serverOutboundChannel.generatePacketFrom(this);
	}
}
