package mapmakingtools;

import mapmakingtools.item.ItemEdit;
import mapmakingtools.item.ItemWrench;
import mapmakingtools.lib.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author ProPercivalalb
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
	
	
	@ObjectHolder(Reference.MOD_ID + ":edit_item")
    public static Item EDIT_ITEM;
	
	@ObjectHolder(Reference.MOD_ID + ":wrench")
    public static Item WRENCH;
	
	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemEdit().setRegistryName(Reference.MOD_ID, "edit_item"));
		event.getRegistry().register(new ItemWrench().setRegistryName(Reference.MOD_ID, "wrench"));
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void setItemModels(ModelRegistryEvent event) {
    	//ModelHelper.setModel(ModItems.EDIT_ITEM, 0, "mapmakingtools:edit_item");
    	//ModelHelper.setModel(ModItems.EDIT_ITEM, 1, "mapmakingtools:wrench");
	}
}