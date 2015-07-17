package mapmakingtools.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

/**
 * @author ProPercivalalb
 */
@SideOnly(Side.CLIENT)
public class ClientData {

	public static PlayerData playerData = new PlayerData(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
}
