package mapmakingtools;

import mapmakingtools.client.model.ModelHelper;
import mapmakingtools.item.ItemEdit;
import mapmakingtools.lib.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
@EventBusSubscriber(modid = Reference.MOD_ID)
public class ModItems {
	
	public static Item EDIT_ITEM;
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
		EDIT_ITEM = new ItemEdit().setUnlocalizedName("mapmakingtools:edititem").setRegistryName(Reference.MOD_ID, "edit_item");
		
		event.getRegistry().register(EDIT_ITEM);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void setItemModels(ModelRegistryEvent event) {
    	ModelHelper.setModel(ModItems.EDIT_ITEM, 0, "mapmakingtools:edit_item");
    	ModelHelper.setModel(ModItems.EDIT_ITEM, 1, "mapmakingtools:wrench");
	}
}