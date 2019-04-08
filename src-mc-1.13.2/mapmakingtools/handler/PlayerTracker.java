package mapmakingtools.handler;

import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * @author ProPercivalalb
 */
public class PlayerTracker {

	@SubscribeEvent
	public void login(PlayerLoggedInEvent event) {
		if(!event.getPlayer().world.isRemote) {
			PlayerData data = WorldData.getPlayerData(event.getPlayer());
			data.sendUpdateToClient(event.getPlayer());
		}
	}
	
	@SubscribeEvent
	public void login(PlayerChangedDimensionEvent event) {
		
	}
}
