package mapmakingtools.helper;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class ClientHelper {

	public static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
}
