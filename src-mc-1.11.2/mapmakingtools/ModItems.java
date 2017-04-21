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

	public static Item editItem;
	
	public static void inti() {
		editItem = new ItemEdit().setUnlocalizedName("mapmakingtools:edititem");
		
		GameRegistry.register(editItem, new ResourceLocation(Reference.MOD_ID, "edit_item"));
	}
}
