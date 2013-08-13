package mapmakingtools.core.handler;

import mapmakingtools.core.util.DataStorage;
import mapmakingtools.network.PacketTypeHandler;
import mapmakingtools.network.packet.PacketBuildLeftClick;
import mapmakingtools.network.packet.PacketBuildRightClick;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

/**
 * @author ProPercivalalb
 */
public class ConnectionHandler implements IConnectionHandler {

	@Override
	public void playerLoggedIn(Player p, NetHandler netHandler, INetworkManager manager) {
		EntityPlayerMP player = (EntityPlayerMP)p;
		int secMinX = DataStorage.getSelectedPosFromPlayer(player)[0];
		int secMinY = DataStorage.getSelectedPosFromPlayer(player)[1];
		int secMinZ = DataStorage.getSelectedPosFromPlayer(player)[2];
		int secMaxX = DataStorage.getSelectedPosFromPlayer(player)[3];
		int secMaxY = DataStorage.getSelectedPosFromPlayer(player)[4];
		int secMaxZ = DataStorage.getSelectedPosFromPlayer(player)[5]; 
		PacketTypeHandler.populatePacketAndSendToClient(new PacketBuildRightClick(secMaxX, secMaxY, secMaxZ), player);
		PacketTypeHandler.populatePacketAndSendToClient(new PacketBuildLeftClick(secMinX, secMinY, secMinZ), player);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) { 
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { 
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) {
		// TODO Auto-generated method stub
		
	}

}
