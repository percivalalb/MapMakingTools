package mapmakingtools.handler;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class EntityJoinWorld {

	public static boolean shouldSpawnEntities = true;
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!EntityJoinWorld.shouldSpawnEntities)
			event.setCanceled(true);
	}
}
