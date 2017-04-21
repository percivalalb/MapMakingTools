package mapmakingtools.handler;

import mapmakingtools.tools.WorldData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class WorldSaveHandler {
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		if(event.world.provider.getDimensionId() == 0 && !event.world.isRemote)
			WorldData.read(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldSave(WorldEvent.Save event) {
		if(event.world.provider.getDimensionId() == 0 && !event.world.isRemote)
			WorldData.save(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		
	}
}
