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
		if(event.getWorld().provider.getDimension() == 0 && !event.getWorld().isRemote)
			WorldData.read(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldSave(WorldEvent.Save event) {
		if(event.getWorld().provider.getDimension() == 0 && !event.getWorld().isRemote)
			WorldData.save(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		
	}
}
