package mapmakingtools;

import cpw.mods.fml.common.registry.GameRegistry;
import mapmakingtools.item.ItemEdit;
import net.minecraft.item.Item;

/**
 * @author ProPercivalalb
 */
public class ModItems {

	public static Item editItem;
	
	public static void inti() {
		editItem = new ItemEdit().setUnlocalizedName("mapmakingtools:edititem");
		
		GameRegistry.registerItem(editItem, "edit_item");
	}
}
