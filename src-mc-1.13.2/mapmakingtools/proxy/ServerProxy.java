package mapmakingtools.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ServerProxy extends CommonProxy {
	
	public ServerProxy() {
    	super();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
    }
	
	public void serverStarting(FMLDedicatedServerSetupEvent event) {
		
	}
}
