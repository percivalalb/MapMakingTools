package mapmakingtools.helper;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class ServerHelper {

	public static final MinecraftServer mcServer = MinecraftServer.getServer();
	
	public static boolean isServer() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
}
