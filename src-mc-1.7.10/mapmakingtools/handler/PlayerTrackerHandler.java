package mapmakingtools.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;

/**
 * @author ProPercivalalb
 */
public class PlayerTrackerHandler {

	@SubscribeEvent
	public void login(PlayerLoggedInEvent event) {
		PlayerData data = WorldData.getPlayerData(event.player);
		data.sendUpdateToClient();
	}
	
	@SubscribeEvent
	public void login(PlayerChangedDimensionEvent event) {
		
	}
}
