package mapmakingtools.core.handler;

import mapmakingtools.core.util.DataStorage;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

public class WorldEventHandler {

	@ForgeSubscribe
	public void worldLoad(WorldEvent.Load event) {
		
	}
	
	@ForgeSubscribe
	public void worldSave(WorldEvent.Save event) {
		
	}
	
	@ForgeSubscribe
	public void worldUnload(WorldEvent.Unload event) {
		DataStorage.clearAll();
	}
}
