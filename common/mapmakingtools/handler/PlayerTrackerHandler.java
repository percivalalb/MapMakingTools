package mapmakingtools.handler;

import mapmakingtools.network.packet.PacketSetPoint1;
import mapmakingtools.network.packet.PacketSetPoint2;
import mapmakingtools.tools.PlayerData;
import mapmakingtools.tools.WorldData;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
