package mapmakingtools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mapmakingtools.config.ConfigHandler;
import mapmakingtools.lib.Reference;
import mapmakingtools.proxy.ClientProxy;
import mapmakingtools.proxy.CommonProxy;
import mapmakingtools.proxy.ServerProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(value = Reference.MOD_ID)
public class MapMakingTools {

	public static MapMakingTools INSTANCE;
    public static CommonProxy PROXY;
	
	public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);
	
	public MapMakingTools() {
		INSTANCE = this;
		ConfigHandler.init();
        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	}
}
