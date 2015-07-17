package mapmakingtools.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

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
