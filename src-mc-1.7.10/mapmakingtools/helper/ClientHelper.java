package mapmakingtools.helper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;

/**
 * @author ProPercivalalb
 */
public class ClientHelper {

	public static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
}
