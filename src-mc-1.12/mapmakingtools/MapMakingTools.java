package mapmakingtools;

import mapmakingtools.handler.ActionHandler;
import mapmakingtools.handler.CommandHandler;
import mapmakingtools.handler.ConfigurationHandler;
import mapmakingtools.handler.EntityJoinWorldHandler;
import mapmakingtools.handler.PlayerTrackerHandler;
import mapmakingtools.handler.WorldSaveHandler;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools INSTANCE;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy PROXY;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
    	PROXY.onPreLoad();
    	PacketDispatcher.registerPackets();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, PROXY);
    	MinecraftForge.EVENT_BUS.register(new ActionHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldSaveHandler());
    	MinecraftForge.EVENT_BUS.register(new EntityJoinWorldHandler());
    	MinecraftForge.EVENT_BUS.register(new PlayerTrackerHandler());
    	PROXY.registerFilters();
    	PROXY.registerRotation();
    	PROXY.registerItemAttribute();
    	PROXY.registerForceKill();
    	PROXY.registerHandlers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	PROXY.onPostLoad();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        CommandHandler.initCommands(event);
    }
	
}
