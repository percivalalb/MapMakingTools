package mapmakingtools.helper;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerHelper {

	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

}
