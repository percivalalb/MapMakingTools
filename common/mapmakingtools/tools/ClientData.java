package mapmakingtools.tools;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
@SideOnly(Side.CLIENT)
public class ClientData {

	public static PlayerData playerData = new PlayerData(Minecraft.getMinecraft().thePlayer);
}
