package mapmakingtools.network;

import java.util.EnumMap;

import mapmakingtools.network.packet.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class NetworkManager {

    public final ChannelHandler channelHandler;
    private final FMLEmbeddedChannel clientOutboundChannel;
    private final FMLEmbeddedChannel serverOutboundChannel;
    
    public NetworkManager(String channelName) {
        this.channelHandler = new ChannelHandler();
        
        EnumMap<Side, FMLEmbeddedChannel> channelPair = NetworkRegistry.INSTANCE.newChannel(channelName, channelHandler);
        this.clientOutboundChannel = channelPair.get(Side.CLIENT);
        this.serverOutboundChannel = channelPair.get(Side.SERVER);
    }
    
    public void sendPacketToServer(IPacket packet) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.clientOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            this.clientOutboundChannel.writeOutbound(packet);
        }
    }
    
    public void sendPacketToPlayer(IPacket packet, EntityPlayer player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            this.serverOutboundChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            this.serverOutboundChannel.writeOutbound(packet);
        }
    }
}
