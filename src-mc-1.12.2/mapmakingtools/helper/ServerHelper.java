package mapmakingtools.helper;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public class ServerHelper {
	
	public static MinecraftServer getServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}
