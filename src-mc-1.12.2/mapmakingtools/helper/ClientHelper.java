package mapmakingtools.helper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ProPercivalalb
 */
public class ClientHelper {

	public static Minecraft getClient() {
		return Minecraft.getMinecraft();
	}
}
