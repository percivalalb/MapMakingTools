package mapmakingtools.handler;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * @author ProPercivalalb
 */
public class PlayerTrackerHandler {

	@SubscribeEvent
	public void login(PlayerLoggedInEvent event) {
		if(!event.player.worldObj.isRemote) {
			PlayerData data = WorldData.getPlayerData(event.player);
			data.sendUpdateToClient();
		}
	}
	
	@SubscribeEvent
	public void login(PlayerChangedDimensionEvent event) {
		
	}
}
