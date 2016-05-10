package mapmakingtools.handler;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class EntityJoinWorldHandler {

	public static boolean shouldSpawnEntities = true;
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!EntityJoinWorldHandler.shouldSpawnEntities)
			event.setCanceled(true);
	}
}
