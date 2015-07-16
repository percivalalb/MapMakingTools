package mapmakingtools.helper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class ClientHelper {

	public static final Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
}
