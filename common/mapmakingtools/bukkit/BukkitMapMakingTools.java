package mapmakingtools.bukkit;

import java.util.logging.Logger;

import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.NetworkManager;
import net.minecraft.server.v1_6_R2.Packet250CustomPayload;
import net.minecraft.server.v1_6_R2.PlayerConnection;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMapMakingTools extends JavaPlugin {

	public final static Logger logger = Logger.getLogger("Minecraft");
	public static BukkitMapMakingTools plugin;

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(String.format("%s %s (Bukkit) has been disabled!", pdfFile.getName(), pdfFile.getVersion()));
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(String.format("%s %s (Bukkit) has been enabled!", pdfFile.getName(), pdfFile.getVersion()));
		getServer().getPluginManager().registerEvents(new LoginListener(), this);
	}
}
