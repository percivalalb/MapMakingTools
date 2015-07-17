package mapmakingtools.handler;

import mapmakingtools.lib.Constants;
import net.minecraftforge.common.config.Configuration;

/**
 * @author ProPercivalalb
 */
public class ConfigurationHandler {

	public static void loadConfig(Configuration config) {
		config.load();
		Constants.SHOULD_SHOW_BLOCK_ID_HELPER = config.get("general", "shouldShowBlockIdHelper", true, "Defines whether the block in the chat menu should show").getBoolean(true);
		config.save();
	 }
}
