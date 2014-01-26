package mapmakingtools.helper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;

/**
 * @author ProPercivalalb
 */
public class ServerHelper {

	public static final MinecraftServer mcServer = MinecraftServer.getServer();
	
	public static boolean isServer() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
}
