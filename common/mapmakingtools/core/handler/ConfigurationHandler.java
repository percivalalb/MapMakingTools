package mapmakingtools.core.handler;

import mapmakingtools.lib.Constants;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

/**
 * @author ProPercivalalb
 */
public class ConfigurationHandler {

	public static void loadConfig(Configuration config) {
		config.load();
		Constants.QUICK_BUILD_ITEM = config.get("general", "quickBuildItemId", Item.axeWood.itemID, "By default it is a wooden axe, you can change it but is not advised.").getInt(Item.axeWood.itemID);
		Constants.SHOULD_SHOW_BLOCK_ID_HELPER = config.get("general", "shouldShowBlockIdHelper", true, "Defines whether the block ids in the chat menu should show").getBoolean(true);
		config.save();
	 }
}
