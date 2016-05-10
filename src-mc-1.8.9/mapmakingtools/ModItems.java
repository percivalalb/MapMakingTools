package mapmakingtools;

import mapmakingtools.item.ItemEdit;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
