package mapmakingtools.proxy;

import net.minecraftforge.common.MinecraftForge;
import mapmakingtools.handler.KeyStateHandler;
import mapmakingtools.handler.ScreenRenderHandler;
import mapmakingtools.handler.WorldOverlayHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author ProPercivalalb
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void onPreLoad() {
		super.onPreLoad();
	}
	
	@Override
	public void registerHandlers() {
		ClientRegistry.registerKeyBinding(KeyStateHandler.keyItemEditor);
    	FMLCommonHandler.instance().bus().register(new KeyStateHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldOverlayHandler());
    	MinecraftForge.EVENT_BUS.register(new ScreenRenderHandler());
	}
}
