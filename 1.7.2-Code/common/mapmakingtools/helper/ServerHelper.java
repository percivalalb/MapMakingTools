package mapmakingtools.helper;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class ServerHelper {

	public static final MinecraftServer mcServer = MinecraftServer.getServer();
	
	public static boolean isServer() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
}
