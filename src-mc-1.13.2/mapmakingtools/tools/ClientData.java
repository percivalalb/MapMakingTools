package mapmakingtools.tools;

import mapmakingtools.MapMakingTools;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author ProPercivalalb
 */
@OnlyIn(Dist.CLIENT)
public class ClientData {

	public static PlayerData playerData = new PlayerData(MapMakingTools.PROXY.getPlayerEntity().getUniqueID());
}
