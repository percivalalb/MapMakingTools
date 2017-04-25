package mapmakingtools;

import mapmakingtools.item.ItemEdit;
import mapmakingtools.lib.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author ProPercivalalb
 */
public class ModItems {

	public static Item EDIT_ITEM;
	
	public static void inti() {
		EDIT_ITEM = new ItemEdit().setUnlocalizedName("mapmakingtools:edititem");
		
		GameRegistry.register(EDIT_ITEM, new ResourceLocation(Reference.MOD_ID, "edit_item"));
	}
}
