package mapmakingtools.api.manager;

import java.util.ArrayList;
import java.util.List;

import mapmakingtools.api.interfaces.IFilterClient;
import mapmakingtools.api.interfaces.IFilterServer;
import mapmakingtools.api.interfaces.IItemAttribute;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author ProPercivalalb
 */
public class ItemEditorManager {
	
	private static final List<IItemAttribute> list = new ArrayList<IItemAttribute>();
	
	public static void registerItemHandler(IItemAttribute handler) {
		list.add(handler);
	}
	
	public List<IItemAttribute> getItems(EntityPlayer player, ItemStack stack) {
		List<IItemAttribute> items = new ArrayList<IItemAttribute>();
		for(IItemAttribute item : list)
			if(item.isApplicable(player, stack))
				items.add(item);
		return items;
	}
}
