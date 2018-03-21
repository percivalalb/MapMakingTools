package mapmakingtools.tools;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
@SideOnly(Side.CLIENT)
public class ClientData {

	public static PlayerData playerData = new PlayerData(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID());
}
