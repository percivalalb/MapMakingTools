package mapmakingtools.handler;

import java.io.File;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mapmakingtools.tools.WorldData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @author ProPercivalalb
 */
public class WorldSaveHandler {
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote) {
			WorldData.read(DimensionManager.getCurrentSaveRootDirectory());
		}
	}
	
	@SubscribeEvent
	public void worldSave(WorldEvent.Save event) {
		if(event.world.provider.dimensionId == 0 && !event.world.isRemote)
			WorldData.save(DimensionManager.getCurrentSaveRootDirectory());
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload event) {
		
	}
}
