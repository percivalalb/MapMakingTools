package mapmakingtools.core.addon;

import mapmakingtools.lib.Reference;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

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
		return Reference.MOD_VERSION;
	}

}
