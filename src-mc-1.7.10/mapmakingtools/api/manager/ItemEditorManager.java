package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mapmakingtools.api.interfaces.IItemAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author ProPercivalalb
 */
public class ItemEditorManager {
	
	private static final List<IItemAttribute> list = new ArrayList<IItemAttribute>();
	
	public static void registerItemHandler(IItemAttribute handler) {
		list.add(handler);
	}
	
	public static Hashtable<IItemAttribute, Boolean> getItems(EntityPlayer player, ItemStack stack) {
		Hashtable<IItemAttribute, Boolean> items = new Hashtable<IItemAttribute, Boolean>();
		for(IItemAttribute item : list)
			items.put(item, item.isApplicable(player, stack));
		return items;
	}
	
	public static List<IItemAttribute> getItemList(EntityPlayer player, ItemStack stack) {
		return list;
	}
}
