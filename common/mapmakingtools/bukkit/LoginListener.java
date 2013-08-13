package mapmakingtools.bukkit;

import mapmakingtools.core.util.DataStorage;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.Packet250CustomPayload;
import net.minecraft.server.v1_6_R2.PlayerConnection;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMapMakingTools.logger.info("Player join");
		EntityPlayer player = ((CraftPlayer)event.getPlayer()).getHandle();
		player.playerConnection = new PlayerConnection(player.server, player.playerConnection.networkManager, player);
		//player.playerConnection.sendPacket(new Packet250CustomPayload("CW|Init", "some text".getBytes()));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerChangedWorldEvent event) {
		BukkitMapMakingTools.logger.info("Player change world");
	}
	
	@EventHandler
	public void onPlayerChannel(PlayerChannelEvent event) {
		BukkitMapMakingTools.logger.info("Player change world");
	}
}
