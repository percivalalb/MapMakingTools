package mapmakingtools.integration.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.NEIPlugin;
import mapmakingtools.lib.Reference;

/**
 * @author ProPercivalalb
 */
@NEIPlugin
public class NEIMapMakingToolsConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {	
		API.registerNEIGuiHandler(new NEIGuiHandler());
	}

	@Override
	public String getName() {
		return Reference.MOD_NAME;
	}

	@Override
	public String getVersion() {
		return "1";
	}

}
