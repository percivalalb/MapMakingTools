package mapmakingtools.handler;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author ProPercivalalb
 */
public class WordSave {
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		//TODO if(event.getWorld().provider.getDimension() == 0 && !event.getWorld().isRemote)
		//	WorldData.read(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldSave(WorldEvent.Save event) {
		//TODO if(event.getWorld().provider.getDimension() == 0 && !event.getWorld().isRemote)
		//	WorldData.save(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		
	}
}
