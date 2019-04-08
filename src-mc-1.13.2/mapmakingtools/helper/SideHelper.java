package mapmakingtools.helper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class SideHelper {

	public static boolean isClient() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}
	
	public static boolean isServer() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}
}
