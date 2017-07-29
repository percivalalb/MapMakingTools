package mapmakingtools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapmakingtools.config.ConfigurationHandler;
import mapmakingtools.lib.Reference;
import mapmakingtools.network.PacketDispatcher;
import mapmakingtools.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class MapMakingTools {

	@Instance(value = Reference.MOD_ID)
	public static MapMakingTools INSTANCE;
	
	@SidedProxy(clientSide = Reference.SP_CLIENT, serverSide = Reference.SP_SERVER)
    public static CommonProxy PROXY;
	
	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigurationHandler.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
    	PROXY.preInit(event);
    	PacketDispatcher.registerPackets();
    }
    
    @EventHandler
    public void onInit(FMLInitializationEvent event) {
    	PROXY.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	PROXY.postInit(event);
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        //Initialize the custom commands
        ModCommands.initCommands(event);
    }
	
}
