package mapmakingtools;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import mapmakingtools.api.FilterManager;
import mapmakingtools.handler.ActionHandler;
import mapmakingtools.handler.CommandHandler;
import mapmakingtools.handler.KeyStateHandler;
import mapmakingtools.handler.PlayerTrackerHandler;
import mapmakingtools.handler.WorldOverlayHandler;
import mapmakingtools.handler.WorldSaveHandler;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.NetworkManager;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.tools.filter.*;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.MOD_DEPENDENCIES)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools instance;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy proxy;
	
	public static NetworkManager NETWORK_MANAGER;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	instance = this;
    	proxy.onPreLoad();
    	ModItems.inti();
    	proxy.registerFilters();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	NETWORK_MANAGER = new NetworkManager();
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    	MinecraftForge.EVENT_BUS.register(new ActionHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldSaveHandler());
    	FMLCommonHandler.instance().bus().register(new PlayerTrackerHandler());
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
}
